package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.R
import com.example.data.model.ActiveCallSession
import com.example.data.model.ArenaAiMessage
import com.example.data.model.ArenaAiMode
import com.example.data.model.ArenaDashboardTab
import com.example.data.model.ArenaNotification
import com.example.data.model.ArenaUserSettings
import com.example.data.model.CallHistoryEntry
import com.example.data.model.ChatFilterType
import com.example.data.model.ChatMessage
import com.example.data.model.ChatMessageType
import com.example.data.model.ChatPollOption
import com.example.data.model.ConversationItem
import com.example.data.model.FabNewActionType
import com.example.data.model.GlobalSearchCategory
import com.example.data.model.ProfileSettingsSection
import com.example.data.model.StoryItem
import com.example.data.repository.ArenaDashboardRepository
import com.example.ui.screens.dashboard.ArenaAiService
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ArenaDashboardUiState(
    val selectedTab: ArenaDashboardTab = ArenaDashboardTab.CHATS,
    val selectedChatFilter: ChatFilterType = ChatFilterType.ALL,
    val isOnlineStatus: Boolean = true,
    val isLoading: Boolean = false,
    val isCloudSynced: Boolean = false,
    val statusBannerMessage: String? = null,
    val errorMessage: String? = null,

    // Global Search State (Section 2.C)
    val isSearchExpanded: Boolean = false,
    val searchQuery: String = "",
    val selectedSearchCategory: GlobalSearchCategory = GlobalSearchCategory.PEOPLE,
    val isSearching: Boolean = false,
    val recentSearches: List<String> = listOf(
        "@elena_vance",
        "ARENA-9042",
        "Encrypted Voice",
        "Compose Guild"
    ),
    val searchSuggestions: List<String> = listOf(
        "@arena_official",
        "ARENA-3319",
        "Design Systems",
        "Quantum E2EE",
        "Global Founders"
    ),

    // Stories / Status State (Section 2.D)
    val stories: List<StoryItem> = ArenaDashboardRepository.defaultStories(),
    val activeViewingStory: StoryItem? = null,
    val showAddStoryModal: Boolean = false,
    val showStoryPrivacyModal: Boolean = false,

    // Conversations & Interactions State (Section 2.F & Section 4)
    val conversations: List<ConversationItem> = ArenaDashboardRepository.defaultConversations(),
    val activeOpenedConversation: ConversationItem? = null,
    val contextMenuConversation: ConversationItem? = null,
    val confirmDeleteConversation: ConversationItem? = null,
    val activeContactProfileSheet: ConversationItem? = null,

    // Full WhatsApp-Style Chat Room State
    val messagesByConversation: Map<String, List<ChatMessage>> = defaultSeededMessages(),
    val replyingToMessage: ChatMessage? = null,
    val inChatSearchQuery: String = "",
    val isInChatSearchActive: Boolean = false,
    val chatWallpaperPreset: Int = 0, // 0 = WhatsApp Warm Cream, 1 = Emerald Mist, 2 = Dark Charcoal, 3 = Royal Gold

    // Active Voice / Video Call State
    val activeCallSession: ActiveCallSession? = null,

    // Floating Action Button State (Section 2.G)
    val isFabMenuExpanded: Boolean = false,
    val activeFabActionSheet: FabNewActionType? = null,

    // ARENA AI Assistant State (Deep Chat, Live Search Mode, Image Studio)
    val showArenaAiAssistant: Boolean = false,
    val arenaAiMode: ArenaAiMode = ArenaAiMode.DEEP_CHAT,
    val arenaAiInitialDraft: String = "",
    val isArenaAiGenerating: Boolean = false,
    val arenaAiMessages: List<ArenaAiMessage> = defaultArenaAiWelcomeMessages(),

    // Notification Center State (Section 5)
    val showNotificationCenter: Boolean = false,
    val notifications: List<ArenaNotification> = ArenaDashboardRepository.defaultNotifications(),

    // Profile & Settings State (Section 6)
    val userSettings: ArenaUserSettings = ArenaUserSettings(),
    val activeSettingsSection: ProfileSettingsSection? = null,

    // Calls Section State (Section 3.2)
    val callHistory: List<CallHistoryEntry> = listOf(
        CallHistoryEntry(
            callId = "call_1",
            contactName = "Elena Vance",
            handleOrArenaId = "@elena_vance • ARENA-9042",
            isVideoCall = true,
            isIncoming = true,
            isMissed = false,
            timeLabel = "Today, 10:15 AM",
            durationLabel = "18m 42s • HD E2EE",
            avatarPreset = 0
        ),
        CallHistoryEntry(
            callId = "call_2",
            contactName = "Tariq Al-Hassan",
            handleOrArenaId = "@tariq_h • ARENA-5180",
            isVideoCall = false,
            isIncoming = true,
            isMissed = true,
            timeLabel = "Today, 09:30 AM",
            durationLabel = "Missed Voice Call",
            avatarPreset = 1
        ),
        CallHistoryEntry(
            callId = "call_3",
            contactName = "Arena Global Founders",
            handleOrArenaId = "@arena_founders • Group Stage",
            isVideoCall = true,
            isIncoming = false,
            isMissed = false,
            timeLabel = "Yesterday, 6:20 PM",
            durationLabel = "45m 10s • 12 Participants",
            avatarPreset = 2
        ),
        CallHistoryEntry(
            callId = "call_4",
            contactName = "Dr. Amina Al-Mansoor",
            handleOrArenaId = "@amina_sec • ARENA-7720",
            isVideoCall = false,
            isIncoming = false,
            isMissed = false,
            timeLabel = "Tue, 4:05 PM",
            durationLabel = "09m 14s • Opus Ultra",
            avatarPreset = 5
        )
    )
)

fun defaultSeededMessages(): Map<String, List<ChatMessage>> {
    val defaultConvs = ArenaDashboardRepository.defaultConversations()
    val map = mutableMapOf<String, List<ChatMessage>>()

    defaultConvs.forEachIndexed { index, conv ->
        val cid = conv.conversationId
        val list = when {
            index == 0 -> listOf(
                ChatMessage(
                    messageId = "${cid}_m1",
                    conversationId = cid,
                    senderName = conv.displayName,
                    isFromMe = false,
                    text = "Salaam! Welcome to the new WhatsApp-powered Arena Chat room 🔥",
                    timeLabel = "10:35 AM",
                    deliveryStatus = "read"
                ),
                ChatMessage(
                    messageId = "${cid}_m2",
                    conversationId = cid,
                    senderName = "You",
                    isFromMe = true,
                    text = "Awesome! I needed full-screen real-time messaging just like WhatsApp with voice notes, media, and calls.",
                    timeLabel = "10:37 AM",
                    deliveryStatus = "read",
                    reactionEmoji = "❤️"
                ),
                ChatMessage(
                    messageId = "${cid}_m3",
                    conversationId = cid,
                    senderName = conv.displayName,
                    isFromMe = false,
                    text = "Arena_Phase2_Encryption_Spec.pdf",
                    timeLabel = "10:39 AM",
                    deliveryStatus = "read",
                    messageType = ChatMessageType.DOCUMENT,
                    documentTitle = "Arena_Phase2_Encryption_Spec.pdf",
                    documentMeta = "2.4 MB • 18 Pages • PDF"
                ),
                ChatMessage(
                    messageId = "${cid}_m4",
                    conversationId = cid,
                    senderName = conv.displayName,
                    isFromMe = false,
                    text = "Voice note (0:19)",
                    timeLabel = "10:40 AM",
                    deliveryStatus = "read",
                    messageType = ChatMessageType.VOICE_NOTE,
                    voiceDurationSeconds = 19
                ),
                ChatMessage(
                    messageId = "${cid}_m5",
                    conversationId = cid,
                    senderName = conv.displayName,
                    isFromMe = false,
                    text = conv.lastMessagePreview,
                    timeLabel = conv.lastMessageTimeLabel,
                    deliveryStatus = "read",
                    replyToMessageId = "${cid}_m2",
                    replyToSenderName = "You",
                    replyToPreview = "Awesome! I needed full-screen real-time messaging just like WhatsApp..."
                )
            )
            conv.displayName.contains("ARENA AI", ignoreCase = true) -> listOf(
                ChatMessage(
                    messageId = "${cid}_m1",
                    conversationId = cid,
                    senderName = "ARENA AI",
                    isFromMe = false,
                    text = "Assalam-o-Alaikum! I am **ARENA AI** — your automatic multi-intelligence assistant built right into every chat.\n\nNo mode buttons needed: just write naturally in English, Urdu, or Roman Urdu to get deep reasoning, live web answers, or say \"photo banao / create image\" to generate 8K visuals automatically!",
                    timeLabel = "Now",
                    deliveryStatus = "read",
                    aiEngineBadge = "Auto Neural v3.8"
                )
            )
            index == 2 -> listOf(
                ChatMessage(
                    messageId = "${cid}_m1",
                    conversationId = cid,
                    senderName = "Sarah Chen",
                    isFromMe = false,
                    text = "Team, we've enabled 200,000-member group capacity + instant WhatsApp speed!",
                    timeLabel = "10:05 AM",
                    deliveryStatus = "read",
                    reactionEmoji = "🔥"
                ),
                ChatMessage(
                    messageId = "${cid}_m2",
                    conversationId = cid,
                    senderName = "Tariq Al-Hassan",
                    isFromMe = false,
                    text = "What feature should we benchmark first in today's live lounge?",
                    timeLabel = "10:12 AM",
                    deliveryStatus = "read",
                    messageType = ChatMessageType.POLL,
                    pollQuestion = "What feature should we benchmark first in today's live lounge?",
                    pollOptions = listOf(
                        ChatPollOption("opt_1", "HD Video & Voice Calling", 42, isVotedByMe = true),
                        ChatPollOption("opt_2", "Instant Voice Notes & Waveforms", 29, isVotedByMe = false),
                        ChatPollOption("opt_3", "Multi-Server Community Rooms", 18, isVotedByMe = false)
                    )
                ),
                ChatMessage(
                    messageId = "${cid}_m3",
                    conversationId = cid,
                    senderName = "Tariq Al-Hassan",
                    isFromMe = false,
                    text = conv.lastMessagePreview,
                    timeLabel = conv.lastMessageTimeLabel,
                    deliveryStatus = "read"
                )
            )
            index == 3 -> listOf(
                ChatMessage(
                    messageId = "${cid}_m1",
                    conversationId = cid,
                    senderName = "You",
                    isFromMe = true,
                    text = "Hey Zayn, how is the Opus audio codec latency looking on Android?",
                    timeLabel = "09:44 AM",
                    deliveryStatus = "read"
                ),
                ChatMessage(
                    messageId = "${cid}_m2",
                    conversationId = cid,
                    senderName = conv.displayName,
                    isFromMe = false,
                    text = "Voice note (0:48)",
                    timeLabel = "09:50 AM",
                    deliveryStatus = "read",
                    messageType = ChatMessageType.VOICE_NOTE,
                    voiceDurationSeconds = 48,
                    reactionEmoji = "👍"
                )
            )
            else -> listOf(
                ChatMessage(
                    messageId = "${cid}_m1",
                    conversationId = cid,
                    senderName = conv.displayName,
                    isFromMe = false,
                    text = "Welcome to ${conv.displayName} (${conv.handleOrArenaId}). All messages here are protected with end-to-end encryption.",
                    timeLabel = "Yesterday",
                    deliveryStatus = "read"
                ),
                ChatMessage(
                    messageId = "${cid}_m2",
                    conversationId = cid,
                    senderName = conv.displayName,
                    isFromMe = false,
                    text = conv.lastMessagePreview,
                    timeLabel = conv.lastMessageTimeLabel,
                    deliveryStatus = "read"
                )
            )
        }
        map[cid] = list
    }
    return map
}

fun defaultArenaAiWelcomeMessages(): List<ArenaAiMessage> = listOf(
    ArenaAiMessage(
        id = "arena_ai_welcome_1",
        isUser = false,
        text = "Assalam-o-Alaikum! Welcome to **ARENA AI** ✨\n\nEverything here works **100% automatically** in a single unified chat—no mode buttons required:\n\n• **Deep Reasoning & Code**: Ask anything in English, Urdu, or Roman Urdu.\n• **Live Web Grounding**: Ask about live news, prices, trends, or facts for instant verified synthesis.\n• **Automatic 8K Visuals**: Just write *\"create an image of...\"* or *\"tasveer/photo banao...\"* and I will generate the image automatically in chat!",
        timeLabel = "Now",
        mode = ArenaAiMode.DEEP_CHAT,
        sourceHighlights = listOf(
            "Auto Neural Engine • Gemini 3.8 Flash + Nano Banana 2.1",
            "Zero-Click Automatic Intent Routing Active"
        )
    )
)

class ArenaDashboardViewModel(
    private val repository: ArenaDashboardRepository,
    private val auth: FirebaseAuth = Firebase.auth
) : ViewModel() {

    private val _uiState = MutableStateFlow(ArenaDashboardUiState())
    val uiState: StateFlow<ArenaDashboardUiState> = _uiState.asStateFlow()

    private var conversationsJob: Job? = null
    private var messagesJob: Job? = null
    private var storiesJob: Job? = null
    private var notificationsJob: Job? = null
    private var settingsJob: Job? = null
    private var searchDebounceJob: Job? = null
    private var arenaAiJob: Job? = null

    private val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        if (user != null) {
            attachFirestoreListeners(
                userId = user.uid,
                displayName = user.displayName ?: "Arena Member",
                username = user.email?.substringBefore("@") ?: "arena_member"
            )
        } else {
            detachFirestoreListeners()
            _uiState.update {
                it.copy(
                    isCloudSynced = false,
                    conversations = ArenaDashboardRepository.defaultConversations(),
                    stories = ArenaDashboardRepository.defaultStories(),
                    notifications = ArenaDashboardRepository.defaultNotifications()
                )
            }
        }
    }

    init {
        auth.addAuthStateListener(authListener)
    }

    override fun onCleared() {
        auth.removeAuthStateListener(authListener)
        detachFirestoreListeners()
        super.onCleared()
    }

    private fun detachFirestoreListeners() {
        conversationsJob?.cancel()
        messagesJob?.cancel()
        storiesJob?.cancel()
        notificationsJob?.cancel()
        settingsJob?.cancel()
    }

    fun attachFirestoreListeners(
        userId: String,
        displayName: String,
        username: String
    ) {
        detachFirestoreListeners()
        viewModelScope.launch {
            runCatching {
                repository.ensureInitialDashboardDataSeeded(displayName, username)
            }
        }

        conversationsJob = viewModelScope.launch {
            repository.observeConversations(userId)
                .catch { e ->
                    _uiState.update { it.copy(errorMessage = e.localizedMessage) }
                }
                .collect { items ->
                    if (items.isNotEmpty()) {
                        val sorted = items.sortedWith(
                            compareByDescending<ConversationItem> { it.isPinned }
                                .thenByDescending { it.unreadCount > 0 }
                                .thenBy { it.displayName }
                        )
                        _uiState.update { state ->
                            val updatedMessages = state.messagesByConversation.toMutableMap()
                            sorted.forEach { conv ->
                                if (!updatedMessages.containsKey(conv.conversationId)) {
                                    updatedMessages[conv.conversationId] = listOf(
                                        ChatMessage(
                                            messageId = "${conv.conversationId}_init1",
                                            conversationId = conv.conversationId,
                                            ownerId = userId,
                                            senderName = conv.displayName,
                                            isFromMe = false,
                                            text = "Connected with ${conv.displayName} (${conv.handleOrArenaId})",
                                            timeLabel = "Earlier",
                                            deliveryStatus = "read",
                                            sequenceOrder = 1L
                                        ),
                                        ChatMessage(
                                            messageId = "${conv.conversationId}_init2",
                                            conversationId = conv.conversationId,
                                            ownerId = userId,
                                            senderName = conv.displayName,
                                            isFromMe = false,
                                            text = conv.lastMessagePreview,
                                            timeLabel = conv.lastMessageTimeLabel,
                                            deliveryStatus = "read",
                                            sequenceOrder = 2L
                                        )
                                    )
                                }
                            }
                            val syncedActive = state.activeOpenedConversation?.let { active ->
                                sorted.find { it.conversationId == active.conversationId } ?: active
                            }
                            state.copy(
                                conversations = sorted,
                                activeOpenedConversation = syncedActive,
                                messagesByConversation = updatedMessages,
                                isCloudSynced = true,
                                errorMessage = null
                            )
                        }
                    }
                }
        }

        messagesJob = viewModelScope.launch {
            repository.observeAllMessages(userId)
                .catch { }
                .collect { cloudMessages ->
                    if (cloudMessages.isNotEmpty()) {
                        val grouped = cloudMessages
                            .groupBy { it.conversationId }
                            .mapValues { (_, list) ->
                                list.sortedWith(
                                    compareBy<ChatMessage> { it.sequenceOrder }
                                        .thenBy { it.createdAt?.toDate()?.time ?: 0L }
                                        .thenBy { it.messageId }
                                )
                            }
                        _uiState.update { state ->
                            val merged = state.messagesByConversation.toMutableMap()
                            grouped.forEach { (convId, msgs) ->
                                val localWithBitmaps = merged[convId].orEmpty()
                                    .filter { it.generatedBitmap != null }
                                    .associateBy { it.messageId }
                                merged[convId] = msgs.map { cloudMsg ->
                                    val bitmapMatch = localWithBitmaps[cloudMsg.messageId]
                                    if (bitmapMatch != null) {
                                        cloudMsg.copy(generatedBitmap = bitmapMatch.generatedBitmap)
                                    } else {
                                        cloudMsg
                                    }
                                }
                            }
                            state.copy(
                                messagesByConversation = merged,
                                isCloudSynced = true
                            )
                        }
                    }
                }
        }

        storiesJob = viewModelScope.launch {
            repository.observeStories(userId)
                .catch { }
                .collect { items ->
                    if (items.isNotEmpty()) {
                        val sorted = items.sortedByDescending { it.isOwnStory }
                        _uiState.update { it.copy(stories = sorted) }
                    }
                }
        }

        notificationsJob = viewModelScope.launch {
            repository.observeNotifications(userId)
                .catch { }
                .collect { items ->
                    _uiState.update { it.copy(notifications = items) }
                }
        }

        settingsJob = viewModelScope.launch {
            repository.observeUserSettings(userId)
                .catch { }
                .collect { settings ->
                    if (settings != null) {
                        _uiState.update {
                            it.copy(
                                userSettings = settings,
                                isOnlineStatus = settings.presenceStatus == "online"
                            )
                        }
                    }
                }
        }
    }

    private fun currentFormattedTime(): String {
        return SimpleDateFormat("hh:mm a", Locale.US).format(Date())
    }

    // ========================================================================
    // Navigation & Presence Actions
    // ========================================================================

    fun selectTab(tab: ArenaDashboardTab) {
        _uiState.update {
            it.copy(
                selectedTab = tab,
                isFabMenuExpanded = false
            )
        }
    }

    fun selectChatFilter(filter: ChatFilterType) {
        _uiState.update { it.copy(selectedChatFilter = filter) }
    }

    fun toggleOnlinePresence() {
        val nextOnline = !_uiState.value.isOnlineStatus
        val nextPresence = if (nextOnline) "online" else "away"
        val updatedSettings = _uiState.value.userSettings.copy(presenceStatus = nextPresence)
        _uiState.update {
            it.copy(
                isOnlineStatus = nextOnline,
                userSettings = updatedSettings,
                statusBannerMessage = if (nextOnline) {
                    "Status set to Online • Real-time presence active"
                } else {
                    "Status set to Away / Offline mode"
                }
            )
        }
        if (auth.currentUser != null) {
            viewModelScope.launch {
                runCatching { repository.saveUserSettings(updatedSettings) }
            }
        }
    }

    fun clearBanner() {
        _uiState.update { it.copy(statusBannerMessage = null, errorMessage = null) }
    }

    // ========================================================================
    // Global Search Actions (Section 2.C)
    // ========================================================================

    fun setSearchExpanded(expanded: Boolean) {
        _uiState.update {
            it.copy(
                isSearchExpanded = expanded,
                searchQuery = if (!expanded) "" else it.searchQuery,
                isSearching = false
            )
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query, isSearching = query.isNotBlank()) }
        searchDebounceJob?.cancel()
        if (query.isNotBlank()) {
            searchDebounceJob = viewModelScope.launch {
                delay(180)
                _uiState.update { state ->
                    val updatedRecent = if (query.trim().length >= 3) {
                        (listOf(query.trim()) + state.recentSearches)
                            .distinct()
                            .take(6)
                    } else {
                        state.recentSearches
                    }
                    state.copy(isSearching = false, recentSearches = updatedRecent)
                }
            }
        }
    }

    fun selectSearchCategory(category: GlobalSearchCategory) {
        _uiState.update { it.copy(selectedSearchCategory = category) }
    }

    fun clearRecentSearches() {
        _uiState.update { it.copy(recentSearches = emptyList()) }
    }

    // ========================================================================
    // Conversation Filtering & Search Calculation
    // ========================================================================

    fun getDisplayedConversations(state: ArenaDashboardUiState = _uiState.value): List<ConversationItem> {
        val query = state.searchQuery.trim().lowercase()
        val baseList = state.conversations.filter { item ->
            when (state.selectedChatFilter) {
                ChatFilterType.ALL -> !item.isArchived
                ChatFilterType.UNREAD -> !item.isArchived && item.unreadCount > 0
                ChatFilterType.PERSONAL -> !item.isArchived && item.category == "personal"
                ChatFilterType.GROUPS -> !item.isArchived && (item.category == "group" || item.category == "community")
                ChatFilterType.FAVORITES -> !item.isArchived && item.isFavorite
                ChatFilterType.ARCHIVED -> item.isArchived
            }
        }

        if (query.isEmpty()) {
            return baseList.sortedWith(
                compareByDescending<ConversationItem> { it.isPinned }
                    .thenByDescending { it.unreadCount > 0 }
                    .thenBy { it.displayName }
            )
        }

        return state.conversations.filter { item ->
            val matchesText = item.displayName.lowercase().contains(query) ||
                item.handleOrArenaId.lowercase().contains(query) ||
                item.lastMessagePreview.lowercase().contains(query) ||
                item.draftText.lowercase().contains(query)

            val matchesCategory = when (state.selectedSearchCategory) {
                GlobalSearchCategory.PEOPLE -> item.category == "personal"
                GlobalSearchCategory.ARENA_IDS -> item.handleOrArenaId.lowercase().contains("arena-") || matchesText
                GlobalSearchCategory.USERNAMES -> item.handleOrArenaId.lowercase().contains("@") && matchesText
                GlobalSearchCategory.MESSAGES -> item.lastMessagePreview.lowercase().contains(query) || item.draftText.lowercase().contains(query)
                GlobalSearchCategory.GROUPS -> item.category == "group"
                GlobalSearchCategory.COMMUNITIES -> item.category == "community"
                GlobalSearchCategory.CHANNELS -> item.category == "channel"
                GlobalSearchCategory.MEDIA_FILES -> matchesText
            }
            matchesText && (matchesCategory || state.conversations.none { c ->
                c.displayName.lowercase().contains(query) && c.category == state.selectedSearchCategory.name.lowercase()
            })
        }
    }

    // ========================================================================
    // Full WhatsApp-Style Chat Room & Real-Time Messaging Actions
    // ========================================================================

    fun openConversation(conversation: ConversationItem) {
        val updatedList = _uiState.value.conversations.map {
            if (it.conversationId == conversation.conversationId) {
                it.copy(unreadCount = 0, isTyping = false)
            } else {
                it
            }
        }
        _uiState.update { state ->
            val existingMessages = state.messagesByConversation[conversation.conversationId]
            val updatedMap = if (existingMessages.isNullOrEmpty()) {
                state.messagesByConversation + (
                    conversation.conversationId to listOf(
                        ChatMessage(
                            messageId = "${conversation.conversationId}_m1",
                            conversationId = conversation.conversationId,
                            senderName = conversation.displayName,
                            isFromMe = false,
                            text = conversation.lastMessagePreview,
                            timeLabel = conversation.lastMessageTimeLabel,
                            deliveryStatus = "read"
                        )
                    )
                    )
            } else {
                state.messagesByConversation
            }

            state.copy(
                conversations = updatedList,
                activeOpenedConversation = conversation.copy(unreadCount = 0, isTyping = false),
                messagesByConversation = updatedMap,
                replyingToMessage = null,
                inChatSearchQuery = "",
                isInChatSearchActive = false,
                contextMenuConversation = null
            )
        }
        if (auth.currentUser != null && conversation.unreadCount > 0) {
            viewModelScope.launch {
                runCatching {
                    repository.updateConversationFlags(
                        conversationId = conversation.conversationId,
                        unreadCount = 0
                    )
                }
            }
        }
    }

    fun closeOpenedConversation() {
        _uiState.update {
            it.copy(
                activeOpenedConversation = null,
                replyingToMessage = null,
                inChatSearchQuery = "",
                isInChatSearchActive = false
            )
        }
    }

    fun setReplyingToMessage(message: ChatMessage?) {
        _uiState.update { it.copy(replyingToMessage = message) }
    }

    fun setInChatSearchActive(active: Boolean) {
        _uiState.update {
            it.copy(
                isInChatSearchActive = active,
                inChatSearchQuery = if (!active) "" else it.inChatSearchQuery
            )
        }
    }

    fun updateInChatSearchQuery(query: String) {
        _uiState.update { it.copy(inChatSearchQuery = query) }
    }

    fun cycleChatWallpaper() {
        _uiState.update { state ->
            val next = (state.chatWallpaperPreset + 1) % 4
            state.copy(chatWallpaperPreset = next)
        }
    }

    fun sendQuickMessageInConversation(conversationId: String, text: String) {
        val cleanText = text.trim()
        if (cleanText.isEmpty()) return

        val replyTarget = _uiState.value.replyingToMessage
        val newMsg = ChatMessage(
            messageId = "msg_${System.currentTimeMillis()}_${(100..999).random()}",
            conversationId = conversationId,
            senderName = "You",
            isFromMe = true,
            text = cleanText,
            timeLabel = currentFormattedTime(),
            deliveryStatus = "sent",
            messageType = ChatMessageType.TEXT,
            replyToMessageId = replyTarget?.messageId,
            replyToSenderName = replyTarget?.senderName,
            replyToPreview = replyTarget?.text
        )

        appendOutgoingMessageAndSimulateLifecycle(
            conversationId = conversationId,
            outgoingMessage = newMsg,
            previewLabel = cleanText
        )
    }

    fun sendVoiceNoteInConversation(conversationId: String, durationSeconds: Int) {
        val safeDuration = durationSeconds.coerceAtLeast(1)
        val mins = safeDuration / 60
        val secs = safeDuration % 60
        val formattedDur = String.format(Locale.US, "%d:%02d", mins, secs)
        val replyTarget = _uiState.value.replyingToMessage

        val voiceMsg = ChatMessage(
            messageId = "msg_voice_${System.currentTimeMillis()}",
            conversationId = conversationId,
            senderName = "You",
            isFromMe = true,
            text = "Voice message ($formattedDur)",
            timeLabel = currentFormattedTime(),
            deliveryStatus = "sent",
            messageType = ChatMessageType.VOICE_NOTE,
            voiceDurationSeconds = safeDuration,
            replyToMessageId = replyTarget?.messageId,
            replyToSenderName = replyTarget?.senderName,
            replyToPreview = replyTarget?.text
        )

        appendOutgoingMessageAndSimulateLifecycle(
            conversationId = conversationId,
            outgoingMessage = voiceMsg,
            previewLabel = "🎤 Voice message ($formattedDur)"
        )
    }

    fun sendImageAttachmentInConversation(conversationId: String, mediaUri: String?, caption: String) {
        val label = caption.trim().ifBlank { "Photo" }
        val imgMsg = ChatMessage(
            messageId = "msg_img_${System.currentTimeMillis()}",
            conversationId = conversationId,
            senderName = "You",
            isFromMe = true,
            text = label,
            timeLabel = currentFormattedTime(),
            deliveryStatus = "sent",
            messageType = ChatMessageType.IMAGE,
            mediaUri = mediaUri
        )
        appendOutgoingMessageAndSimulateLifecycle(
            conversationId = conversationId,
            outgoingMessage = imgMsg,
            previewLabel = "📷 $label"
        )
    }

    fun sendDocumentInConversation(conversationId: String, fileName: String, fileMeta: String) {
        val docMsg = ChatMessage(
            messageId = "msg_doc_${System.currentTimeMillis()}",
            conversationId = conversationId,
            senderName = "You",
            isFromMe = true,
            text = fileName,
            timeLabel = currentFormattedTime(),
            deliveryStatus = "sent",
            messageType = ChatMessageType.DOCUMENT,
            documentTitle = fileName,
            documentMeta = fileMeta
        )
        appendOutgoingMessageAndSimulateLifecycle(
            conversationId = conversationId,
            outgoingMessage = docMsg,
            previewLabel = "📄 $fileName"
        )
    }

    fun sendLocationInConversation(conversationId: String, title: String, coordinates: String) {
        val locMsg = ChatMessage(
            messageId = "msg_loc_${System.currentTimeMillis()}",
            conversationId = conversationId,
            senderName = "You",
            isFromMe = true,
            text = title,
            timeLabel = currentFormattedTime(),
            deliveryStatus = "sent",
            messageType = ChatMessageType.LOCATION,
            locationTitle = title,
            locationSubtitle = coordinates
        )
        appendOutgoingMessageAndSimulateLifecycle(
            conversationId = conversationId,
            outgoingMessage = locMsg,
            previewLabel = "📍 Live Location: $title"
        )
    }

    fun sendPollInConversation(conversationId: String, question: String, options: List<String>) {
        val cleanQuestion = question.trim().ifBlank { "Quick Team Poll" }
        val cleanOpts = options.map { it.trim() }.filter { it.isNotBlank() }.ifEmpty {
            listOf("Yes, approved ✅", "Need changes 🛠️")
        }
        val pollMsg = ChatMessage(
            messageId = "msg_poll_${System.currentTimeMillis()}",
            conversationId = conversationId,
            senderName = "You",
            isFromMe = true,
            text = cleanQuestion,
            timeLabel = currentFormattedTime(),
            deliveryStatus = "sent",
            messageType = ChatMessageType.POLL,
            pollQuestion = cleanQuestion,
            pollOptions = cleanOpts.mapIndexed { idx, opt ->
                ChatPollOption(
                    optionId = "opt_${idx}_${System.currentTimeMillis()}",
                    text = opt,
                    votes = if (idx == 0) 1 else 0,
                    isVotedByMe = idx == 0
                )
            }
        )
        appendOutgoingMessageAndSimulateLifecycle(
            conversationId = conversationId,
            outgoingMessage = pollMsg,
            previewLabel = "📊 Poll: $cleanQuestion"
        )
    }

    fun voteOnPollOption(conversationId: String, messageId: String, optionId: String) {
        _uiState.update { state ->
            val currentList = state.messagesByConversation[conversationId] ?: return@update state
            val updatedList = currentList.map { msg ->
                if (msg.messageId == messageId && msg.messageType == ChatMessageType.POLL) {
                    val updatedOptions = msg.pollOptions.map { opt ->
                        if (opt.optionId == optionId) {
                            val nextVoted = !opt.isVotedByMe
                            opt.copy(
                                isVotedByMe = nextVoted,
                                votes = (opt.votes + if (nextVoted) 1 else -1).coerceAtLeast(0)
                            )
                        } else {
                            opt
                        }
                    }
                    msg.copy(pollOptions = updatedOptions)
                } else {
                    msg
                }
            }
            state.copy(
                messagesByConversation = state.messagesByConversation + (conversationId to updatedList)
            )
        }
    }

    fun toggleMessageReaction(conversationId: String, messageId: String, emoji: String) {
        var updatedReaction: String? = null
        _uiState.update { state ->
            val currentList = state.messagesByConversation[conversationId] ?: return@update state
            val updatedList = currentList.map { msg ->
                if (msg.messageId == messageId) {
                    val nextEmoji = if (msg.reactionEmoji == emoji) null else emoji
                    updatedReaction = nextEmoji
                    msg.copy(reactionEmoji = nextEmoji)
                } else {
                    msg
                }
            }
            state.copy(
                messagesByConversation = state.messagesByConversation + (conversationId to updatedList)
            )
        }
        if (auth.currentUser != null) {
            viewModelScope.launch {
                runCatching { repository.updateMessageReaction(messageId, updatedReaction) }
            }
        }
    }

    fun toggleStarMessage(conversationId: String, messageId: String) {
        var nextStarred = false
        _uiState.update { state ->
            val currentList = state.messagesByConversation[conversationId] ?: return@update state
            val updatedList = currentList.map { msg ->
                if (msg.messageId == messageId) {
                    nextStarred = !msg.isStarred
                    msg.copy(isStarred = nextStarred)
                } else {
                    msg
                }
            }
            state.copy(
                messagesByConversation = state.messagesByConversation + (conversationId to updatedList)
            )
        }
        if (auth.currentUser != null) {
            viewModelScope.launch {
                runCatching { repository.updateMessageStarred(messageId, nextStarred) }
            }
        }
    }

    fun deleteMessageInConversation(conversationId: String, messageId: String) {
        _uiState.update { state ->
            val currentList = state.messagesByConversation[conversationId] ?: return@update state
            val updatedList = currentList.filterNot { it.messageId == messageId }
            val lastMsg = updatedList.lastOrNull()
            val updatedConvs = state.conversations.map { conv ->
                if (conv.conversationId == conversationId && lastMsg != null) {
                    conv.copy(
                        lastMessagePreview = lastMsg.text,
                        lastMessageTimeLabel = lastMsg.timeLabel
                    )
                } else {
                    conv
                }
            }
            state.copy(
                conversations = updatedConvs,
                messagesByConversation = state.messagesByConversation + (conversationId to updatedList)
            )
        }
        if (auth.currentUser != null) {
            viewModelScope.launch {
                runCatching { repository.deleteChatMessage(messageId) }
            }
        }
    }

    fun clearConversationHistory(conversationId: String) {
        val messageIdsToClear = _uiState.value.messagesByConversation[conversationId]
            .orEmpty()
            .map { it.messageId }
        _uiState.update { state ->
            val updatedConvs = state.conversations.map { conv ->
                if (conv.conversationId == conversationId) {
                    conv.copy(
                        lastMessagePreview = "Chat history cleared",
                        lastMessageTimeLabel = "Just now",
                        unreadCount = 0
                    )
                } else {
                    conv
                }
            }
            state.copy(
                conversations = updatedConvs,
                messagesByConversation = state.messagesByConversation + (conversationId to emptyList()),
                statusBannerMessage = "Chat cleared"
            )
        }
        if (auth.currentUser != null && messageIdsToClear.isNotEmpty()) {
            viewModelScope.launch {
                runCatching { repository.clearConversationMessages(messageIdsToClear) }
            }
        }
    }

    private fun appendOutgoingMessageAndSimulateLifecycle(
        conversationId: String,
        outgoingMessage: ChatMessage,
        previewLabel: String
    ) {
        val timeNow = outgoingMessage.timeLabel
        _uiState.update { state ->
            val existing = state.messagesByConversation[conversationId].orEmpty()
            val updatedMessages = existing + outgoingMessage

            val updatedList = state.conversations.map { item ->
                if (item.conversationId == conversationId) {
                    item.copy(
                        lastMessagePreview = previewLabel,
                        lastMessageTimeLabel = timeNow,
                        deliveryStatus = "sent",
                        draftText = "",
                        unreadCount = 0,
                        isTyping = false
                    )
                } else {
                    item
                }
            }
            val updatedActive = state.activeOpenedConversation?.let { active ->
                if (active.conversationId == conversationId) {
                    active.copy(
                        lastMessagePreview = previewLabel,
                        lastMessageTimeLabel = timeNow,
                        deliveryStatus = "sent",
                        draftText = "",
                        isTyping = false
                    )
                } else {
                    active
                }
            }

            state.copy(
                conversations = updatedList,
                activeOpenedConversation = updatedActive,
                messagesByConversation = state.messagesByConversation + (conversationId to updatedMessages),
                replyingToMessage = null
            )
        }

        val currentUid = auth.currentUser?.uid
        val messageWithOwner = if (currentUid != null) {
            outgoingMessage.copy(ownerId = currentUid, sequenceOrder = System.currentTimeMillis())
        } else {
            outgoingMessage
        }

        if (currentUid != null) {
            viewModelScope.launch {
                runCatching {
                    repository.saveChatMessage(messageWithOwner)
                    repository.updateConversationFlags(
                        conversationId = conversationId,
                        lastMessagePreview = previewLabel,
                        lastMessageTimeLabel = timeNow,
                        deliveryStatus = "sent",
                        unreadCount = 0
                    )
                }
            }
        }

        // Real-time WhatsApp tick progression (sent -> delivered -> read) + Automatic In-Chat Arena AI / Smart Reply
        viewModelScope.launch {
            delay(350)
            updateMessageDeliveryState(conversationId, outgoingMessage.messageId, "delivered")
            delay(400)
            updateMessageDeliveryState(conversationId, outgoingMessage.messageId, "read")

            val targetConv = _uiState.value.conversations.find { it.conversationId == conversationId }
                ?: return@launch
            if (targetConv.category == "channel") return@launch

            setConversationTypingState(conversationId, true)

            val isArenaAiRoom = targetConv.displayName.contains("ARENA AI", ignoreCase = true) ||
                targetConv.handleOrArenaId.contains("@arena_ai", ignoreCase = true) ||
                outgoingMessage.text.contains("@ai", ignoreCase = true) ||
                outgoingMessage.text.contains("arena ai", ignoreCase = true)

            val detectedIntent = if (outgoingMessage.messageType == ChatMessageType.TEXT) {
                ArenaAiService.detectAutomaticIntent(outgoingMessage.text)
            } else {
                ArenaAiMode.DEEP_CHAT
            }

            val replySender = when {
                isArenaAiRoom || detectedIntent == ArenaAiMode.IMAGE_STUDIO -> "ARENA AI"
                targetConv.category == "group" || targetConv.category == "community" ->
                    listOf("Tariq Al-Hassan", "Elena Vance", "Sarah Chen", "Zayn Malik").random()
                else -> targetConv.displayName
            }

            val replyMessageId = "msg_reply_${System.currentTimeMillis()}"
            val replyTime = currentFormattedTime()

            // Automatic 8K Image Synthesis directly inside ANY chat room when the user asks for an image
            if (outgoingMessage.messageType == ChatMessageType.TEXT && detectedIntent == ArenaAiMode.IMAGE_STUDIO) {
                val bitmapResult = runCatching {
                    ArenaAiService.generateArenaAiImage(outgoingMessage.text)
                }.getOrNull()

                setConversationTypingState(conversationId, false)

                val imageReplyText = if (bitmapResult != null) {
                    "✨ ARENA AI 8K Visual synthesized for: \"${outgoingMessage.text}\""
                } else {
                    ArenaAiService.buildSmartOfflineFallback(outgoingMessage.text, ArenaAiMode.IMAGE_STUDIO)
                }

                val incomingImageMsg = ChatMessage(
                    messageId = replyMessageId,
                    conversationId = conversationId,
                    ownerId = auth.currentUser?.uid ?: "local",
                    senderName = "ARENA AI",
                    isFromMe = false,
                    text = imageReplyText,
                    timeLabel = replyTime,
                    deliveryStatus = "read",
                    messageType = if (bitmapResult != null) ChatMessageType.IMAGE else ChatMessageType.TEXT,
                    generatedBitmap = bitmapResult,
                    aiEngineBadge = ArenaAiMode.IMAGE_STUDIO.badge,
                    replyToMessageId = outgoingMessage.messageId,
                    replyToSenderName = "You",
                    replyToPreview = outgoingMessage.text,
                    sequenceOrder = System.currentTimeMillis()
                )
                appendOrUpdateIncomingReply(conversationId, incomingImageMsg, imageReplyText, replyTime, persistToCloud = true)
                return@launch
            }

            // For Text messages in ANY chat room, stream real-time AI response automatically with full chat history
            if (outgoingMessage.messageType == ChatMessageType.TEXT) {
                var streamedReply = ""
                val badge = detectedIntent.badge
                val recentHistory = _uiState.value.messagesByConversation[conversationId].orEmpty()
                try {
                    ArenaAiService.streamInChatContextualAiReply(
                        contactName = targetConv.displayName,
                        contactHandle = targetConv.handleOrArenaId,
                        category = targetConv.category,
                        userMessageText = outgoingMessage.text,
                        conversationHistory = recentHistory
                    ).collect { partial ->
                        streamedReply = partial
                        val partialMsg = ChatMessage(
                            messageId = replyMessageId,
                            conversationId = conversationId,
                            ownerId = auth.currentUser?.uid ?: "local",
                            senderName = replySender,
                            isFromMe = false,
                            text = partial,
                            timeLabel = replyTime,
                            deliveryStatus = "read",
                            aiEngineBadge = badge,
                            isStreaming = true,
                            replyToMessageId = outgoingMessage.messageId,
                            replyToSenderName = "You",
                            replyToPreview = outgoingMessage.text,
                            sequenceOrder = System.currentTimeMillis()
                        )
                        appendOrUpdateIncomingReply(conversationId, partialMsg, partial, replyTime, persistToCloud = false)
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    ArenaAiService.logAiError("In-chat automatic AI reply fallback", e)
                }

                setConversationTypingState(conversationId, false)

                val finalReplyText = streamedReply.trim().ifBlank {
                    if (isArenaAiRoom) {
                        ArenaAiService.buildSmartOfflineFallback(outgoingMessage.text, detectedIntent)
                    } else {
                        generateContextualReply(
                            contactName = targetConv.displayName,
                            category = targetConv.category,
                            userMessage = outgoingMessage
                        )
                    }
                }
                val finalIncoming = ChatMessage(
                    messageId = replyMessageId,
                    conversationId = conversationId,
                    ownerId = auth.currentUser?.uid ?: "local",
                    senderName = replySender,
                    isFromMe = false,
                    text = finalReplyText,
                    timeLabel = replyTime,
                    deliveryStatus = "read",
                    aiEngineBadge = badge,
                    isStreaming = false,
                    replyToMessageId = outgoingMessage.messageId,
                    replyToSenderName = "You",
                    replyToPreview = outgoingMessage.text,
                    sequenceOrder = System.currentTimeMillis()
                )
                appendOrUpdateIncomingReply(conversationId, finalIncoming, finalReplyText, replyTime, persistToCloud = true)
            } else {
                delay(800)
                setConversationTypingState(conversationId, false)
                val autoReplyText = generateContextualReply(
                    contactName = targetConv.displayName,
                    category = targetConv.category,
                    userMessage = outgoingMessage
                )
                val incomingReply = ChatMessage(
                    messageId = replyMessageId,
                    conversationId = conversationId,
                    ownerId = auth.currentUser?.uid ?: "local",
                    senderName = replySender,
                    isFromMe = false,
                    text = autoReplyText,
                    timeLabel = replyTime,
                    deliveryStatus = "read",
                    replyToMessageId = outgoingMessage.messageId,
                    replyToSenderName = "You",
                    replyToPreview = outgoingMessage.text,
                    sequenceOrder = System.currentTimeMillis()
                )
                appendOrUpdateIncomingReply(conversationId, incomingReply, autoReplyText, replyTime, persistToCloud = true)
            }
        }
    }

    private suspend fun appendOrUpdateIncomingReply(
        conversationId: String,
        incomingReply: ChatMessage,
        previewText: String,
        replyTime: String,
        persistToCloud: Boolean
    ) {
        _uiState.update { state ->
            val currentList = state.messagesByConversation[conversationId].orEmpty()
            val exists = currentList.any { it.messageId == incomingReply.messageId }
            val updatedList = if (exists) {
                currentList.map { if (it.messageId == incomingReply.messageId) incomingReply else it }
            } else {
                currentList + incomingReply
            }
            val isStillOpen = state.activeOpenedConversation?.conversationId == conversationId
            val updatedConvs = state.conversations.map { item ->
                if (item.conversationId == conversationId) {
                    item.copy(
                        lastMessagePreview = previewText.take(140),
                        lastMessageTimeLabel = replyTime,
                        deliveryStatus = "read",
                        unreadCount = if (isStillOpen) 0 else (if (exists) item.unreadCount else item.unreadCount + 1),
                        isTyping = incomingReply.isStreaming
                    )
                } else {
                    item
                }
            }
            val updatedActive = state.activeOpenedConversation?.let { active ->
                if (active.conversationId == conversationId) {
                    active.copy(
                        lastMessagePreview = previewText.take(140),
                        lastMessageTimeLabel = replyTime,
                        deliveryStatus = "read",
                        isTyping = incomingReply.isStreaming
                    )
                } else {
                    active
                }
            }
            state.copy(
                conversations = updatedConvs,
                activeOpenedConversation = updatedActive,
                messagesByConversation = state.messagesByConversation + (conversationId to updatedList)
            )
        }

        if (persistToCloud && auth.currentUser != null) {
            runCatching {
                repository.saveChatMessage(incomingReply)
                repository.updateConversationFlags(
                    conversationId = conversationId,
                    lastMessagePreview = previewText.take(500),
                    lastMessageTimeLabel = replyTime,
                    deliveryStatus = "read"
                )
            }
        }
    }

    private fun updateMessageDeliveryState(conversationId: String, messageId: String, status: String) {
        _uiState.update { state ->
            val currentList = state.messagesByConversation[conversationId] ?: return@update state
            val updatedList = currentList.map { msg ->
                if (msg.messageId == messageId) msg.copy(deliveryStatus = status) else msg
            }
            val updatedConvs = state.conversations.map { item ->
                if (item.conversationId == conversationId) item.copy(deliveryStatus = status) else item
            }
            state.copy(
                conversations = updatedConvs,
                messagesByConversation = state.messagesByConversation + (conversationId to updatedList)
            )
        }
        if (auth.currentUser != null) {
            viewModelScope.launch {
                runCatching { repository.updateMessageStatus(messageId, status) }
            }
        }
    }

    private fun setConversationTypingState(conversationId: String, isTyping: Boolean) {
        _uiState.update { state ->
            val updatedConvs = state.conversations.map { item ->
                if (item.conversationId == conversationId) item.copy(isTyping = isTyping) else item
            }
            val updatedActive = state.activeOpenedConversation?.let { active ->
                if (active.conversationId == conversationId) active.copy(isTyping = isTyping) else active
            }
            state.copy(
                conversations = updatedConvs,
                activeOpenedConversation = updatedActive
            )
        }
    }

    private fun generateContextualReply(
        contactName: String,
        category: String,
        userMessage: ChatMessage
    ): String {
        val lower = userMessage.text.lowercase()
        return when {
            userMessage.messageType == ChatMessageType.VOICE_NOTE ->
                "Listened to your voice note! Crystal clear audio quality 🎧👍"
            userMessage.messageType == ChatMessageType.IMAGE ->
                "Got the photo! Looks super sharp in HD ✨"
            userMessage.messageType == ChatMessageType.DOCUMENT ->
                "Received \"${userMessage.documentTitle ?: "document"}\". Reviewing it right now 📄✅"
            userMessage.messageType == ChatMessageType.LOCATION ->
                "Got your live location pin! On my way 📍"
            userMessage.messageType == ChatMessageType.POLL ->
                "Just cast my vote in the poll! Great initiative 📊🔥"
            lower.contains("salam") || lower.contains("salaam") || lower.contains("hello") || lower.contains("hi") ->
                "Walaikum Assalam! How are things going on Arena Chat today? 🤝"
            lower.contains("call") || lower.contains("voice") || lower.contains("video") ->
                "I'm free right now — tap the voice or video call icon at the top whenever you're ready! 📞"
            category == "group" || category == "community" ->
                "100% agreed! Pinning this in $contactName for the whole team 🚀"
            else ->
                "Got it! Received your message loud and clear on Arena Chat ✅"
        }
    }

    // ========================================================================
    // WhatsApp-Style Voice & Video Calling Actions
    // ========================================================================

    fun startCallWithConversation(conversation: ConversationItem, isVideoCall: Boolean) {
        _uiState.update {
            it.copy(
                activeCallSession = ActiveCallSession(
                    callId = "call_${System.currentTimeMillis()}",
                    conversationId = conversation.conversationId,
                    contactName = conversation.displayName,
                    handleOrArenaId = conversation.handleOrArenaId,
                    isVideoCall = isVideoCall,
                    avatarPreset = conversation.avatarPreset,
                    isCameraOn = isVideoCall
                )
            )
        }
    }

    fun startCallFromHistory(entry: CallHistoryEntry, isVideoOverride: Boolean? = null) {
        val useVideo = isVideoOverride ?: entry.isVideoCall
        _uiState.update {
            it.copy(
                activeCallSession = ActiveCallSession(
                    callId = "call_${System.currentTimeMillis()}",
                    conversationId = entry.callId,
                    contactName = entry.contactName,
                    handleOrArenaId = entry.handleOrArenaId,
                    isVideoCall = useVideo,
                    avatarPreset = entry.avatarPreset,
                    isCameraOn = useVideo
                )
            )
        }
    }

    fun clearCallHistory() {
        _uiState.update {
            it.copy(
                callHistory = emptyList(),
                statusBannerMessage = "Call log cleared"
            )
        }
    }

    fun showStatusBanner(message: String) {
        _uiState.update { it.copy(statusBannerMessage = message) }
    }

    fun openCommunitySubRoom(community: ConversationItem, roomName: String) {
        if (roomName.contains("voice", ignoreCase = true)) {
            startCallWithConversation(community, isVideoCall = false)
        } else {
            openConversation(community)
        }
    }

    fun replyToStory(story: StoryItem, replyText: String) {
        val cleanReply = replyText.trim()
        if (cleanReply.isEmpty()) return
        val existingConv = _uiState.value.conversations.firstOrNull {
            it.displayName.equals(story.authorName, ignoreCase = true) ||
                it.handleOrArenaId.contains(story.authorUsername, ignoreCase = true)
        }
        if (existingConv != null) {
            sendQuickMessageInConversation(
                conversationId = existingConv.conversationId,
                text = "Replied to Status (\"${story.caption.take(28)}...\"): $cleanReply"
            )
            _uiState.update {
                it.copy(
                    activeViewingStory = null,
                    statusBannerMessage = "Sent reply to ${story.authorName}'s Status"
                )
            }
        } else {
            createNewItemFromFab(
                actionType = FabNewActionType.NEW_MESSAGE,
                titleOrName = story.authorName,
                handleOrArenaId = "@${story.authorUsername}",
                initialMessageOrDescription = "Replied to Status: $cleanReply"
            )
            _uiState.update {
                it.copy(
                    activeViewingStory = null,
                    statusBannerMessage = "Sent reply to ${story.authorName}'s Status"
                )
            }
        }
    }

    fun toggleActiveCallMute() {
        _uiState.update { state ->
            val current = state.activeCallSession ?: return@update state
            state.copy(activeCallSession = current.copy(isMuted = !current.isMuted))
        }
    }

    fun toggleActiveCallSpeaker() {
        _uiState.update { state ->
            val current = state.activeCallSession ?: return@update state
            state.copy(activeCallSession = current.copy(isSpeakerOn = !current.isSpeakerOn))
        }
    }

    fun toggleActiveCallVideo() {
        _uiState.update { state ->
            val current = state.activeCallSession ?: return@update state
            val nextVideo = !current.isCameraOn
            state.copy(
                activeCallSession = current.copy(
                    isVideoCall = nextVideo,
                    isCameraOn = nextVideo
                )
            )
        }
    }

    fun endActiveCallSession(durationLabel: String = "01m 12s • HD E2EE") {
        val current = _uiState.value.activeCallSession ?: return
        val newEntry = CallHistoryEntry(
            callId = current.callId,
            contactName = current.contactName,
            handleOrArenaId = current.handleOrArenaId,
            isVideoCall = current.isVideoCall,
            isIncoming = false,
            isMissed = false,
            timeLabel = "Just now",
            durationLabel = durationLabel,
            avatarPreset = current.avatarPreset
        )
        _uiState.update { state ->
            state.copy(
                activeCallSession = null,
                callHistory = listOf(newEntry) + state.callHistory,
                statusBannerMessage = "Call ended with ${current.contactName} ($durationLabel)"
            )
        }
    }

    // ========================================================================
    // Context Menu & Conversation Flag Actions
    // ========================================================================

    fun openConversationContextMenu(conversation: ConversationItem?) {
        _uiState.update { it.copy(contextMenuConversation = conversation) }
    }

    fun openContactProfileSheet(conversation: ConversationItem?) {
        _uiState.update {
            it.copy(
                activeContactProfileSheet = conversation,
                contextMenuConversation = null
            )
        }
    }

    fun togglePinConversation(conversation: ConversationItem) {
        val nextPinned = !conversation.isPinned
        _uiState.update { state ->
            val updated = state.conversations.map {
                if (it.conversationId == conversation.conversationId) it.copy(isPinned = nextPinned) else it
            }
            state.copy(
                conversations = updated,
                contextMenuConversation = null,
                statusBannerMessage = if (nextPinned) {
                    "Pinned \"${conversation.displayName}\" to top"
                } else {
                    "Unpinned \"${conversation.displayName}\""
                }
            )
        }
        if (auth.currentUser != null) {
            viewModelScope.launch {
                runCatching {
                    repository.updateConversationFlags(
                        conversationId = conversation.conversationId,
                        isPinned = nextPinned
                    )
                }
            }
        }
    }

    fun toggleMuteConversation(conversation: ConversationItem) {
        val nextMuted = !conversation.isMuted
        _uiState.update { state ->
            val updated = state.conversations.map {
                if (it.conversationId == conversation.conversationId) it.copy(isMuted = nextMuted) else it
            }
            val updatedActive = state.activeOpenedConversation?.let { active ->
                if (active.conversationId == conversation.conversationId) active.copy(isMuted = nextMuted) else active
            }
            state.copy(
                conversations = updated,
                activeOpenedConversation = updatedActive,
                contextMenuConversation = null,
                statusBannerMessage = if (nextMuted) {
                    "Muted notifications for \"${conversation.displayName}\""
                } else {
                    "Unmuted notifications for \"${conversation.displayName}\""
                }
            )
        }
        if (auth.currentUser != null) {
            viewModelScope.launch {
                runCatching {
                    repository.updateConversationFlags(
                        conversationId = conversation.conversationId,
                        isMuted = nextMuted
                    )
                }
            }
        }
    }

    fun toggleArchiveConversation(conversation: ConversationItem) {
        val nextArchived = !conversation.isArchived
        _uiState.update { state ->
            val updated = state.conversations.map {
                if (it.conversationId == conversation.conversationId) {
                    it.copy(isArchived = nextArchived, isPinned = if (nextArchived) false else it.isPinned)
                } else {
                    it
                }
            }
            state.copy(
                conversations = updated,
                contextMenuConversation = null,
                statusBannerMessage = if (nextArchived) {
                    "Moved \"${conversation.displayName}\" to Archived"
                } else {
                    "Restored \"${conversation.displayName}\" from Archived"
                }
            )
        }
        if (auth.currentUser != null) {
            viewModelScope.launch {
                runCatching {
                    repository.updateConversationFlags(
                        conversationId = conversation.conversationId,
                        isArchived = nextArchived,
                        isPinned = if (nextArchived) false else conversation.isPinned
                    )
                }
            }
        }
    }

    fun toggleFavoriteConversation(conversation: ConversationItem) {
        val nextFavorite = !conversation.isFavorite
        _uiState.update { state ->
            val updated = state.conversations.map {
                if (it.conversationId == conversation.conversationId) it.copy(isFavorite = nextFavorite) else it
            }
            val updatedActive = state.activeOpenedConversation?.let { active ->
                if (active.conversationId == conversation.conversationId) active.copy(isFavorite = nextFavorite) else active
            }
            state.copy(
                conversations = updated,
                activeOpenedConversation = updatedActive,
                contextMenuConversation = null,
                statusBannerMessage = if (nextFavorite) {
                    "Added \"${conversation.displayName}\" to Favorites"
                } else {
                    "Removed \"${conversation.displayName}\" from Favorites"
                }
            )
        }
        if (auth.currentUser != null) {
            viewModelScope.launch {
                runCatching {
                    repository.updateConversationFlags(
                        conversationId = conversation.conversationId,
                        isFavorite = nextFavorite
                    )
                }
            }
        }
    }

    fun toggleUnreadConversation(conversation: ConversationItem) {
        val nextUnread = if (conversation.unreadCount > 0) 0 else 1
        _uiState.update { state ->
            val updated = state.conversations.map {
                if (it.conversationId == conversation.conversationId) it.copy(unreadCount = nextUnread) else it
            }
            state.copy(
                conversations = updated,
                contextMenuConversation = null,
                statusBannerMessage = if (nextUnread > 0) {
                    "Marked \"${conversation.displayName}\" as unread"
                } else {
                    "Marked \"${conversation.displayName}\" as read"
                }
            )
        }
        if (auth.currentUser != null) {
            viewModelScope.launch {
                runCatching {
                    repository.updateConversationFlags(
                        conversationId = conversation.conversationId,
                        unreadCount = nextUnread
                    )
                }
            }
        }
    }

    fun requestDeleteConversation(conversation: ConversationItem?) {
        _uiState.update {
            it.copy(
                confirmDeleteConversation = conversation,
                contextMenuConversation = null
            )
        }
    }

    fun confirmDeleteConversationNow() {
        val target = _uiState.value.confirmDeleteConversation ?: return
        _uiState.update { state ->
            state.copy(
                conversations = state.conversations.filterNot { it.conversationId == target.conversationId },
                confirmDeleteConversation = null,
                activeOpenedConversation = state.activeOpenedConversation?.takeIf { it.conversationId != target.conversationId },
                statusBannerMessage = "Deleted conversation with \"${target.displayName}\""
            )
        }
        if (auth.currentUser != null) {
            viewModelScope.launch {
                runCatching { repository.deleteConversation(target.conversationId) }
            }
        }
    }

    // ========================================================================
    // Floating Action Button Actions (Section 2.G)
    // ========================================================================

    fun toggleFabMenu() {
        _uiState.update { it.copy(isFabMenuExpanded = !it.isFabMenuExpanded) }
    }

    fun dismissFabMenu() {
        _uiState.update { it.copy(isFabMenuExpanded = false) }
    }

    fun openFabCreationFlow(actionType: FabNewActionType?) {
        _uiState.update {
            it.copy(
                isFabMenuExpanded = false,
                activeFabActionSheet = actionType
            )
        }
    }

    fun createNewItemFromFab(
        actionType: FabNewActionType,
        titleOrName: String,
        handleOrArenaId: String,
        initialMessageOrDescription: String
    ) {
        val cleanTitle = titleOrName.trim().ifBlank { actionType.title }
        val cleanHandle = handleOrArenaId.trim().ifBlank {
            "@${cleanTitle.lowercase().replace(Regex("[^a-z0-9]"), "_").take(14)} • ARENA-${(1000..9999).random()}"
        }
        val cleanPreview = initialMessageOrDescription.trim().ifBlank {
            "Hey! Let's chat on Arena Chat."
        }
        val defaultMembers = when (actionType) {
            FabNewActionType.NEW_MESSAGE, FabNewActionType.NEW_CONTACT -> 2
            FabNewActionType.NEW_GROUP -> 12
            FabNewActionType.NEW_CHANNEL -> 100
            FabNewActionType.NEW_COMMUNITY -> 250
        }

        val convId = "conv_local_${System.currentTimeMillis()}"
        val localItem = ConversationItem(
            conversationId = convId,
            ownerId = auth.currentUser?.uid ?: "local",
            displayName = cleanTitle,
            handleOrArenaId = cleanHandle,
            category = actionType.defaultCategory,
            lastMessagePreview = cleanPreview,
            lastMessageTimeLabel = currentFormattedTime(),
            unreadCount = 0,
            isOnline = true,
            isTyping = false,
            deliveryStatus = "read",
            isPinned = false,
            isVerified = actionType == FabNewActionType.NEW_CHANNEL || actionType == FabNewActionType.NEW_COMMUNITY,
            memberCount = defaultMembers,
            avatarPreset = (0..5).random()
        )

        val initialMessages = listOf(
            ChatMessage(
                messageId = "${convId}_m1",
                conversationId = convId,
                senderName = "You",
                isFromMe = true,
                text = cleanPreview,
                timeLabel = currentFormattedTime(),
                deliveryStatus = "read"
            )
        )

        _uiState.update { state ->
            state.copy(
                conversations = listOf(localItem) + state.conversations,
                messagesByConversation = state.messagesByConversation + (convId to initialMessages),
                activeFabActionSheet = null,
                activeOpenedConversation = localItem,
                selectedTab = ArenaDashboardTab.CHATS,
                selectedChatFilter = ChatFilterType.ALL
            )
        }

        if (auth.currentUser != null) {
            viewModelScope.launch {
                runCatching {
                    repository.createConversation(
                        displayName = cleanTitle,
                        handleOrArenaId = cleanHandle,
                        category = actionType.defaultCategory,
                        lastMessagePreview = cleanPreview,
                        isVerified = localItem.isVerified,
                        memberCount = defaultMembers,
                        avatarPreset = localItem.avatarPreset
                    )
                }
            }
        }
    }

    // ========================================================================
    // Stories / Status Actions (Section 2.D)
    // ========================================================================

    fun viewStory(story: StoryItem?) {
        if (story != null) {
            _uiState.update { state ->
                val updated = state.stories.map {
                    if (it.storyId == story.storyId) it.copy(hasUnseen = false) else it
                }
                state.copy(
                    stories = updated,
                    activeViewingStory = story.copy(hasUnseen = false)
                )
            }
            if (auth.currentUser != null && story.hasUnseen) {
                viewModelScope.launch {
                    runCatching { repository.markStoryViewed(story.storyId) }
                }
            }
        } else {
            _uiState.update { it.copy(activeViewingStory = null) }
        }
    }

    fun setShowAddStoryModal(show: Boolean) {
        _uiState.update { it.copy(showAddStoryModal = show) }
    }

    fun setShowStoryPrivacyModal(show: Boolean) {
        _uiState.update { it.copy(showStoryPrivacyModal = show) }
    }

    fun publishUserStory(
        authorName: String,
        authorUsername: String,
        caption: String,
        privacyLevel: String,
        accentPreset: Int
    ) {
        val cleanCaption = caption.trim().ifBlank { "Connecting the world on ARENA CHAT ✨" }
        _uiState.update { state ->
            val updatedOwn = StoryItem(
                storyId = "story_own_${auth.currentUser?.uid ?: "local"}",
                ownerId = auth.currentUser?.uid ?: "local",
                authorName = authorName.ifBlank { "My Status" },
                authorUsername = authorUsername.ifBlank { "arena_member" },
                caption = cleanCaption,
                timestampLabel = "Just now",
                isOwnStory = true,
                hasUnseen = true,
                privacyLevel = privacyLevel,
                accentPreset = accentPreset
            )
            val others = state.stories.filterNot { it.isOwnStory }
            state.copy(
                stories = listOf(updatedOwn) + others,
                showAddStoryModal = false,
                statusBannerMessage = "Your Status Story is now live"
            )
        }
        if (auth.currentUser != null) {
            viewModelScope.launch {
                runCatching {
                    repository.publishStory(
                        authorName = authorName,
                        authorUsername = authorUsername,
                        caption = cleanCaption,
                        privacyLevel = privacyLevel,
                        accentPreset = accentPreset
                    )
                }
            }
        }
    }

    // ========================================================================
    // Notification Center Actions (Section 5)
    // ========================================================================

    fun setShowNotificationCenter(show: Boolean) {
        _uiState.update { it.copy(showNotificationCenter = show) }
    }

    fun markNotificationAsRead(notificationId: String) {
        _uiState.update { state ->
            state.copy(
                notifications = state.notifications.map {
                    if (it.notificationId == notificationId) it.copy(isRead = true) else it
                }
            )
        }
        if (auth.currentUser != null) {
            viewModelScope.launch {
                runCatching { repository.markNotificationRead(notificationId) }
            }
        }
    }

    fun markAllNotificationsAsRead() {
        val ids = _uiState.value.notifications.filter { !it.isRead }.map { it.notificationId }
        _uiState.update { state ->
            state.copy(
                notifications = state.notifications.map { it.copy(isRead = true) },
                statusBannerMessage = "All notifications marked as read"
            )
        }
        if (auth.currentUser != null && ids.isNotEmpty()) {
            viewModelScope.launch {
                runCatching { repository.markAllNotificationsRead(ids) }
            }
        }
    }

    fun clearAllNotifications() {
        val ids = _uiState.value.notifications.map { it.notificationId }
        _uiState.update { state ->
            state.copy(
                notifications = emptyList(),
                statusBannerMessage = "Cleared all notifications"
            )
        }
        if (auth.currentUser != null && ids.isNotEmpty()) {
            viewModelScope.launch {
                runCatching { repository.clearAllNotifications(ids) }
            }
        }
    }

    // ========================================================================
    // Profile & Settings Access Actions (Section 6)
    // ========================================================================

    fun openSettingsSection(section: ProfileSettingsSection?) {
        _uiState.update { it.copy(activeSettingsSection = section) }
    }

    fun updateUserSettings(updated: ArenaUserSettings) {
        _uiState.update {
            it.copy(
                userSettings = updated,
                isOnlineStatus = updated.presenceStatus == "online",
                showStoryPrivacyModal = false,
                statusBannerMessage = "Preferences saved"
            )
        }
        if (auth.currentUser != null) {
            viewModelScope.launch {
                runCatching { repository.saveUserSettings(updated) }
            }
        }
    }

    // ========================================================================
    // ARENA AI Assistant Actions (Deep Chat, Live Search Mode, Image Studio)
    // ========================================================================

    fun openArenaAiAssistant(
        mode: ArenaAiMode = ArenaAiMode.DEEP_CHAT,
        initialPrompt: String = ""
    ) {
        _uiState.update {
            it.copy(
                showArenaAiAssistant = true,
                arenaAiMode = mode,
                arenaAiInitialDraft = initialPrompt,
                isFabMenuExpanded = false
            )
        }
        if (initialPrompt.trim().isNotEmpty()) {
            sendArenaAiPrompt(initialPrompt.trim(), mode)
        }
    }

    fun closeArenaAiAssistant() {
        _uiState.update {
            it.copy(
                showArenaAiAssistant = false,
                arenaAiInitialDraft = ""
            )
        }
    }

    fun selectArenaAiMode(mode: ArenaAiMode) {
        _uiState.update { it.copy(arenaAiMode = mode) }
    }

    fun clearArenaAiHistory() {
        arenaAiJob?.cancel()
        _uiState.update {
            it.copy(
                arenaAiMessages = defaultArenaAiWelcomeMessages(),
                isArenaAiGenerating = false,
                arenaAiInitialDraft = ""
            )
        }
    }

    fun sendArenaAiPrompt(
        prompt: String,
        mode: ArenaAiMode = ArenaAiService.detectAutomaticIntent(prompt)
    ) {
        val cleanPrompt = prompt.trim()
        if (cleanPrompt.isEmpty()) return
        val autoMode = ArenaAiService.detectAutomaticIntent(cleanPrompt)

        val timeNow = currentFormattedTime()
        val userMessage = ArenaAiMessage(
            id = "ai_user_${System.currentTimeMillis()}",
            isUser = true,
            text = cleanPrompt,
            timeLabel = timeNow,
            mode = autoMode
        )
        val aiResponseId = "ai_reply_${System.currentTimeMillis() + 1}"
        val placeholderAiMessage = ArenaAiMessage(
            id = aiResponseId,
            isUser = false,
            text = when (autoMode) {
                ArenaAiMode.IMAGE_STUDIO -> "Automatically synthesizing 8K visual..."
                ArenaAiMode.LIVE_WEB_SEARCH -> "Automatically grounding live web facts..."
                ArenaAiMode.DEEP_CHAT -> "Synthesizing response..."
            },
            timeLabel = timeNow,
            mode = autoMode,
            isStreaming = true
        )

        _uiState.update { state ->
            state.copy(
                arenaAiMode = autoMode,
                arenaAiInitialDraft = "",
                isArenaAiGenerating = true,
                arenaAiMessages = state.arenaAiMessages + userMessage + placeholderAiMessage
            )
        }

        arenaAiJob?.cancel()
        arenaAiJob = viewModelScope.launch {
            try {
                if (autoMode == ArenaAiMode.IMAGE_STUDIO) {
                    val bitmap = ArenaAiService.generateArenaAiImage(cleanPrompt)
                    val caption = "Generated 8K-style artwork for: \"$cleanPrompt\""
                    _uiState.update { state ->
                        val updated = state.arenaAiMessages.map { msg ->
                            if (msg.id == aiResponseId) {
                                msg.copy(
                                    text = caption,
                                    generatedBitmap = bitmap,
                                    isStreaming = false,
                                    sourceHighlights = listOf("Arena AI Image Studio • gemini-nano-banana-2.1")
                                )
                            } else {
                                msg
                            }
                        }
                        state.copy(
                            arenaAiMessages = updated,
                            isArenaAiGenerating = false
                        )
                    }
                } else {
                    var latestStreamedText = ""
                    val sources = ArenaAiService.extractSourceBadges(autoMode, cleanPrompt)
                    ArenaAiService.streamArenaAiResponse(
                        userPrompt = cleanPrompt,
                        mode = autoMode
                    ).collect { partialText ->
                        latestStreamedText = partialText
                        _uiState.update { state ->
                            val updated = state.arenaAiMessages.map { msg ->
                                if (msg.id == aiResponseId) {
                                    msg.copy(
                                        text = partialText,
                                        isStreaming = true,
                                        sourceHighlights = sources
                                    )
                                } else {
                                    msg
                                }
                            }
                            state.copy(arenaAiMessages = updated)
                        }
                    }

                    val finalText = latestStreamedText.ifBlank {
                        ArenaAiService.buildSmartOfflineFallback(cleanPrompt, autoMode)
                    }
                    _uiState.update { state ->
                        val updated = state.arenaAiMessages.map { msg ->
                            if (msg.id == aiResponseId) {
                                msg.copy(
                                    text = finalText,
                                    isStreaming = false,
                                    sourceHighlights = sources
                                )
                            } else {
                                msg
                            }
                        }
                        state.copy(
                            arenaAiMessages = updated,
                            isArenaAiGenerating = false
                        )
                    }
                }
            } catch (e: TimeoutCancellationException) {
                ArenaAiService.logAiError("Arena AI request timed out", e)
                val fallback = ArenaAiService.buildSmartOfflineFallback(cleanPrompt, autoMode)
                _uiState.update { state ->
                    val updated = state.arenaAiMessages.map { msg ->
                        if (msg.id == aiResponseId) {
                            msg.copy(
                                text = fallback,
                                isStreaming = false,
                                sourceHighlights = ArenaAiService.extractSourceBadges(autoMode, cleanPrompt)
                            )
                        } else {
                            msg
                        }
                    }
                    state.copy(
                        arenaAiMessages = updated,
                        isArenaAiGenerating = false
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                ArenaAiService.logAiError("Arena AI request failed", e)
                val fallback = ArenaAiService.buildSmartOfflineFallback(cleanPrompt, autoMode)
                _uiState.update { state ->
                    val updated = state.arenaAiMessages.map { msg ->
                        if (msg.id == aiResponseId) {
                            msg.copy(
                                text = fallback,
                                isStreaming = false,
                                sourceHighlights = ArenaAiService.extractSourceBadges(autoMode, cleanPrompt)
                            )
                        } else {
                            msg
                        }
                    }
                    state.copy(
                        arenaAiMessages = updated,
                        isArenaAiGenerating = false
                    )
                }
            }
        }
    }

    fun shareArenaAiMessageToConversation(
        conversation: ConversationItem,
        aiMessage: ArenaAiMessage
    ) {
        val timeNow = currentFormattedTime()
        val sharedMsg = if (aiMessage.generatedBitmap != null) {
            ChatMessage(
                messageId = "msg_ai_img_${System.currentTimeMillis()}",
                conversationId = conversation.conversationId,
                ownerId = auth.currentUser?.uid ?: "local",
                senderName = "You (via ARENA AI)",
                isFromMe = true,
                text = "✨ ARENA AI Artwork: ${aiMessage.text}",
                timeLabel = timeNow,
                deliveryStatus = "sent",
                messageType = ChatMessageType.IMAGE,
                generatedBitmap = aiMessage.generatedBitmap,
                sequenceOrder = System.currentTimeMillis()
            )
        } else {
            ChatMessage(
                messageId = "msg_ai_txt_${System.currentTimeMillis()}",
                conversationId = conversation.conversationId,
                ownerId = auth.currentUser?.uid ?: "local",
                senderName = "You (via ARENA AI)",
                isFromMe = true,
                text = "✨ Shared from ARENA AI (${aiMessage.mode.label}):\n${aiMessage.text}",
                timeLabel = timeNow,
                deliveryStatus = "sent",
                messageType = ChatMessageType.TEXT,
                sequenceOrder = System.currentTimeMillis()
            )
        }

        appendOutgoingMessageAndSimulateLifecycle(
            conversationId = conversation.conversationId,
            outgoingMessage = sharedMsg,
            previewLabel = sharedMsg.text.lineSequence().firstOrNull()?.take(70) ?: "Shared from ARENA AI"
        )

        _uiState.update {
            it.copy(
                showArenaAiAssistant = false,
                activeOpenedConversation = conversation,
                statusBannerMessage = "Shared ARENA AI response to ${conversation.displayName}"
            )
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                val dbId = app.getString(R.string.firestore_database_id)
                val db = FirebaseFirestore.getInstance(dbId)
                val auth = Firebase.auth
                ArenaDashboardViewModel(
                    repository = ArenaDashboardRepository(db, auth),
                    auth = auth
                )
            }
        }
    }
}
