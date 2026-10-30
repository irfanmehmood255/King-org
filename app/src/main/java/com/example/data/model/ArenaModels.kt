package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val email: String = "",
    val country: String = "United States",
    val termsAccepted: Boolean = false,
    val emailVerified: Boolean = false,
    val humanVerified: Boolean = false,
    val humanVerificationMethod: String = "none",
    val username: String = "",
    val onboardingStage: String = "registered",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toCreateMap(): Map<String, Any> {
        return mapOf(
            "userId" to userId,
            "email" to email,
            "country" to country,
            "termsAccepted" to termsAccepted,
            "emailVerified" to emailVerified,
            "humanVerified" to humanVerified,
            "humanVerificationMethod" to humanVerificationMethod,
            "username" to username,
            "onboardingStage" to onboardingStage,
            "createdAt" to (createdAt ?: FieldValue.serverTimestamp()),
            "updatedAt" to FieldValue.serverTimestamp()
        )
    }
}

@IgnoreExtraProperties
data class UsernameRegistry(
    @DocumentId val id: String = "",
    val userId: String = "",
    val username: String = "",
    val displayUsername: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toCreateMap(): Map<String, Any> {
        return mapOf(
            "userId" to userId,
            "username" to username,
            "displayUsername" to displayUsername,
            "createdAt" to (createdAt ?: FieldValue.serverTimestamp()),
            "updatedAt" to FieldValue.serverTimestamp()
        )
    }
}
