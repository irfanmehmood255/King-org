package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.UserProfile
import com.example.data.model.UsernameRegistry
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class ArenaRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        ),
        Firebase.auth
    )

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    fun observeUserProfile(userId: String = requireUserId()): Flow<UserProfile?> = flow {
        val docRef = db.collection("users").document(userId)
        emitAll(
            docRef.snapshots()
                .map { snapshot ->
                    if (snapshot.exists()) {
                        snapshot.toObject(
                            UserProfile::class.java,
                            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                        )
                    } else {
                        null
                    }
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.GET, docRef.path)
                    throw error
                }
        )
    }

    suspend fun getUserProfile(userId: String = requireUserId()): Result<UserProfile?> = runCatching {
        val docRef = db.collection("users").document(userId)
        try {
            val snap = docRef.get().await()
            if (snap.exists()) {
                snap.toObject(
                    UserProfile::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )
            } else {
                null
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, docRef.path)
            throw e
        }
    }

    suspend fun createOrInitializeProfile(
        email: String,
        country: String,
        termsAccepted: Boolean
    ): Result<UserProfile> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
        try {
            val existingSnap = docRef.get().await()
            if (existingSnap.exists()) {
                val updates = mapOf(
                    "email" to email.trim(),
                    "country" to country.trim(),
                    "termsAccepted" to termsAccepted,
                    "emailVerified" to false,
                    "humanVerified" to false,
                    "humanVerificationMethod" to "none",
                    "onboardingStage" to "registered",
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.update(updates).await()
            } else {
                val newProfile = UserProfile(
                    userId = uid,
                    email = email.trim(),
                    country = country.trim(),
                    termsAccepted = termsAccepted,
                    emailVerified = false,
                    humanVerified = false,
                    humanVerificationMethod = "none",
                    username = "",
                    onboardingStage = "registered"
                )
                docRef.set(newProfile.toCreateMap()).await()
            }
            val updated = docRef.get().await()
            checkNotNull(
                updated.toObject(
                    UserProfile::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            throw e
        }
    }

    suspend fun markEmailVerified(): Result<Unit> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
        try {
            docRef.update(
                mapOf(
                    "emailVerified" to true,
                    "onboardingStage" to "email_verified",
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    suspend fun markHumanVerified(method: String): Result<Unit> = runCatching {
        val uid = requireUserId()
        require(method in listOf("face_scan", "fingerprint_scan")) { "Invalid human verification method" }
        val docRef = db.collection("users").document(uid)
        try {
            docRef.update(
                mapOf(
                    "humanVerified" to true,
                    "humanVerificationMethod" to method,
                    "onboardingStage" to "human_verified",
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    suspend fun isUsernameAvailable(rawUsername: String): Result<Boolean> = runCatching {
        val normalized = rawUsername.trim().lowercase()
        require(normalized.matches(Regex("^[a-z0-9_]{3,20}$"))) {
            "Username must be 3-20 characters using letters, numbers, or underscores."
        }
        val uid = requireUserId()
        val docRef = db.collection("usernames").document(normalized)
        try {
            val snap = docRef.get().await()
            if (!snap.exists()) {
                true
            } else {
                val existingOwner = snap.getString("userId")
                existingOwner == uid
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, docRef.path)
            throw e
        }
    }

    /**
     * Real-time Firestore listener for username availability while the user types on the
     * 'Set Your Username' screen.
     */
    fun observeUsernameAvailability(rawUsername: String): Flow<Boolean> = flow {
        val normalized = rawUsername.trim().lowercase()
        require(normalized.matches(Regex("^[a-z0-9_]{3,20}$"))) {
            "Username must be 3-20 characters using letters, numbers, or underscores."
        }
        val uid = requireUserId()
        val docRef = db.collection("usernames").document(normalized)
        emitAll(
            docRef.snapshots()
                .map { snap ->
                    if (!snap.exists()) {
                        true
                    } else {
                        snap.getString("userId") == uid
                    }
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.GET, docRef.path)
                    throw error
                }
        )
    }

    suspend fun claimUsernameAndCompleteOnboarding(rawUsername: String): Result<Unit> = runCatching {
        val display = rawUsername.trim()
        val normalized = display.lowercase()
        require(display.matches(Regex("^[a-zA-Z0-9_]{3,20}$"))) {
            "Username must be 3-20 characters and contain only letters, numbers, and underscores."
        }
        val uid = requireUserId()
        val usernameRef = db.collection("usernames").document(normalized)
        val userRef = db.collection("users").document(uid)

        try {
            val existingSnap = usernameRef.get().await()
            if (existingSnap.exists()) {
                val ownerUid = existingSnap.getString("userId")
                if (ownerUid != uid) {
                    throw IllegalStateException("Username '$display' is already taken. Please choose another.")
                }
                usernameRef.update(
                    mapOf(
                        "displayUsername" to display,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } else {
                val registry = UsernameRegistry(
                    userId = uid,
                    username = normalized,
                    displayUsername = display
                )
                usernameRef.set(registry.toCreateMap()).await()
            }

            userRef.update(
                mapOf(
                    "username" to display,
                    "onboardingStage" to "completed",
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, usernameRef.path)
            throw e
        }
    }
}
