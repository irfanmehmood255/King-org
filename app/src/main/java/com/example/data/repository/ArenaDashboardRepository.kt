package com.example.data.repository

import com.example.data.model.ArenaNotification
import com.example.data.model.ArenaUserSettings
import com.example.data.model.ChatMessage
import com.example.data.model.ChatMessageType
import com.example.data.model.ChatRoom
import com.example.data.model.ConversationItem
import com.example.data.model.StoryItem
import com.example.data.model.toChatRoom
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ArenaDashboardRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val conversationsCollection = db.collection("conversations")
    private val messagesCollection = db.collection("messages")
    private val storiesCollection = db.collection("stories")
    private val notificationsCollection = db.collection("notifications")
    private val settingsCollection = db.collection("user_settings")

    private fun requireUserId(): String =
        auth.currentUser?.uid ?: error("Authentication required for Firestore write operation")

    /**
     * Real-time Firestore listener returning [ChatRoom] models for the authenticated user.
     */
    fun observeChatRooms(userId: String): Flow<List<ChatRoom>> = callbackFlow {
        val registration = conversationsCollection
            .whereEqualTo("ownerId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    handleFirestoreError(error, OperationType.LIST, "conversations")
                    close(error)
                    return@addSnapshotListener
                }
                val rooms = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(
                        ConversationItem::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )?.toChatRoom()
                } ?: emptyList()
                trySend(rooms)
            }
        awaitClose { registration.remove() }
    }

    /**
     * Persists a [ChatRoom] in Cloud Firestore (`conversations` collection).
     */
    suspend fun saveChatRoom(chatRoom: ChatRoom): ChatRoom {
        val created = createConversation(
            displayName = chatRoom.roomName,
            handleOrArenaId = chatRoom.handleOrArenaId,
            category = chatRoom.category,
            lastMessagePreview = chatRoom.lastMessageText.ifBlank { "Chat room initialized" },
            isVerified = chatRoom.isVerified,
            memberCount = chatRoom.memberCount,
            avatarPreset = chatRoom.avatarPreset
        )
        return created.toChatRoom()
    }

    fun observeConversations(userId: String): Flow<List<ConversationItem>> = callbackFlow {
        val registration = conversationsCollection
            .whereEqualTo("ownerId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    handleFirestoreError(error, OperationType.LIST, "conversations")
                    close(error)
                    return@addSnapshotListener
                }
                val items = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(
                        ConversationItem::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                } ?: emptyList()
                trySend(items)
            }
        awaitClose { registration.remove() }
    }

    /**
     * Real-time Firestore listener for all chat messages belonging to the authenticated user,
     * synchronizing message history across sessions and devices.
     */
    fun observeAllMessages(userId: String): Flow<List<ChatMessage>> = callbackFlow {
        val registration = messagesCollection
            .whereEqualTo("ownerId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    handleFirestoreError(error, OperationType.LIST, "messages")
                    close(error)
                    return@addSnapshotListener
                }
                val items = snapshot?.documents?.mapNotNull { doc ->
                    docToChatMessage(doc)
                }?.sortedBy { it.sequenceOrder } ?: emptyList()
                trySend(items)
            }
        awaitClose { registration.remove() }
    }

    /**
     * Real-time Firestore listener for a single conversation's message history.
     */
    fun observeMessagesForConversation(userId: String, conversationId: String): Flow<List<ChatMessage>> = callbackFlow {
        val registration = messagesCollection
            .whereEqualTo("ownerId", userId)
            .whereEqualTo("conversationId", conversationId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    handleFirestoreError(error, OperationType.LIST, "messages")
                    close(error)
                    return@addSnapshotListener
                }
                val items = snapshot?.documents?.mapNotNull { doc ->
                    docToChatMessage(doc)
                }?.sortedBy { it.sequenceOrder } ?: emptyList()
                trySend(items)
            }
        awaitClose { registration.remove() }
    }

    private fun docToChatMessage(doc: DocumentSnapshot): ChatMessage? {
        val messageId = doc.getString("messageId") ?: doc.id
        val conversationId = doc.getString("conversationId") ?: return null
        val ownerId = doc.getString("ownerId") ?: ""
        val senderName = doc.getString("senderName") ?: "Arena User"
        val isFromMe = doc.getBoolean("isFromMe") ?: true
        val text = doc.getString("text") ?: ""
        val timeLabel = doc.getString("timeLabel") ?: "Just now"
        val deliveryStatus = doc.getString("deliveryStatus") ?: "read"
        val typeRaw = doc.getString("messageType") ?: "TEXT"
        val messageType = runCatching { ChatMessageType.valueOf(typeRaw) }.getOrDefault(ChatMessageType.TEXT)
        val isStarred = doc.getBoolean("isStarred") ?: false
        val isForwarded = doc.getBoolean("isForwarded") ?: false
        val voiceDuration = (doc.getLong("voiceDurationSeconds") ?: 0L).toInt()
        val sequenceOrder = doc.getLong("sequenceOrder") ?: 0L
        val createdAt = doc.getTimestamp("createdAt", DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
        val updatedAt = doc.getTimestamp("updatedAt", DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)

        return ChatMessage(
            messageId = messageId,
            conversationId = conversationId,
            ownerId = ownerId,
            senderName = senderName,
            isFromMe = isFromMe,
            text = text,
            timeLabel = timeLabel,
            deliveryStatus = deliveryStatus,
            messageType = messageType,
            replyToMessageId = doc.getString("replyToMessageId"),
            replyToSenderName = doc.getString("replyToSenderName"),
            replyToPreview = doc.getString("replyToPreview"),
            reactionEmoji = doc.getString("reactionEmoji"),
            isStarred = isStarred,
            isForwarded = isForwarded,
            voiceDurationSeconds = voiceDuration,
            mediaUri = doc.getString("mediaUri"),
            documentTitle = doc.getString("documentTitle"),
            documentMeta = doc.getString("documentMeta"),
            locationTitle = doc.getString("locationTitle"),
            locationSubtitle = doc.getString("locationSubtitle"),
            pollQuestion = doc.getString("pollQuestion"),
            sequenceOrder = sequenceOrder,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    /**
     * Persists a sent or received chat message to `/messages/{messageId}` in Firestore
     * and synchronizes the parent conversation's preview & timestamp.
     */
    suspend fun saveChatMessage(message: ChatMessage): ChatMessage {
        val uid = requireUserId()
        val cleanId = message.messageId
            .replace(Regex("[^a-zA-Z0-9_\\-]"), "_")
            .take(120)
            .ifBlank { "msg_${System.currentTimeMillis()}" }
        val cleanConvId = message.conversationId
            .replace(Regex("[^a-zA-Z0-9_\\-]"), "_")
            .take(120)
            .ifBlank { "conv_default" }
        val docRef = messagesCollection.document(cleanId)
        val payload = buildMessageWriteMap(
            uid = uid,
            cleanId = cleanId,
            cleanConvId = cleanConvId,
            message = message
        )
        try {
            docRef.set(payload).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
        return message.copy(
            messageId = cleanId,
            conversationId = cleanConvId,
            ownerId = uid
        )
    }

    suspend fun updateMessageStatus(messageId: String, deliveryStatus: String) {
        requireUserId()
        val docRef = messagesCollection.document(messageId)
        try {
            docRef.update(
                mapOf(
                    "deliveryStatus" to deliveryStatus,
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

    suspend fun updateMessageReaction(messageId: String, emoji: String?) {
        requireUserId()
        val docRef = messagesCollection.document(messageId)
        val updates = mutableMapOf<String, Any>(
            "updatedAt" to FieldValue.serverTimestamp()
        )
        if (emoji.isNullOrBlank()) {
            updates["reactionEmoji"] = FieldValue.delete()
        } else {
            updates["reactionEmoji"] = emoji.take(32)
        }
        try {
            docRef.update(updates).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    suspend fun updateMessageStarred(messageId: String, isStarred: Boolean) {
        requireUserId()
        val docRef = messagesCollection.document(messageId)
        try {
            docRef.update(
                mapOf(
                    "isStarred" to isStarred,
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

    suspend fun deleteChatMessage(messageId: String) {
        requireUserId()
        val docRef = messagesCollection.document(messageId)
        try {
            docRef.delete().await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            throw e
        }
    }

    suspend fun clearConversationMessages(messageIds: List<String>) {
        requireUserId()
        if (messageIds.isEmpty()) return
        val batch = db.batch()
        messageIds.forEach { id ->
            batch.delete(messagesCollection.document(id))
        }
        try {
            batch.commit().await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, "messages")
            throw e
        }
    }

    private fun buildMessageWriteMap(
        uid: String,
        cleanId: String,
        cleanConvId: String,
        message: ChatMessage
    ): Map<String, Any> {
        val now = FieldValue.serverTimestamp()
        val base = mutableMapOf<String, Any?>(
            "messageId" to cleanId,
            "conversationId" to cleanConvId,
            "ownerId" to uid,
            "senderName" to message.senderName.trim().take(120).ifBlank { "You" },
            "isFromMe" to message.isFromMe,
            "text" to message.text.take(4000),
            "timeLabel" to message.timeLabel.trim().take(40).ifBlank { "Just now" },
            "deliveryStatus" to message.deliveryStatus.takeIf { it in listOf("sent", "delivered", "read") }.orEmpty().ifBlank { "sent" },
            "messageType" to message.messageType.name,
            "isStarred" to message.isStarred,
            "isForwarded" to message.isForwarded,
            "voiceDurationSeconds" to message.voiceDurationSeconds.coerceIn(0, 3600),
            "sequenceOrder" to message.sequenceOrder.coerceAtLeast(0L),
            "replyToMessageId" to message.replyToMessageId?.take(128),
            "replyToSenderName" to message.replyToSenderName?.take(120),
            "replyToPreview" to message.replyToPreview?.take(500),
            "reactionEmoji" to message.reactionEmoji?.take(32),
            "mediaUri" to message.mediaUri?.take(1000),
            "documentTitle" to message.documentTitle?.take(200),
            "documentMeta" to message.documentMeta?.take(200),
            "locationTitle" to message.locationTitle?.take(200),
            "locationSubtitle" to message.locationSubtitle?.take(200),
            "pollQuestion" to message.pollQuestion?.take(300),
            "createdAt" to now,
            "updatedAt" to now
        )
        @Suppress("UNCHECKED_CAST")
        return base.filterValues { it != null } as Map<String, Any>
    }

    fun observeStories(userId: String): Flow<List<StoryItem>> = callbackFlow {
        val registration = storiesCollection
            .whereEqualTo("ownerId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    handleFirestoreError(error, OperationType.LIST, "stories")
                    close(error)
                    return@addSnapshotListener
                }
                val items = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(
                        StoryItem::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                } ?: emptyList()
                trySend(items)
            }
        awaitClose { registration.remove() }
    }

    fun observeNotifications(userId: String): Flow<List<ArenaNotification>> = callbackFlow {
        val registration = notificationsCollection
            .whereEqualTo("ownerId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    handleFirestoreError(error, OperationType.LIST, "notifications")
                    close(error)
                    return@addSnapshotListener
                }
                val items = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(
                        ArenaNotification::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                } ?: emptyList()
                trySend(items)
            }
        awaitClose { registration.remove() }
    }

    fun observeUserSettings(userId: String): Flow<ArenaUserSettings?> = callbackFlow {
        val docRef = settingsCollection.document(userId)
        val registration = docRef
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    handleFirestoreError(error, OperationType.GET, docRef.path)
                    close(error)
                    return@addSnapshotListener
                }
                val settings = if (snapshot != null && snapshot.exists()) {
                    snapshot.toObject(
                        ArenaUserSettings::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                } else {
                    null
                }
                trySend(settings)
            }
        awaitClose { registration.remove() }
    }

    suspend fun createConversation(
        displayName: String,
        handleOrArenaId: String,
        category: String,
        lastMessagePreview: String,
        isVerified: Boolean = false,
        memberCount: Int = 2,
        avatarPreset: Int = 0
    ): ConversationItem {
        val uid = requireUserId()
        val cleanId = "conv_${System.currentTimeMillis()}_${(100..999).random()}"
        val now = FieldValue.serverTimestamp()
        val payload = mapOf(
            "conversationId" to cleanId,
            "ownerId" to uid,
            "displayName" to displayName.trim().take(120).ifBlank { "Arena Contact" },
            "handleOrArenaId" to handleOrArenaId.trim().take(80).ifBlank { "@arena_user" },
            "category" to category,
            "lastMessagePreview" to lastMessagePreview.trim().take(500),
            "lastMessageTimeLabel" to "Just now",
            "unreadCount" to 0,
            "isOnline" to true,
            "isTyping" to false,
            "deliveryStatus" to "sent",
            "isMuted" to false,
            "isPinned" to false,
            "isFavorite" to false,
            "isArchived" to false,
            "isVerified" to isVerified,
            "draftText" to "",
            "memberCount" to memberCount.coerceIn(1, 1000000),
            "avatarPreset" to avatarPreset.coerceIn(0, 10),
            "createdAt" to now,
            "updatedAt" to now
        )
        val docRef = conversationsCollection.document(cleanId)
        try {
            docRef.set(payload).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
        return ConversationItem(
            conversationId = cleanId,
            ownerId = uid,
            displayName = displayName.trim().take(120).ifBlank { "Arena Contact" },
            handleOrArenaId = handleOrArenaId.trim().take(80).ifBlank { "@arena_user" },
            category = category,
            lastMessagePreview = lastMessagePreview.trim().take(500),
            lastMessageTimeLabel = "Just now",
            isOnline = true,
            deliveryStatus = "sent",
            isVerified = isVerified,
            memberCount = memberCount.coerceIn(1, 1000000),
            avatarPreset = avatarPreset.coerceIn(0, 10)
        )
    }

    suspend fun updateConversationFlags(
        conversationId: String,
        isPinned: Boolean? = null,
        isMuted: Boolean? = null,
        isArchived: Boolean? = null,
        isFavorite: Boolean? = null,
        unreadCount: Int? = null,
        lastMessagePreview: String? = null,
        lastMessageTimeLabel: String? = null,
        deliveryStatus: String? = null
    ) {
        requireUserId()
        val updates = mutableMapOf<String, Any>(
            "updatedAt" to FieldValue.serverTimestamp()
        )
        if (isPinned != null) updates["isPinned"] = isPinned
        if (isMuted != null) updates["isMuted"] = isMuted
        if (isArchived != null) updates["isArchived"] = isArchived
        if (isFavorite != null) updates["isFavorite"] = isFavorite
        if (unreadCount != null) updates["unreadCount"] = unreadCount.coerceIn(0, 9999)
        if (lastMessagePreview != null) updates["lastMessagePreview"] = lastMessagePreview.take(500)
        if (lastMessageTimeLabel != null) updates["lastMessageTimeLabel"] = lastMessageTimeLabel.take(40)
        if (deliveryStatus != null) updates["deliveryStatus"] = deliveryStatus

        val docRef = conversationsCollection.document(conversationId)
        try {
            docRef.update(updates).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    suspend fun deleteConversation(conversationId: String) {
        requireUserId()
        val docRef = conversationsCollection.document(conversationId)
        try {
            docRef.delete().await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            throw e
        }
    }

    suspend fun publishStory(
        authorName: String,
        authorUsername: String,
        caption: String,
        privacyLevel: String,
        accentPreset: Int
    ) {
        val uid = requireUserId()
        val storyId = "story_own_$uid"
        val docRef = storiesCollection.document(storyId)
        try {
            val snap = docRef.get().await()
            val now = FieldValue.serverTimestamp()
            if (snap.exists()) {
                docRef.update(
                    mapOf(
                        "caption" to caption.trim().take(300).ifBlank { "Available on Arena Chat" },
                        "timestampLabel" to "Just now",
                        "hasUnseen" to true,
                        "privacyLevel" to privacyLevel,
                        "accentPreset" to accentPreset.coerceIn(0, 10),
                        "updatedAt" to now
                    )
                ).await()
            } else {
                docRef.set(
                    mapOf(
                        "storyId" to storyId,
                        "ownerId" to uid,
                        "authorName" to authorName.trim().take(80).ifBlank { "My Status" },
                        "authorUsername" to authorUsername.trim().take(40).ifBlank { "me" },
                        "caption" to caption.trim().take(300).ifBlank { "Available on Arena Chat" },
                        "timestampLabel" to "Just now",
                        "isOwnStory" to true,
                        "hasUnseen" to true,
                        "privacyLevel" to privacyLevel,
                        "accentPreset" to accentPreset.coerceIn(0, 10),
                        "createdAt" to now,
                        "updatedAt" to now
                    )
                ).await()
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            throw e
        }
    }

    suspend fun markStoryViewed(storyId: String) {
        requireUserId()
        val docRef = storiesCollection.document(storyId)
        try {
            docRef.update(
                mapOf(
                    "hasUnseen" to false,
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

    suspend fun markNotificationRead(notificationId: String) {
        requireUserId()
        val docRef = notificationsCollection.document(notificationId)
        try {
            docRef.update(
                mapOf(
                    "isRead" to true,
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

    suspend fun markAllNotificationsRead(notificationIds: List<String>) {
        requireUserId()
        if (notificationIds.isEmpty()) return
        val batch = db.batch()
        val now = FieldValue.serverTimestamp()
        notificationIds.forEach { id ->
            batch.update(
                notificationsCollection.document(id),
                mapOf("isRead" to true, "updatedAt" to now)
            )
        }
        try {
            batch.commit().await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, "notifications")
            throw e
        }
    }

    suspend fun clearAllNotifications(notificationIds: List<String>) {
        requireUserId()
        if (notificationIds.isEmpty()) return
        val batch = db.batch()
        notificationIds.forEach { id ->
            batch.delete(notificationsCollection.document(id))
        }
        try {
            batch.commit().await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, "notifications")
            throw e
        }
    }

    suspend fun saveUserSettings(settings: ArenaUserSettings) {
        val uid = requireUserId()
        val docRef = settingsCollection.document(uid)
        val payload = mapOf(
            "userId" to uid,
            "presenceStatus" to settings.presenceStatus,
            "storyPrivacy" to settings.storyPrivacy,
            "e2eeStrictMode" to settings.e2eeStrictMode,
            "readReceiptsEnabled" to settings.readReceiptsEnabled,
            "typingIndicatorsEnabled" to settings.typingIndicatorsEnabled,
            "notificationsEnabled" to settings.notificationsEnabled,
            "themeMode" to settings.themeMode,
            "languageCode" to settings.languageCode.take(20).ifBlank { "English" },
            "updatedAt" to FieldValue.serverTimestamp()
        )
        try {
            docRef.set(payload, SetOptions.merge()).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            throw e
        }
    }

    /**
     * Seeds initial realistic conversations, messages, stories, notifications, and settings into Firestore
     * on first dashboard visit for the authenticated user if their collections are empty.
     */
    suspend fun ensureInitialDashboardDataSeeded(
        displayName: String,
        username: String
    ) {
        val uid = auth.currentUser?.uid ?: return
        val existingConvs = conversationsCollection
            .whereEqualTo("ownerId", uid)
            .limit(1)
            .get()
            .await()

        if (!existingConvs.isEmpty) return

        val batch = db.batch()
        val now = FieldValue.serverTimestamp()
        val seededConvs = defaultConversations(uid)

        seededConvs.forEach { item ->
            val ref = conversationsCollection.document(item.conversationId)
            batch.set(
                ref,
                mapOf(
                    "conversationId" to item.conversationId,
                    "ownerId" to uid,
                    "displayName" to item.displayName,
                    "handleOrArenaId" to item.handleOrArenaId,
                    "category" to item.category,
                    "lastMessagePreview" to item.lastMessagePreview,
                    "lastMessageTimeLabel" to item.lastMessageTimeLabel,
                    "unreadCount" to item.unreadCount,
                    "isOnline" to item.isOnline,
                    "isTyping" to item.isTyping,
                    "deliveryStatus" to item.deliveryStatus,
                    "isMuted" to item.isMuted,
                    "isPinned" to item.isPinned,
                    "isFavorite" to item.isFavorite,
                    "isArchived" to item.isArchived,
                    "isVerified" to item.isVerified,
                    "draftText" to item.draftText,
                    "memberCount" to item.memberCount,
                    "avatarPreset" to item.avatarPreset,
                    "createdAt" to now,
                    "updatedAt" to now
                )
            )
        }

        // Seed initial messages per conversation so message history is persisted in Firestore across sessions
        defaultMessagesForConversations(uid, seededConvs).forEach { msg ->
            val ref = messagesCollection.document(msg.messageId)
            batch.set(
                ref,
                buildMessageWriteMap(
                    uid = uid,
                    cleanId = msg.messageId,
                    cleanConvId = msg.conversationId,
                    message = msg
                )
            )
        }

        defaultStories(uid, displayName, username).forEach { story ->
            val ref = storiesCollection.document(story.storyId)
            batch.set(
                ref,
                mapOf(
                    "storyId" to story.storyId,
                    "ownerId" to uid,
                    "authorName" to story.authorName,
                    "authorUsername" to story.authorUsername,
                    "caption" to story.caption,
                    "timestampLabel" to story.timestampLabel,
                    "isOwnStory" to story.isOwnStory,
                    "hasUnseen" to story.hasUnseen,
                    "privacyLevel" to story.privacyLevel,
                    "accentPreset" to story.accentPreset,
                    "createdAt" to now,
                    "updatedAt" to now
                )
            )
        }

        defaultNotifications(uid).forEach { notif ->
            val ref = notificationsCollection.document(notif.notificationId)
            batch.set(
                ref,
                mapOf(
                    "notificationId" to notif.notificationId,
                    "ownerId" to uid,
                    "type" to notif.type,
                    "title" to notif.title,
                    "body" to notif.body,
                    "timeLabel" to notif.timeLabel,
                    "isRead" to notif.isRead,
                    "createdAt" to now,
                    "updatedAt" to now
                )
            )
        }

        val settingsRef = settingsCollection.document(uid)
        batch.set(
            settingsRef,
            mapOf(
                "userId" to uid,
                "presenceStatus" to "online",
                "storyPrivacy" to "all_contacts",
                "e2eeStrictMode" to true,
                "readReceiptsEnabled" to true,
                "typingIndicatorsEnabled" to true,
                "notificationsEnabled" to true,
                "themeMode" to "cream_emerald",
                "languageCode" to "English",
                "updatedAt" to now
            )
        )

        batch.commit().await()
    }

    companion object {
        fun defaultMessagesForConversations(
            ownerId: String = "local",
            conversations: List<ConversationItem> = defaultConversations(ownerId)
        ): List<ChatMessage> {
            val all = mutableListOf<ChatMessage>()
            var seq = 1000L
            conversations.forEachIndexed { idx, conv ->
                val cid = conv.conversationId
                when (idx) {
                    0 -> {
                        all += ChatMessage(
                            messageId = "${cid}_m1",
                            conversationId = cid,
                            ownerId = ownerId,
                            senderName = conv.displayName,
                            isFromMe = false,
                            text = "Salaam! Welcome to the new WhatsApp-powered Arena Chat room 🔥",
                            timeLabel = "10:35 AM",
                            deliveryStatus = "read",
                            sequenceOrder = seq++
                        )
                        all += ChatMessage(
                            messageId = "${cid}_m2",
                            conversationId = cid,
                            ownerId = ownerId,
                            senderName = "You",
                            isFromMe = true,
                            text = "Awesome! I needed full-screen real-time messaging just like WhatsApp with voice notes, media, and calls.",
                            timeLabel = "10:37 AM",
                            deliveryStatus = "read",
                            reactionEmoji = "❤️",
                            sequenceOrder = seq++
                        )
                        all += ChatMessage(
                            messageId = "${cid}_m3",
                            conversationId = cid,
                            ownerId = ownerId,
                            senderName = conv.displayName,
                            isFromMe = false,
                            text = conv.lastMessagePreview,
                            timeLabel = conv.lastMessageTimeLabel,
                            deliveryStatus = "read",
                            replyToMessageId = "${cid}_m2",
                            replyToSenderName = "You",
                            replyToPreview = "Awesome! I needed full-screen real-time messaging just like WhatsApp...",
                            sequenceOrder = seq++
                        )
                    }
                    else -> {
                        all += ChatMessage(
                            messageId = "${cid}_m1",
                            conversationId = cid,
                            ownerId = ownerId,
                            senderName = conv.displayName,
                            isFromMe = false,
                            text = "Welcome to ${conv.displayName} (${conv.handleOrArenaId}). All messages are end-to-end encrypted.",
                            timeLabel = "Earlier",
                            deliveryStatus = "read",
                            sequenceOrder = seq++
                        )
                        all += ChatMessage(
                            messageId = "${cid}_m2",
                            conversationId = cid,
                            ownerId = ownerId,
                            senderName = conv.displayName,
                            isFromMe = false,
                            text = conv.lastMessagePreview,
                            timeLabel = conv.lastMessageTimeLabel,
                            deliveryStatus = "read",
                            sequenceOrder = seq++
                        )
                    }
                }
            }
            return all
        }

        fun defaultConversations(ownerId: String = "local"): List<ConversationItem> {
            val suffix = ownerId.takeLast(6).replace(Regex("[^a-zA-Z0-9]"), "a")
            return listOf(
                ConversationItem(
                    conversationId = "conv_1_$suffix",
                    ownerId = ownerId,
                    displayName = "Elena Vance",
                    handleOrArenaId = "@elena_vance • ARENA-9042",
                    category = "personal",
                    lastMessagePreview = "Reviewed the Phase 2 end-to-end encryption architecture deck ✨",
                    lastMessageTimeLabel = "10:42 AM",
                    unreadCount = 3,
                    isOnline = true,
                    isTyping = true,
                    deliveryStatus = "delivered",
                    isMuted = false,
                    isPinned = true,
                    isFavorite = true,
                    isArchived = false,
                    isVerified = true,
                    draftText = "",
                    memberCount = 2,
                    avatarPreset = 0
                ),
                ConversationItem(
                    conversationId = "conv_arena_ai_$suffix",
                    ownerId = ownerId,
                    displayName = "ARENA AI",
                    handleOrArenaId = "@arena_ai • Official Neural Engine",
                    category = "personal",
                    lastMessagePreview = "Ask anything, search live web data, or generate 8K images automatically in chat ✨",
                    lastMessageTimeLabel = "Now",
                    unreadCount = 1,
                    isOnline = true,
                    isTyping = false,
                    deliveryStatus = "read",
                    isMuted = false,
                    isPinned = true,
                    isFavorite = true,
                    isArchived = false,
                    isVerified = true,
                    draftText = "",
                    memberCount = 2,
                    avatarPreset = 4
                ),
                ConversationItem(
                    conversationId = "conv_2_$suffix",
                    ownerId = ownerId,
                    displayName = "Arena Global Founders",
                    handleOrArenaId = "@arena_founders • Group",
                    category = "group",
                    lastMessagePreview = "Tariq: Voice lounge & screen-share test starts in 15 mins",
                    lastMessageTimeLabel = "10:18 AM",
                    unreadCount = 7,
                    isOnline = true,
                    isTyping = false,
                    deliveryStatus = "delivered",
                    isMuted = false,
                    isPinned = true,
                    isFavorite = true,
                    isArchived = false,
                    isVerified = true,
                    draftText = "",
                    memberCount = 148,
                    avatarPreset = 1
                ),
                ConversationItem(
                    conversationId = "conv_3_$suffix",
                    ownerId = ownerId,
                    displayName = "Zayn Malik",
                    handleOrArenaId = "@zayn_dev • ARENA-3319",
                    category = "personal",
                    lastMessagePreview = "Voice note (0:48) — Zero-latency Opus audio codec benchmark",
                    lastMessageTimeLabel = "09:50 AM",
                    unreadCount = 0,
                    isOnline = true,
                    isTyping = false,
                    deliveryStatus = "read",
                    isMuted = false,
                    isPinned = false,
                    isFavorite = true,
                    isArchived = false,
                    isVerified = false,
                    draftText = "",
                    memberCount = 2,
                    avatarPreset = 2
                ),
                ConversationItem(
                    conversationId = "conv_4_$suffix",
                    ownerId = ownerId,
                    displayName = "ARENA Official Announcements",
                    handleOrArenaId = "@arena_official • Broadcast Channel",
                    category = "channel",
                    lastMessagePreview = "ARENA CHAT v2.1: Multi-device Quantum-Safe E2EE & 6-Hub Navigation",
                    lastMessageTimeLabel = "Yesterday",
                    unreadCount = 1,
                    isOnline = true,
                    isTyping = false,
                    deliveryStatus = "delivered",
                    isMuted = true,
                    isPinned = false,
                    isFavorite = false,
                    isArchived = false,
                    isVerified = true,
                    draftText = "",
                    memberCount = 94500,
                    avatarPreset = 3
                ),
                ConversationItem(
                    conversationId = "conv_5_$suffix",
                    ownerId = ownerId,
                    displayName = "Design Systems & Compose Guild",
                    handleOrArenaId = "@compose_guild • Community Server",
                    category = "community",
                    lastMessagePreview = "#ui-showcase: Emerald & Gold adaptive tokens approved",
                    lastMessageTimeLabel = "Yesterday",
                    unreadCount = 12,
                    isOnline = true,
                    isTyping = false,
                    deliveryStatus = "delivered",
                    isMuted = false,
                    isPinned = false,
                    isFavorite = false,
                    isArchived = false,
                    isVerified = true,
                    draftText = "",
                    memberCount = 18420,
                    avatarPreset = 4
                ),
                ConversationItem(
                    conversationId = "conv_6_$suffix",
                    ownerId = ownerId,
                    displayName = "Dr. Amina Al-Mansoor",
                    handleOrArenaId = "@amina_sec • ARENA-7720",
                    category = "personal",
                    lastMessagePreview = "Let's finalize the biometric verification attestation report",
                    lastMessageTimeLabel = "Tue",
                    unreadCount = 0,
                    isOnline = false,
                    isTyping = false,
                    deliveryStatus = "draft",
                    isMuted = false,
                    isPinned = false,
                    isFavorite = false,
                    isArchived = false,
                    isVerified = true,
                    draftText = "Sending over the updated SHA-256 certificate pin...",
                    memberCount = 2,
                    avatarPreset = 5
                ),
                ConversationItem(
                    conversationId = "conv_7_$suffix",
                    ownerId = ownerId,
                    displayName = "Q3 Product Archive Vault",
                    handleOrArenaId = "@q3_vault • Group",
                    category = "group",
                    lastMessagePreview = "All sprint milestones archived and signed off.",
                    lastMessageTimeLabel = "Oct 22",
                    unreadCount = 0,
                    isOnline = false,
                    isTyping = false,
                    deliveryStatus = "read",
                    isMuted = true,
                    isPinned = false,
                    isFavorite = false,
                    isArchived = true,
                    isVerified = false,
                    draftText = "",
                    memberCount = 19,
                    avatarPreset = 6
                )
            )
        }

        fun defaultStories(
            ownerId: String = "local",
            userDisplayName: String = "My Status",
            username: String = "arena_member"
        ): List<StoryItem> {
            val suffix = ownerId.takeLast(6).replace(Regex("[^a-zA-Z0-9]"), "a")
            return listOf(
                StoryItem(
                    storyId = "story_own_$ownerId",
                    ownerId = ownerId,
                    authorName = userDisplayName.ifBlank { "My Status" },
                    authorUsername = username.ifBlank { "arena_member" },
                    caption = "Building the future of encrypted global communication on ARENA CHAT ✨",
                    timestampLabel = "Just now",
                    isOwnStory = true,
                    hasUnseen = false,
                    privacyLevel = "all_contacts",
                    accentPreset = 0
                ),
                StoryItem(
                    storyId = "story_1_$suffix",
                    ownerId = ownerId,
                    authorName = "Elena Vance",
                    authorUsername = "elena_vance",
                    caption = "Live from Zurich Cryptography Summit — E2EE v2 benchmarks!",
                    timestampLabel = "14m ago",
                    isOwnStory = false,
                    hasUnseen = true,
                    privacyLevel = "all_contacts",
                    accentPreset = 1
                ),
                StoryItem(
                    storyId = "story_2_$suffix",
                    ownerId = ownerId,
                    authorName = "Zayn Malik",
                    authorUsername = "zayn_dev",
                    caption = "Testing ultra-low latency HD spatial voice channels 🎧",
                    timestampLabel = "42m ago",
                    isOwnStory = false,
                    hasUnseen = true,
                    privacyLevel = "close_friends",
                    accentPreset = 2
                ),
                StoryItem(
                    storyId = "story_3_$suffix",
                    ownerId = ownerId,
                    authorName = "Dr. Amina",
                    authorUsername = "amina_sec",
                    caption = "Biometric liveness shield passing 99.98% accuracy checks 🛡️",
                    timestampLabel = "2h ago",
                    isOwnStory = false,
                    hasUnseen = true,
                    privacyLevel = "all_contacts",
                    accentPreset = 3
                ),
                StoryItem(
                    storyId = "story_4_$suffix",
                    ownerId = ownerId,
                    authorName = "Arena Official",
                    authorUsername = "arena_official",
                    caption = "Welcome to ARENA CHAT Part 2.1 — Connect. Share. Belong.",
                    timestampLabel = "4h ago",
                    isOwnStory = false,
                    hasUnseen = false,
                    privacyLevel = "all_contacts",
                    accentPreset = 4
                )
            )
        }

        fun defaultNotifications(ownerId: String = "local"): List<ArenaNotification> {
            val suffix = ownerId.takeLast(6).replace(Regex("[^a-zA-Z0-9]"), "a")
            return listOf(
                ArenaNotification(
                    notificationId = "notif_1_$suffix",
                    ownerId = ownerId,
                    type = "message",
                    title = "New Encrypted Message from Elena Vance",
                    body = "Reviewed the Phase 2 end-to-end encryption architecture deck ✨",
                    timeLabel = "2m ago",
                    isRead = false
                ),
                ArenaNotification(
                    notificationId = "notif_2_$suffix",
                    ownerId = ownerId,
                    type = "call",
                    title = "Missed HD Voice Call • Tariq Al-Hassan",
                    body = "Encrypted voice call attempt via Arena Direct Relay",
                    timeLabel = "18m ago",
                    isRead = false
                ),
                ArenaNotification(
                    notificationId = "notif_3_$suffix",
                    ownerId = ownerId,
                    type = "mention",
                    title = "Mentioned in Arena Global Founders",
                    body = "@you Can you confirm the Part 2.1 dashboard layout sign-off?",
                    timeLabel = "35m ago",
                    isRead = false
                ),
                ArenaNotification(
                    notificationId = "notif_4_$suffix",
                    ownerId = ownerId,
                    type = "community",
                    title = "Design Systems & Compose Guild Activity",
                    body = "24 members joined the #emerald-gold-tokens voice stage",
                    timeLabel = "1h ago",
                    isRead = true
                ),
                ArenaNotification(
                    notificationId = "notif_5_$suffix",
                    ownerId = ownerId,
                    type = "channel",
                    title = "ARENA Official Announcements Broadcast",
                    body = "Part 2.1 Main Dashboard & 6-Hub Navigation is now live",
                    timeLabel = "3h ago",
                    isRead = true
                ),
                ArenaNotification(
                    notificationId = "notif_6_$suffix",
                    ownerId = ownerId,
                    type = "security",
                    title = "Human Verification & E2EE Shield Active",
                    body = "Your Arena ID cryptographic session key is verified on this device",
                    timeLabel = "Today",
                    isRead = true
                )
            )
        }
    }
}
