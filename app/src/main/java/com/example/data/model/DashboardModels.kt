package com.example.data.model

import android.graphics.Bitmap
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

/**
 * The exact 6 primary bottom/rail navigation sections required by ARENA CHAT Part 2.1.
 */
enum class ArenaDashboardTab(val label: String, val subtitle: String) {
    CHATS("Chats", "Personal messages, group conversations & chat folders"),
    CALLS("Calls", "Voice calls, video calls, call history & missed calls"),
    COMMUNITIES("Communities", "Community discovery, servers, memberships & management"),
    CHANNELS("Channels", "Public channels, private channels, subscriptions & broadcasting"),
    DISCOVER("Discover", "People discovery, public groups, trending communities & verified profiles"),
    PROFILE("Profile", "User identity, Arena ID, account settings, privacy & customization")
}

/**
 * The exact 6 horizontal chat filters required by Part 2.1 Section 2.E.
 */
enum class ChatFilterType(val label: String) {
    ALL("All"),
    UNREAD("Unread"),
    PERSONAL("Personal"),
    GROUPS("Groups"),
    FAVORITES("Favorites"),
    ARCHIVED("Archived")
}

/**
 * The exact 8 Global Search categories required by Part 2.1 Section 2.C.
 */
enum class GlobalSearchCategory(val label: String) {
    PEOPLE("People"),
    ARENA_IDS("Arena IDs"),
    USERNAMES("Usernames"),
    MESSAGES("Messages"),
    GROUPS("Groups"),
    COMMUNITIES("Communities"),
    CHANNELS("Channels"),
    MEDIA_FILES("Media & Files")
}

/**
 * The exact 5 Floating Action Button menu items required by Part 2.1 Section 2.G.
 */
enum class FabNewActionType(val title: String, val subtitle: String, val defaultCategory: String) {
    NEW_MESSAGE("New Message", "Start an end-to-end encrypted 1-on-1 chat", "personal"),
    NEW_GROUP("New Group", "Create a multi-member group up to 200,000 members", "group"),
    NEW_CHANNEL("New Channel", "Broadcast updates to unlimited subscribers", "channel"),
    NEW_COMMUNITY("New Community", "Launch a multi-channel server with voice & text rooms", "community"),
    NEW_CONTACT("New Contact", "Add a person by Arena ID or @username", "personal")
}

/**
 * Settings & Profile access sections required by Part 2.1 Section 6.
 */
enum class ProfileSettingsSection(val title: String, val subtitle: String) {
    MY_PROFILE("My Profile", "Display name, bio, status & profile avatar"),
    ARENA_ID("Arena ID", "Unique Arena Chat cryptographic identifier & QR card"),
    PRIVACY("Privacy", "Last seen, online status, story visibility & read receipts"),
    SECURITY("Security", "End-to-end encryption keys, human verification badge & sessions"),
    NOTIFICATIONS("Notifications", "Message alerts, call ringtones, mentions & channel badges"),
    APPEARANCE("Appearance", "Emerald & Soft Cream palette, chat density & font scaling"),
    LANGUAGE("Language", "App interface language & instant message translation prefs"),
    STORAGE("Storage", "Local cache, media auto-download & cloud vault usage"),
    HELP_SUPPORT("Help and Support", "Part 2 architecture status, FAQ & security documentation")
}

@IgnoreExtraProperties
data class ChatRoom(
    val roomId: String = "",
    val ownerId: String = "",
    val roomName: String = "",
    val handleOrArenaId: String = "",
    val category: String = "personal",
    val lastMessageText: String = "",
    val lastMessageTimeLabel: String = "Just now",
    val unreadCount: Int = 0,
    val isOnline: Boolean = true,
    val isTyping: Boolean = false,
    val deliveryStatus: String = "read",
    val isMuted: Boolean = false,
    val isPinned: Boolean = false,
    val isFavorite: Boolean = false,
    val isArchived: Boolean = false,
    val isVerified: Boolean = false,
    val memberCount: Int = 2,
    val avatarPreset: Int = 0,
    @get:Exclude val avatarUri: String? = null,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toConversationItem(): ConversationItem = ConversationItem(
        conversationId = roomId.ifBlank { "conv_default" },
        ownerId = ownerId,
        displayName = roomName.ifBlank { "Arena Chat Room" },
        handleOrArenaId = handleOrArenaId,
        category = category,
        lastMessagePreview = lastMessageText,
        lastMessageTimeLabel = lastMessageTimeLabel,
        unreadCount = unreadCount,
        isOnline = isOnline,
        isTyping = isTyping,
        deliveryStatus = deliveryStatus,
        isMuted = isMuted,
        isPinned = isPinned,
        isFavorite = isFavorite,
        isArchived = isArchived,
        isVerified = isVerified,
        memberCount = memberCount,
        avatarPreset = avatarPreset,
        avatarUri = avatarUri,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun ConversationItem.toChatRoom(): ChatRoom = ChatRoom(
    roomId = conversationId,
    ownerId = ownerId,
    roomName = displayName,
    handleOrArenaId = handleOrArenaId,
    category = category,
    lastMessageText = lastMessagePreview,
    lastMessageTimeLabel = lastMessageTimeLabel,
    unreadCount = unreadCount,
    isOnline = isOnline,
    isTyping = isTyping,
    deliveryStatus = deliveryStatus,
    isMuted = isMuted,
    isPinned = isPinned,
    isFavorite = isFavorite,
    isArchived = isArchived,
    isVerified = isVerified,
    memberCount = memberCount,
    avatarPreset = avatarPreset,
    avatarUri = avatarUri,
    createdAt = createdAt,
    updatedAt = updatedAt
)

@IgnoreExtraProperties
data class ConversationItem(
    val conversationId: String = "",
    val ownerId: String = "",
    val displayName: String = "",
    val handleOrArenaId: String = "",
    val category: String = "personal", // "personal", "group", "channel", "community"
    val lastMessagePreview: String = "",
    val lastMessageTimeLabel: String = "Just now",
    val unreadCount: Int = 0,
    val isOnline: Boolean = false,
    val isTyping: Boolean = false,
    val deliveryStatus: String = "read", // "sent", "delivered", "read", "draft"
    val isMuted: Boolean = false,
    val isPinned: Boolean = false,
    val isFavorite: Boolean = false,
    val isArchived: Boolean = false,
    val isVerified: Boolean = false,
    val draftText: String = "",
    val memberCount: Int = 2,
    val avatarPreset: Int = 0,
    @get:Exclude val avatarUri: String? = null,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

enum class ChatMessageType {
    TEXT,
    VOICE_NOTE,
    IMAGE,
    DOCUMENT,
    LOCATION,
    POLL,
    CONTACT_CARD
}

@IgnoreExtraProperties
data class ChatPollOption(
    val optionId: String = "",
    val text: String = "",
    val votes: Int = 0,
    val isVotedByMe: Boolean = false
)

@IgnoreExtraProperties
data class ChatMessage(
    val messageId: String = "",
    val conversationId: String = "",
    val ownerId: String = "",
    val senderName: String = "",
    val isFromMe: Boolean = true,
    val text: String = "",
    val timeLabel: String = "Just now",
    val deliveryStatus: String = "read", // "sent", "delivered", "read"
    val messageType: ChatMessageType = ChatMessageType.TEXT,
    val replyToMessageId: String? = null,
    val replyToSenderName: String? = null,
    val replyToPreview: String? = null,
    val reactionEmoji: String? = null,
    val isStarred: Boolean = false,
    val isForwarded: Boolean = false,
    val voiceDurationSeconds: Int = 0,
    val mediaUri: String? = null,
    val documentTitle: String? = null,
    val documentMeta: String? = null,
    val locationTitle: String? = null,
    val locationSubtitle: String? = null,
    val pollQuestion: String? = null,
    @get:Exclude val pollOptions: List<ChatPollOption> = emptyList(),
    @get:Exclude val generatedBitmap: Bitmap? = null,
    @get:Exclude val aiEngineBadge: String? = null,
    @get:Exclude val isStreaming: Boolean = false,
    val sequenceOrder: Long = System.currentTimeMillis(),
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

/**
 * ARENA AI Automatic Intelligence Capabilities:
 * - DEEP_CHAT: ChatGPT / Claude-caliber reasoning, coding, writing & multilingual chat (gemini-3.8-flash)
 * - LIVE_WEB_SEARCH: Google AI Search Mode live knowledge & web-grounded synthesis (gemini-3.8-flash)
 * - IMAGE_STUDIO: Real-time AI Image Generation Studio (gemini-nano-banana-2.1)
 */
enum class ArenaAiMode(val label: String, val subtitle: String, val badge: String) {
    DEEP_CHAT(
        "Auto Neural Reasoning",
        "Automatic ChatGPT/Claude reasoning, Live Web Grounding & 8K Image Engine",
        "Auto Neural v3.8"
    ),
    LIVE_WEB_SEARCH(
        "Auto Live Web Grounding",
        "Real-time internet synthesis, verified facts & live knowledge cards",
        "Live Web Engine"
    ),
    IMAGE_STUDIO(
        "Auto 8K Visual Studio",
        "Automatic high-definition AI artwork & image synthesis",
        "8K Visual Engine"
    )
}

data class ArenaAiMessage(
    val id: String = "ai_${System.currentTimeMillis()}",
    val isUser: Boolean,
    val text: String,
    val timeLabel: String,
    val mode: ArenaAiMode = ArenaAiMode.DEEP_CHAT,
    val isStreaming: Boolean = false,
    val generatedBitmap: Bitmap? = null,
    val sourceHighlights: List<String> = emptyList(),
    val isError: Boolean = false
)

/**
 * Represents the automatic, non-interactive ARENA AI analysis injected into every conversation's message stream.
 */
data class ArenaConversationAiInsight(
    val conversationId: String,
    val detectedMode: ArenaAiMode = ArenaAiMode.DEEP_CHAT,
    val historySummary: String = "",
    val contextualAnswer: String = "",
    val smartSuggestions: List<String> = emptyList(),
    val engineBadge: String = ArenaAiMode.DEEP_CHAT.badge,
    val isLiveSynthesizing: Boolean = false
)

data class ActiveCallSession(
    val callId: String,
    val conversationId: String,
    val contactName: String,
    val handleOrArenaId: String,
    val isVideoCall: Boolean,
    val avatarPreset: Int = 0,
    val isMuted: Boolean = false,
    val isSpeakerOn: Boolean = true,
    val isCameraOn: Boolean = true,
    val callStatusLabel: String = "End-to-end encrypted"
)

@IgnoreExtraProperties
data class StoryItem(
    val storyId: String = "",
    val ownerId: String = "",
    val authorName: String = "",
    val authorUsername: String = "",
    val caption: String = "",
    val timestampLabel: String = "Just now",
    val isOwnStory: Boolean = false,
    val hasUnseen: Boolean = true,
    val privacyLevel: String = "all_contacts", // "all_contacts", "close_friends", "selected_only"
    val accentPreset: Int = 0,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class ArenaNotification(
    val notificationId: String = "",
    val ownerId: String = "",
    val type: String = "message", // "message", "call", "mention", "community", "channel", "security"
    val title: String = "",
    val body: String = "",
    val timeLabel: String = "Just now",
    val isRead: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class ArenaUserSettings(
    val userId: String = "",
    val presenceStatus: String = "online", // "online", "away", "dnd", "invisible"
    val storyPrivacy: String = "all_contacts", // "all_contacts", "close_friends", "selected_only"
    val e2eeStrictMode: Boolean = true,
    val readReceiptsEnabled: Boolean = true,
    val typingIndicatorsEnabled: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val themeMode: String = "cream_emerald", // "cream_emerald", "emerald_night", "system"
    val languageCode: String = "English (Global)",
    @ServerTimestamp val updatedAt: Timestamp? = null
)

data class CallHistoryEntry(
    val callId: String,
    val contactName: String,
    val handleOrArenaId: String,
    val isVideoCall: Boolean,
    val isIncoming: Boolean,
    val isMissed: Boolean,
    val timeLabel: String,
    val durationLabel: String,
    val isEncrypted: Boolean = true,
    val avatarPreset: Int = 0
)
