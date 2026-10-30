package com.example.ui.screens.dashboard

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.ArenaDashboardTab
import com.example.data.model.ChatFilterType
import com.example.data.model.FabNewActionType
import com.example.data.model.ProfileSettingsSection
import com.example.ui.theme.ArenaEmeraldDark
import com.example.ui.theme.ArenaMintSoft
import com.example.ui.theme.ArenaTextSecondary
import com.example.ui.theme.PlusJakartaSansFont
import com.example.ui.viewmodel.ArenaDashboardViewModel
import com.example.ui.viewmodel.ArenaOnboardingUiState

@Composable
fun ArenaMainDashboardScreen(
    onboardingState: ArenaOnboardingUiState,
    dashboardViewModel: ArenaDashboardViewModel = viewModel(factory = ArenaDashboardViewModel.Factory),
    onBackToOnboardingClick: () -> Unit,
    onSignOutClick: () -> Unit,
    onRequestGoogleSignIn: () -> Unit
) {
    val dashState by dashboardViewModel.uiState.collectAsStateWithLifecycle()

    val username = onboardingState.userProfile?.username?.takeIf { it.isNotBlank() }
        ?: onboardingState.usernameInput.takeIf { it.isNotBlank() }
        ?: onboardingState.email.substringBefore("@").takeIf { it.isNotBlank() }
        ?: "arena_member"

    val displayName = username
        .split("_", ".")
        .filter { it.isNotBlank() }
        .joinToString(" ") { part ->
            part.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
        .ifBlank { "Arena Member" }

    val uidSuffix = (onboardingState.currentUserId ?: username).hashCode()
        .let { kotlin.math.abs(it) % 9000 + 1000 }
    val arenaId = "ARENA-$uidSuffix"

    val isHumanVerified = onboardingState.userProfile?.humanVerified == true ||
        onboardingState.verificationChallengeCompleted

    val displayedConversations = dashboardViewModel.getDisplayedConversations(dashState)
    val unreadChatsCount = dashState.conversations.count { !it.isArchived && it.unreadCount > 0 }
    val archivedCount = dashState.conversations.count { it.isArchived }
    val missedCallsCount = dashState.callHistory.count { it.isMissed }
    val unreadNotificationsCount = dashState.notifications.count { !it.isRead }

    BackHandler {
        when {
            dashState.activeCallSession != null -> dashboardViewModel.endActiveCallSession()
            dashState.showArenaAiAssistant -> dashboardViewModel.closeArenaAiAssistant()
            dashState.activeSettingsSection != null -> dashboardViewModel.openSettingsSection(null)
            dashState.showNotificationCenter -> dashboardViewModel.setShowNotificationCenter(false)
            dashState.confirmDeleteConversation != null -> dashboardViewModel.requestDeleteConversation(null)
            dashState.contextMenuConversation != null -> dashboardViewModel.openConversationContextMenu(null)
            dashState.activeContactProfileSheet != null -> dashboardViewModel.openContactProfileSheet(null)
            dashState.activeFabActionSheet != null -> dashboardViewModel.openFabCreationFlow(null)
            dashState.activeViewingStory != null -> dashboardViewModel.viewStory(null)
            dashState.showAddStoryModal -> dashboardViewModel.setShowAddStoryModal(false)
            dashState.showStoryPrivacyModal -> dashboardViewModel.setShowStoryPrivacyModal(false)
            dashState.activeOpenedConversation != null -> dashboardViewModel.closeOpenedConversation()
            dashState.isFabMenuExpanded -> dashboardViewModel.dismissFabMenu()
            dashState.isSearchExpanded -> dashboardViewModel.setSearchExpanded(false)
            dashState.selectedTab != ArenaDashboardTab.CHATS -> dashboardViewModel.selectTab(ArenaDashboardTab.CHATS)
            else -> onBackToOnboardingClick()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("arena_main_dashboard_screen")
    ) {
        // ====================================================================
        // MAIN WHATSAPP-STYLE DASHBOARD
        // ====================================================================
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            val isWideScreen = maxWidth >= 700.dp

            Row(modifier = Modifier.fillMaxSize()) {
                if (isWideScreen) {
                    ArenaSixSectionNavigationRail(
                        selectedTab = dashState.selectedTab,
                        unreadChatsCount = unreadChatsCount,
                        missedCallsCount = missedCallsCount,
                        onSelectTab = dashboardViewModel::selectTab
                    )
                }

                Scaffold(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .testTag("dashboard_scaffold"),
                    containerColor = Color.White,
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                    topBar = {
                        ArenaDashboardTopHeader(
                            displayName = displayName,
                            profileAvatarUri = onboardingState.profileAvatarUri,
                            selectedAvatarPreset = onboardingState.selectedAvatarPreset,
                            isOnline = dashState.isOnlineStatus,
                            unreadNotificationCount = unreadNotificationsCount,
                            isSearchExpanded = dashState.isSearchExpanded,
                            onToggleOnlineStatus = dashboardViewModel::toggleOnlinePresence,
                            onToggleSearch = { dashboardViewModel.setSearchExpanded(!dashState.isSearchExpanded) },
                            onOpenNotifications = { dashboardViewModel.setShowNotificationCenter(true) },
                            onOpenSettings = {
                                dashboardViewModel.openSettingsSection(ProfileSettingsSection.PRIVACY)
                            },
                            onOpenProfileTab = {
                                dashboardViewModel.selectTab(ArenaDashboardTab.PROFILE)
                            },
                            onQuickCameraStatus = {
                                dashboardViewModel.setShowAddStoryModal(true)
                            }
                        )
                    },
                    bottomBar = {
                        if (!isWideScreen) {
                            ArenaSixSectionBottomNavigation(
                                selectedTab = dashState.selectedTab,
                                unreadChatsCount = unreadChatsCount,
                                missedCallsCount = missedCallsCount,
                                onSelectTab = dashboardViewModel::selectTab
                            )
                        }
                    },
                    floatingActionButton = {
                        if (dashState.selectedTab != ArenaDashboardTab.CHATS &&
                            dashState.selectedTab != ArenaDashboardTab.PROFILE
                        ) {
                            ArenaDashboardFabAndMenu(
                                isExpanded = dashState.isFabMenuExpanded,
                                onToggleExpand = dashboardViewModel::toggleFabMenu,
                                onSelectAction = dashboardViewModel::openFabCreationFlow,
                                onOpenArenaAi = {
                                    dashboardViewModel.openArenaAiAssistant()
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        AnimatedVisibility(visible = dashState.statusBannerMessage != null) {
                            dashState.statusBannerMessage?.let { msg ->
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 4.dp)
                                        .border(1.dp, ArenaPrimaryEmerald, RoundedCornerShape(10.dp))
                                        .testTag("dashboard_status_banner"),
                                    color = ArenaMintSoft,
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 7.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = ArenaPrimaryEmerald,
                                                modifier = Modifier.size(15.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = msg,
                                                style = TextStyle(
                                                    fontFamily = PlusJakartaSansFont,
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 12.sp,
                                                    color = ArenaEmeraldDark
                                                )
                                            )
                                        }
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Dismiss Banner",
                                            tint = ArenaEmeraldDark,
                                            modifier = Modifier
                                                .size(16.dp)
                                                .clickable { dashboardViewModel.clearBanner() }
                                        )
                                    }
                                }
                            }
                        }

                        Box(modifier = Modifier.weight(1f)) {
                            when (dashState.selectedTab) {
                                ArenaDashboardTab.CHATS -> {
                                    ChatListScreen(
                                        conversations = displayedConversations,
                                        allConversationsForCounts = dashState.conversations,
                                        selectedFilter = dashState.selectedChatFilter,
                                        searchQuery = dashState.searchQuery,
                                        isSearchExpanded = dashState.isSearchExpanded,
                                        selectedSearchCategory = dashState.selectedSearchCategory,
                                        isSearching = dashState.isSearching,
                                        recentSearches = dashState.recentSearches,
                                        searchSuggestions = dashState.searchSuggestions,
                                        stories = dashState.stories,
                                        displayName = displayName,
                                        username = username,
                                        arenaId = arenaId,
                                        isOnline = dashState.isOnlineStatus,
                                        isHumanVerified = isHumanVerified,
                                        isCloudSynced = dashState.isCloudSynced,
                                        profileAvatarUri = onboardingState.profileAvatarUri,
                                        selectedAvatarPreset = onboardingState.selectedAvatarPreset,
                                        archivedCount = archivedCount,
                                        isFabMenuExpanded = dashState.isFabMenuExpanded,
                                        onToggleFabMenu = dashboardViewModel::toggleFabMenu,
                                        onSelectFabAction = dashboardViewModel::openFabCreationFlow,
                                        onOpenConversation = dashboardViewModel::openConversation,
                                        onOpenProfile = dashboardViewModel::openContactProfileSheet,
                                        onOpenContextMenu = dashboardViewModel::openConversationContextMenu,
                                        onSelectFilter = dashboardViewModel::selectChatFilter,
                                        onSearchQueryChange = dashboardViewModel::updateSearchQuery,
                                        onSearchFocusExpand = { dashboardViewModel.setSearchExpanded(true) },
                                        onSelectSearchCategory = dashboardViewModel::selectSearchCategory,
                                        onClearRecentSearches = dashboardViewModel::clearRecentSearches,
                                        onAddStoryClick = { dashboardViewModel.setShowAddStoryModal(true) },
                                        onViewStoryClick = { story -> dashboardViewModel.viewStory(story) },
                                        onOpenStoryPrivacyClick = {
                                            dashboardViewModel.openSettingsSection(ProfileSettingsSection.PRIVACY)
                                        },
                                        onToggleOnlineStatus = dashboardViewModel::toggleOnlinePresence,
                                        onOpenUserProfileTab = {
                                            dashboardViewModel.selectTab(ArenaDashboardTab.PROFILE)
                                        },
                                        onRequestCloudSync = onRequestGoogleSignIn,
                                        onOpenArenaAi = { mode, initialQuery ->
                                            dashboardViewModel.openArenaAiAssistant(
                                                mode = mode,
                                                initialPrompt = initialQuery
                                            )
                                        }
                                    )
                                }

                                ArenaDashboardTab.CALLS -> {
                                    ArenaCallsTabContent(
                                        calls = dashState.callHistory,
                                        onCallActionClick = { callEntry, isVideoOverride ->
                                            dashboardViewModel.startCallFromHistory(callEntry, isVideoOverride)
                                        },
                                        onClearCallHistory = dashboardViewModel::clearCallHistory,
                                        onCopyCallLink = dashboardViewModel::showStatusBanner
                                    )
                                }

                                ArenaDashboardTab.COMMUNITIES -> {
                                    ArenaCommunitiesTabContent(
                                        conversations = dashState.conversations,
                                        onOpenCommunity = dashboardViewModel::openConversation,
                                        onOpenCommunityRoom = { community, roomName ->
                                            dashboardViewModel.openCommunitySubRoom(community, roomName)
                                        },
                                        onCreateCommunityClick = {
                                            dashboardViewModel.openFabCreationFlow(FabNewActionType.NEW_COMMUNITY)
                                        }
                                    )
                                }

                                ArenaDashboardTab.CHANNELS -> {
                                    ArenaChannelsTabContent(
                                        conversations = dashState.conversations,
                                        onOpenChannel = dashboardViewModel::openConversation,
                                        onOpenProfile = dashboardViewModel::openContactProfileSheet,
                                        onOpenContextMenu = dashboardViewModel::openConversationContextMenu,
                                        onCreateChannelClick = {
                                            dashboardViewModel.openFabCreationFlow(FabNewActionType.NEW_CHANNEL)
                                        },
                                        onFollowDirectoryChannel = { title, subtitle ->
                                            dashboardViewModel.showStatusBanner("Now following \"$title\" ($subtitle)")
                                        }
                                    )
                                }

                                ArenaDashboardTab.DISCOVER -> {
                                    ArenaDiscoverTabContent(
                                        onConnectDiscoverItem = { title, handle, category ->
                                            val action = when (category) {
                                                "community" -> FabNewActionType.NEW_COMMUNITY
                                                "channel" -> FabNewActionType.NEW_CHANNEL
                                                "group" -> FabNewActionType.NEW_GROUP
                                                else -> FabNewActionType.NEW_MESSAGE
                                            }
                                            dashboardViewModel.createNewItemFromFab(
                                                actionType = action,
                                                titleOrName = title,
                                                handleOrArenaId = handle,
                                                initialMessageOrDescription = "Connected via Arena Discover"
                                            )
                                        }
                                    )
                                }

                                ArenaDashboardTab.PROFILE -> {
                                    ArenaProfileTabContent(
                                        displayName = displayName,
                                        username = username,
                                        email = onboardingState.userProfile?.email ?: onboardingState.email,
                                        country = onboardingState.userProfile?.country ?: onboardingState.country,
                                        arenaId = arenaId,
                                        isHumanVerified = isHumanVerified,
                                        onOpenSettingsSection = dashboardViewModel::openSettingsSection,
                                        onBackToOnboardingClick = onBackToOnboardingClick,
                                        onSignOutClick = onSignOutClick,
                                        onStatusNoteChanged = dashboardViewModel::showStatusBanner
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // ====================================================================
        // FULL-SCREEN WHATSAPP CHAT SCREEN (Replaces the old popup dialog!)
        // ====================================================================
        dashState.activeOpenedConversation?.let { activeChat ->
            val chatMessages = dashState.messagesByConversation[activeChat.conversationId].orEmpty()
            ChatScreen(
                conversation = activeChat,
                messages = chatMessages,
                replyingToMessage = dashState.replyingToMessage,
                inChatSearchQuery = dashState.inChatSearchQuery,
                isInChatSearchActive = dashState.isInChatSearchActive,
                wallpaperPreset = dashState.chatWallpaperPreset,
                onBackClick = dashboardViewModel::closeOpenedConversation,
                onOpenContactInfo = {
                    dashboardViewModel.openContactProfileSheet(activeChat)
                },
                onStartVoiceCall = {
                    dashboardViewModel.startCallWithConversation(activeChat, isVideoCall = false)
                },
                onStartVideoCall = {
                    dashboardViewModel.startCallWithConversation(activeChat, isVideoCall = true)
                },
                onSendMessage = { msgText ->
                    dashboardViewModel.sendQuickMessageInConversation(activeChat.conversationId, msgText)
                },
                onSendVoiceNote = { durationSec ->
                    dashboardViewModel.sendVoiceNoteInConversation(activeChat.conversationId, durationSec)
                },
                onSendImageAttachment = { uriStr, caption ->
                    dashboardViewModel.sendImageAttachmentInConversation(activeChat.conversationId, uriStr, caption)
                },
                onSendDocument = { fileName, meta ->
                    dashboardViewModel.sendDocumentInConversation(activeChat.conversationId, fileName, meta)
                },
                onSendLocation = { title, coords ->
                    dashboardViewModel.sendLocationInConversation(activeChat.conversationId, title, coords)
                },
                onSendPoll = { question, options ->
                    dashboardViewModel.sendPollInConversation(activeChat.conversationId, question, options)
                },
                onVotePoll = { messageId, optionId ->
                    dashboardViewModel.voteOnPollOption(activeChat.conversationId, messageId, optionId)
                },
                onSetReplyMessage = dashboardViewModel::setReplyingToMessage,
                onToggleReaction = { messageId, emoji ->
                    dashboardViewModel.toggleMessageReaction(activeChat.conversationId, messageId, emoji)
                },
                onToggleStarMessage = { messageId ->
                    dashboardViewModel.toggleStarMessage(activeChat.conversationId, messageId)
                },
                onDeleteMessage = { messageId ->
                    dashboardViewModel.deleteMessageInConversation(activeChat.conversationId, messageId)
                },
                onToggleSearchActive = dashboardViewModel::setInChatSearchActive,
                onUpdateSearchQuery = dashboardViewModel::updateInChatSearchQuery,
                onToggleMuteChat = {
                    dashboardViewModel.toggleMuteConversation(activeChat)
                },
                onToggleFavoriteChat = {
                    dashboardViewModel.toggleFavoriteConversation(activeChat)
                },
                onCycleWallpaper = dashboardViewModel::cycleChatWallpaper,
                onClearChat = {
                    dashboardViewModel.clearConversationHistory(activeChat.conversationId)
                },
                onOpenArenaAi = { mode, initialPrompt ->
                    dashboardViewModel.openArenaAiAssistant(mode = mode, initialPrompt = initialPrompt)
                }
            )
        }

        // ====================================================================
        // FULL-SCREEN WHATSAPP ACTIVE VOICE / VIDEO CALL OVERLAY
        // ====================================================================
        dashState.activeCallSession?.let { activeCall ->
            WhatsAppActiveCallOverlay(
                callSession = activeCall,
                onToggleMute = dashboardViewModel::toggleActiveCallMute,
                onToggleSpeaker = dashboardViewModel::toggleActiveCallSpeaker,
                onToggleVideo = dashboardViewModel::toggleActiveCallVideo,
                onEndCall = { durationLabel ->
                    dashboardViewModel.endActiveCallSession(durationLabel)
                }
            )
        }

        // ====================================================================
        // FULL-SCREEN ARENA AI STUDIO (Deep Chat + Live Web Search + Image Gen)
        // ====================================================================
        if (dashState.showArenaAiAssistant) {
            ArenaAiAssistantScreen(
                messages = dashState.arenaAiMessages,
                activeMode = dashState.arenaAiMode,
                isGenerating = dashState.isArenaAiGenerating,
                conversations = dashState.conversations,
                initialDraftPrompt = dashState.arenaAiInitialDraft,
                onSelectMode = dashboardViewModel::selectArenaAiMode,
                onSendPrompt = { prompt, mode ->
                    dashboardViewModel.sendArenaAiPrompt(prompt, mode)
                },
                onClearHistory = dashboardViewModel::clearArenaAiHistory,
                onShareToConversation = { conversation, aiMessage ->
                    dashboardViewModel.shareArenaAiMessageToConversation(conversation, aiMessage)
                },
                onClose = dashboardViewModel::closeArenaAiAssistant
            )
        }
    }

    // ========================================================================
    // Interactive Modals, Sheets & Context Menus
    // ========================================================================

    if (dashState.showNotificationCenter) {
        ArenaNotificationCenterDialog(
            notifications = dashState.notifications,
            onDismiss = { dashboardViewModel.setShowNotificationCenter(false) },
            onMarkRead = dashboardViewModel::markNotificationAsRead,
            onMarkAllRead = dashboardViewModel::markAllNotificationsAsRead,
            onClearAll = dashboardViewModel::clearAllNotifications
        )
    }

    dashState.contextMenuConversation?.let { target ->
        ArenaConversationContextMenuDialog(
            conversation = target,
            onDismiss = { dashboardViewModel.openConversationContextMenu(null) },
            onOpenChat = { dashboardViewModel.openConversation(target) },
            onOpenProfile = { dashboardViewModel.openContactProfileSheet(target) },
            onTogglePin = { dashboardViewModel.togglePinConversation(target) },
            onToggleFavorite = { dashboardViewModel.toggleFavoriteConversation(target) },
            onToggleMute = { dashboardViewModel.toggleMuteConversation(target) },
            onToggleUnread = { dashboardViewModel.toggleUnreadConversation(target) },
            onToggleArchive = { dashboardViewModel.toggleArchiveConversation(target) },
            onRequestDelete = { dashboardViewModel.requestDeleteConversation(target) }
        )
    }

    dashState.confirmDeleteConversation?.let { target ->
        ArenaDeleteConfirmationDialog(
            conversation = target,
            onDismiss = { dashboardViewModel.requestDeleteConversation(null) },
            onConfirmDelete = dashboardViewModel::confirmDeleteConversationNow
        )
    }

    dashState.activeContactProfileSheet?.let { contact ->
        val msgCount = dashState.messagesByConversation[contact.conversationId]?.size ?: 1
        WhatsAppContactInfoModal(
            conversation = contact,
            messageCount = msgCount,
            onDismiss = { dashboardViewModel.openContactProfileSheet(null) },
            onOpenChat = { dashboardViewModel.openConversation(contact) },
            onStartVoiceCall = { dashboardViewModel.startCallWithConversation(contact, isVideoCall = false) },
            onStartVideoCall = { dashboardViewModel.startCallWithConversation(contact, isVideoCall = true) },
            onToggleMute = { dashboardViewModel.toggleMuteConversation(contact) },
            onToggleFavorite = { dashboardViewModel.toggleFavoriteConversation(contact) },
            onRequestDelete = { dashboardViewModel.requestDeleteConversation(contact) }
        )
    }

    dashState.activeFabActionSheet?.let { actionType ->
        ArenaFabCreationModal(
            actionType = actionType,
            onDismiss = { dashboardViewModel.openFabCreationFlow(null) },
            onCreate = { name, handle, msg ->
                dashboardViewModel.createNewItemFromFab(actionType, name, handle, msg)
            }
        )
    }

    dashState.activeViewingStory?.let { story ->
        ArenaStoryViewerModal(
            story = story,
            onDismiss = { dashboardViewModel.viewStory(null) },
            onReplyToStory = { replyText ->
                dashboardViewModel.replyToStory(story, replyText)
            }
        )
    }

    if (dashState.showAddStoryModal) {
        ArenaAddStoryModal(
            defaultAuthorName = displayName,
            defaultUsername = username,
            currentPrivacy = dashState.userSettings.storyPrivacy,
            onDismiss = { dashboardViewModel.setShowAddStoryModal(false) },
            onPublish = { caption, privacy, preset ->
                dashboardViewModel.publishUserStory(displayName, username, caption, privacy, preset)
            }
        )
    }

    dashState.activeSettingsSection?.let { section ->
        ArenaSettingsSectionModal(
            section = section,
            settings = dashState.userSettings,
            arenaId = arenaId,
            username = username,
            onDismiss = { dashboardViewModel.openSettingsSection(null) },
            onSaveSettings = dashboardViewModel::updateUserSettings
        )
    }
}
