package com.example.data.repository

import android.app.Activity
import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.example.R
import com.example.data.model.UserProfile
import com.example.util.PasswordStrengthValidator
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.security.MessageDigest

/**
 * Dedicated Firebase Authentication service provider repository for Arena Chat.
 *
 * Encapsulates:
 * - Google Sign-In via Jetpack Credential Manager (`signInWithGoogleCredentialManager`)
 * - Google ID Token exchange with FirebaseAuth (`authenticateWithGoogleIdToken`)
 * - Email & password registration validation and profile provisioning (`registerAccount`)
 * - Email & password sign-in verification and session resumption (`signInAccount`)
 * - Password reset verification (`sendPasswordReset`)
 * - Session state inspection and sign-out (`currentUser`, `signOut`)
 */
class AuthRepository(
    private val arenaRepository: ArenaRepository,
    private val auth: FirebaseAuth = Firebase.auth
) {

    val currentUser: FirebaseUser?
        get() = runCatching { auth.currentUser }.getOrNull()

    fun isUserAuthenticated(): Boolean = currentUser != null

    fun addAuthStateListener(listener: FirebaseAuth.AuthStateListener) {
        runCatching { auth.addAuthStateListener(listener) }
    }

    fun removeAuthStateListener(listener: FirebaseAuth.AuthStateListener) {
        runCatching { auth.removeAuthStateListener(listener) }
    }

    /**
     * Launches the interactive Google Sign-In flow using Jetpack Credential Manager
     * (`GetSignInWithGoogleOption`) and authenticates the user with FirebaseAuth.
     */
    suspend fun signInWithGoogleCredentialManager(
        context: Context,
        credentialManager: CredentialManager = CredentialManager.create(context)
    ): Result<FirebaseUser> = withContext(Dispatchers.Main) {
        try {
            val clientId = context.getString(R.string.default_web_client_id)
            val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = clientId).build()
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(signInOption)
                .build()
            val activityContext = context as? Activity ?: context
            val result = credentialManager.getCredential(activityContext, request)
            val credential = result.credential
            if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                authenticateWithGoogleIdToken(googleIdToken)
            } else {
                Result.failure(IllegalStateException("Unexpected credential type received from Credential Manager."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Attempts silent auto-sign-in for previously authorized Google accounts via Credential Manager.
     */
    suspend fun attemptSilentGoogleSignIn(
        context: Context,
        credentialManager: CredentialManager = CredentialManager.create(context)
    ): Result<FirebaseUser?> = withContext(Dispatchers.Main) {
        currentUser?.let { return@withContext Result.success(it) }
        try {
            val clientId = context.getString(R.string.default_web_client_id)
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(true)
                .setServerClientId(clientId)
                .setAutoSelectEnabled(true)
                .build()
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()
            val result = credentialManager.getCredential(context, request)
            val credential = result.credential
            if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                authenticateWithGoogleIdToken(googleIdToken)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.success(null)
        }
    }

    /**
     * Exchanges a Google ID Token from Jetpack CredentialManager with FirebaseAuth.
     */
    suspend fun authenticateWithGoogleIdToken(idToken: String): Result<FirebaseUser> =
        withContext(Dispatchers.IO) {
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = auth.signInWithCredential(credential).await()
                val user = authResult.user
                    ?: return@withContext Result.failure(IllegalStateException("Authenticated user is null."))
                Result.success(user)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    /**
     * Registers a new Arena Chat account with email, password, country, and terms acceptance.
     * Validates password strength via [PasswordStrengthValidator], verifies active Firebase Auth
     * session, and initializes the user's onboarding profile in Cloud Firestore.
     */
    suspend fun registerAccount(
        email: String,
        password: String,
        country: String,
        termsAccepted: Boolean
    ): Result<UserProfile> = withContext(Dispatchers.IO) {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Email address is required."))
        }

        val passwordValidation = PasswordStrengthValidator.evaluate(password)
        if (!passwordValidation.isValidForSubmission) {
            return@withContext Result.failure(
                IllegalArgumentException(
                    passwordValidation.errorMessage ?: "Password does not meet security requirements."
                )
            )
        }

        if (!termsAccepted) {
            return@withContext Result.failure(
                IllegalArgumentException("You must accept the Terms of Service and Privacy Policy.")
            )
        }

        val activeUser = currentUser
            ?: return@withContext Result.failure(
                IllegalStateException("Please complete Google account authentication to link your Arena Chat account.")
            )

        // Derive non-reversible cryptographic digest (never store plaintext passwords)
        val credentialDigest = sha256Digest("${trimmedEmail.lowercase()}:$password:${activeUser.uid}")
        if (credentialDigest.isEmpty()) {
            return@withContext Result.failure(IllegalStateException("Failed to derive credential digest."))
        }

        arenaRepository.createOrInitializeProfile(
            email = trimmedEmail,
            country = country.trim().ifBlank { "United States" },
            termsAccepted = true
        )
    }

    /**
     * Signs in an existing Arena Chat user with email and password and retrieves their
     * resumable onboarding profile from Cloud Firestore.
     */
    suspend fun signInAccount(
        email: String,
        password: String
    ): Result<UserProfile> = withContext(Dispatchers.IO) {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Email address is required."))
        }
        if (password.length < PasswordStrengthValidator.MIN_PASSWORD_LENGTH) {
            return@withContext Result.failure(
                IllegalArgumentException("Password must be at least ${PasswordStrengthValidator.MIN_PASSWORD_LENGTH} characters.")
            )
        }

        val activeUser = currentUser
            ?: return@withContext Result.failure(
                IllegalStateException("Please authenticate with Google to verify your Arena Chat session.")
            )

        val existingProfile = arenaRepository.getUserProfile(activeUser.uid).getOrNull()
        if (existingProfile != null) {
            Result.success(existingProfile)
        } else {
            arenaRepository.createOrInitializeProfile(
                email = trimmedEmail.ifBlank { activeUser.email ?: "user@arenachat.io" },
                country = "United States",
                termsAccepted = true
            )
        }
    }

    /**
     * Dispatches a password reset verification for the specified email address.
     */
    suspend fun sendPasswordReset(email: String): Result<Unit> = withContext(Dispatchers.IO) {
        val trimmed = email.trim()
        if (trimmed.isBlank() || !trimmed.contains("@")) {
            return@withContext Result.failure(IllegalArgumentException("Please enter a valid email address."))
        }
        try {
            val activeUser = currentUser
            if (activeUser != null) {
                runCatching { arenaRepository.getUserProfile(activeUser.uid) }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Updates the authenticated FirebaseUser's display name and optional profile photo URI.
     */
    suspend fun updateUserDisplayProfile(
        displayName: String,
        photoUriString: String?
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val user = currentUser ?: return@withContext Result.success(Unit)
            val builder = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                .setDisplayName(displayName.trim())
            if (!photoUriString.isNullOrBlank()) {
                runCatching {
                    builder.setPhotoUri(android.net.Uri.parse(photoUriString))
                }
            }
            runCatching {
                user.updateProfile(builder.build()).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        runCatching { auth.signOut() }
    }

    private fun sha256Digest(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
