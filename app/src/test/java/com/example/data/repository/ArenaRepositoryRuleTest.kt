package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.google.firebase.firestore.FirebaseFirestoreException
import java.util.UUID
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ArenaRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun createAndProgressOnboarding_validUser_succeedsAcrossAllStages() = runBlocking {
        val freshEmail = "alice_${UUID.randomUUID().toString().take(8)}@arenachat.test"
        val uid = signInTestUser(freshEmail)
        val repository = ArenaRepository(firestore, auth)

        val createResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.createOrInitializeProfile(
                email = freshEmail,
                country = "Pakistan",
                termsAccepted = true
            )
        }
        assertTrue("Profile creation should succeed: ${createResult.exceptionOrNull()}", createResult.isSuccess)
        val profile = createResult.getOrThrow()
        assertEquals(uid, profile.userId)
        assertEquals("registered", profile.onboardingStage)

        val emailVerResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.markEmailVerified()
        }
        assertTrue(emailVerResult.isSuccess)

        val humanVerResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.markHumanVerified("face_scan")
        }
        assertTrue(humanVerResult.isSuccess)

        val uniqueUsername = "arena_${UUID.randomUUID().toString().take(8).replace("-", "_")}"
        val availableBefore = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.isUsernameAvailable(uniqueUsername)
        }
        assertTrue(availableBefore.getOrThrow())

        val claimResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.claimUsernameAndCompleteOnboarding(uniqueUsername)
        }
        assertTrue("Username claim should succeed: ${claimResult.exceptionOrNull()}", claimResult.isSuccess)

        val finalProfile = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.getUserProfile(uid).getOrThrow()
        }
        assertNotNull(finalProfile)
        assertEquals("completed", finalProfile?.onboardingStage)
        assertEquals(uniqueUsername, finalProfile?.username)
    }

    @Test
    fun duplicateUsername_secondUserCannotClaimTakenUsername() = runBlocking {
        signInTestUser(ALICE_EMAIL)
        val aliceRepo = ArenaRepository(firestore, auth)
        withTimeout(DEFAULT_TIMEOUT_MS) {
            aliceRepo.createOrInitializeProfile(ALICE_EMAIL, "United States", true).getOrThrow()
        }
        val sharedUsername = "taken_${UUID.randomUUID().toString().take(8).replace("-", "_")}"
        withTimeout(DEFAULT_TIMEOUT_MS) {
            aliceRepo.claimUsernameAndCompleteOnboarding(sharedUsername).getOrThrow()
        }

        // Bob signs in and checks availability
        signInTestUser(BOB_EMAIL)
        val bobRepo = ArenaRepository(firestore, auth)
        withTimeout(DEFAULT_TIMEOUT_MS) {
            bobRepo.createOrInitializeProfile(BOB_EMAIL, "United Kingdom", true).getOrThrow()
        }
        val availableForBob = withTimeout(DEFAULT_TIMEOUT_MS) {
            bobRepo.isUsernameAvailable(sharedUsername).getOrThrow()
        }
        assertFalse("Taken username should not be available for Bob", availableForBob)

        val bobClaim = withTimeout(DEFAULT_TIMEOUT_MS) {
            bobRepo.claimUsernameAndCompleteOnboarding(sharedUsername)
        }
        assertTrue("Bob claiming Alice's username must fail", bobClaim.isFailure)
    }

    @Test
    fun getUserProfile_crossUserAccess_failsWithPermissionDenied() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val aliceRepo = ArenaRepository(firestore, auth)
        withTimeout(DEFAULT_TIMEOUT_MS) {
            aliceRepo.createOrInitializeProfile(ALICE_EMAIL, "Canada", true).getOrThrow()
        }

        signInTestUser(BOB_EMAIL)
        val bobRepo = ArenaRepository(firestore, auth)
        val crossRead = withTimeout(DEFAULT_TIMEOUT_MS) {
            bobRepo.getUserProfile(aliceUid)
        }
        assertTrue(crossRead.isFailure)
        val firestoreEx = crossRead.exceptionOrNull()?.unwrapFirestoreException()
        assertNotNull(firestoreEx)
        assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreEx?.code)
    }

    @Test
    fun observeUserProfile_unauthenticatedUser_failsWithPermissionDenied() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        auth.signOut()
        val repository = ArenaRepository(firestore, auth)

        val result = runCatching {
            withTimeout(FLOW_TIMEOUT_MS) {
                repository.observeUserProfile(aliceUid).first()
            }
        }
        assertTrue(result.isFailure)
        val firestoreEx = result.exceptionOrNull()?.unwrapFirestoreException()
        assertNotNull("Expected FirebaseFirestoreException, got ${result.exceptionOrNull()}", firestoreEx)
        assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreEx?.code)
    }

    @Test
    fun saveAndSynchronizeMessageHistory_persistsAndEnforcesIsolation() = runBlocking {
        val aliceEmail = "alice_msg_${UUID.randomUUID().toString().take(8)}@arenachat.test"
        val aliceUid = signInTestUser(aliceEmail)
        val aliceDashRepo = ArenaDashboardRepository(firestore, auth)

        val convId = "conv_sync_${UUID.randomUUID().toString().take(6)}"
        val msg1 = com.example.data.model.ChatMessage(
            messageId = "msg_${UUID.randomUUID().toString().take(8)}",
            conversationId = convId,
            ownerId = aliceUid,
            senderName = "You",
            isFromMe = true,
            text = "Synchronized Firestore message across sessions",
            timeLabel = "10:45 AM",
            deliveryStatus = "sent",
            sequenceOrder = 100L
        )

        withTimeout(DEFAULT_TIMEOUT_MS) {
            aliceDashRepo.saveChatMessage(msg1)
        }

        val syncedMessages = withTimeout(FLOW_TIMEOUT_MS) {
            aliceDashRepo.observeMessagesForConversation(aliceUid, convId).first { it.isNotEmpty() }
        }
        assertEquals(1, syncedMessages.size)
        assertEquals("Synchronized Firestore message across sessions", syncedMessages.first().text)
        assertEquals("10:45 AM", syncedMessages.first().timeLabel)

        withTimeout(DEFAULT_TIMEOUT_MS) {
            aliceDashRepo.updateMessageStatus(msg1.messageId, "read")
            aliceDashRepo.updateMessageReaction(msg1.messageId, "🔥")
        }

        val updatedMessages = withTimeout(FLOW_TIMEOUT_MS) {
            aliceDashRepo.observeAllMessages(aliceUid).first { list ->
                list.any { it.messageId == msg1.messageId && it.deliveryStatus == "read" }
            }
        }
        val fetched = updatedMessages.first { it.messageId == msg1.messageId }
        assertEquals("read", fetched.deliveryStatus)
        assertEquals("🔥", fetched.reactionEmoji)

        // Verify Bob cannot read Alice's messages
        signInTestUser(BOB_EMAIL)
        val bobDashRepo = ArenaDashboardRepository(firestore, auth)
        val bobCrossRead = runCatching {
            withTimeout(FLOW_TIMEOUT_MS) {
                bobDashRepo.observeAllMessages(aliceUid).first()
            }
        }
        assertTrue("Cross-user message read must fail", bobCrossRead.isFailure)
    }

    private companion object {
        const val ALICE_EMAIL = "alice@arenachat.test"
        const val BOB_EMAIL = "bob@arenachat.test"
        const val DEFAULT_TIMEOUT_MS = 5000L
        const val FLOW_TIMEOUT_MS = 3000L
    }
}
