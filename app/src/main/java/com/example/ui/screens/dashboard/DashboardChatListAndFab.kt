package com.example.ui.screens.dashboard

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Hub
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ArenaAiMode
import com.example.data.model.ArenaDashboardTab
import com.example.data.model.ChatFilterType
import com.example.data.model.ConversationItem
import com.example.data.model.FabNewActionType
import com.example.data.model.GlobalSearchCategory
import com.example.data.model.StoryItem
import com.example.data.repository.ArenaDashboardRepository
import com.example.ui.theme.ArenaEmeraldDark
import com.example.ui.theme.ArenaEmeraldDeep
import com.example.ui.theme.ArenaEmeraldVibrant
import com.example.ui.theme.ArenaGoldDark
import com.example.ui.theme.ArenaGoldLight
import com.example.ui.theme.ArenaGoldPrimary
import com.example.ui.theme.ArenaMintBorder
import com.example.ui.theme.ArenaMintSoft
import com.example.ui.theme.ArenaTextMuted
import com.example.ui.theme.ArenaTextSecondary
import com.example.ui.theme.MontserratFont
import com.example.ui.theme.PlusJakartaSansFont

@Composable
fun ChatListScreen(
    conversations: List<ConversationItem> = ArenaDashboardRepository.defaultConversations().filter { !it.isArchived },
    allConversationsForCounts: List<ConversationItem> = conversations,
    selectedFilter: ChatFilterType = ChatFilterType.ALL,
    searchQuery: String = "",
    isSearchExpanded: Boolean = false,
    selectedSearchCategory: GlobalSearchCategory = GlobalSearchCategory.PEOPLE,
    isSearching: Boolean = false,
    recentSearches: List<String> = listOf("@elena_vance", "ARENA-9042", "Encrypted Voice"),
    searchSuggestions: List<String> = listOf("@arena_official", "ARENA-3319", "Design Systems"),
    stories: List<StoryItem> = ArenaDashboardRepository.defaultStories(),
    displayName: String = "Arena Member",
    username: String = "arena_member",
    arenaId: String = "ARENA-1024",
    isOnline: Boolean = true,
    isHumanVerified: Boolean = true,
    isCloudSynced: Boolean = false,
    profileAvatarUri: String? = null,
    selectedAvatarPreset: Int = 0,
    archivedCount: Int = allConversationsForCounts.count { it.isArchived },
    isFabMenuExpanded: Boolean? = null,
    onToggleFabMenu: () -> Unit = {},
    onSelectFabAction: (FabNewActionType) -> Unit = {},
    onOpenConversation: (ConversationItem) -> Unit = {},
    onOpenProfile: (ConversationItem) -> Unit = {},
    onOpenContextMenu: (ConversationItem) -> Unit = {},
    onSelectFilter: (ChatFilterType) -> Unit = {},
    onSearchQueryChange: (String) -> Unit = {},
    onSearchFocusExpand: () -> Unit = {},
    onSelectSearchCategory: (GlobalSearchCategory) -> Unit = {},
    onClearRecentSearches: () -> Unit = {},
    onAddStoryClick: () -> Unit = {},
    onViewStoryClick: (StoryItem) -> Unit = {},
    onOpenStoryPrivacyClick: () -> Unit = {},
    onToggleOnlineStatus: () -> Unit = {},
    onOpenUserProfileTab: () -> Unit = {},
    onRequestCloudSync: () -> Unit = {},
    onOpenArenaAi: (ArenaAiMode, String) -> Unit = { _, _ -> }
) {
    var localFabExpanded by remember { mutableStateOf(false) }
    val effectiveFabExpanded = isFabMenuExpanded ?: localFabExpanded

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("chat_list_screen"),
        containerColor = Color.White,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        floatingActionButton = {
            ArenaDashboardFabAndMenu(
                isExpanded = effectiveFabExpanded,
                onToggleExpand = {
                    if (isFabMenuExpanded == null) {
                        localFabExpanded = !localFabExpanded
                    }
                    onToggleFabMenu()
                },
                onSelectAction = { action ->
                    localFabExpanded = false
                    onSelectFabAction(action)
                },
                onOpenArenaAi = {
                    localFabExpanded = false
                    onOpenArenaAi(ArenaAiMode.DEEP_CHAT, "")
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("chats_tab_lazy_column"),
            contentPadding = PaddingValues(bottom = 132.dp)
        ) {
            // C. WhatsApp Pill Global Search Bar + ARENA AI Live Search Mode
            item {
                ArenaGlobalSearchSection(
                    searchQuery = searchQuery,
                    isSearchExpanded = isSearchExpanded,
                    selectedCategory = selectedSearchCategory,
                    isSearching = isSearching,
                    resultCount = conversations.size,
                    recentSearches = recentSearches,
                    suggestions = searchSuggestions,
                    onSearchQueryChange = onSearchQueryChange,
                    onSearchFocusExpand = onSearchFocusExpand,
                    onSelectCategory = onSelectSearchCategory,
                    onClearRecentSearches = onClearRecentSearches,
                    onOpenArenaAiSearch = { query, mode ->
                        onOpenArenaAi(mode, query)
                    }
                )
            }

            // E. WhatsApp Horizontal Filter Pills (All, Unread, Personal, Groups, Favorites, Archived)
            item {
                ArenaChatFiltersRow(
                    selectedFilter = selectedFilter,
                    conversations = allConversationsForCounts,
                    onSelectFilter = onSelectFilter
                )
            }

            // D. Compact Status / Stories Strip
            item {
                ArenaStoriesSection(
                    stories = stories,
                    onAddStoryClick = onAddStoryClick,
                    onViewStoryClick = onViewStoryClick,
                    onOpenStoryPrivacyClick = onOpenStoryPrivacyClick
                )
            }

            // B. Compact Welcome / Identity Line
            item {
                ArenaWelcomeProfileBanner(
                    displayName = displayName,
                    username = username,
                    arenaId = arenaId,
                    isOnline = isOnline,
                    isHumanVerified = isHumanVerified,
                    profileAvatarUri = profileAvatarUri,
                    selectedAvatarPreset = selectedAvatarPreset,
                    onToggleOnlineStatus = onToggleOnlineStatus,
                    onOpenProfileClick = onOpenUserProfileTab
                )
            }

            // F. Edge-to-Edge WhatsApp Recent Conversations List
            item {
                ArenaChatListSection(
                    conversations = conversations,
                    selectedFilter = selectedFilter,
                    searchQuery = searchQuery,
                    archivedCount = archivedCount,
                    onSelectArchivedFilter = {
                        onSelectFilter(ChatFilterType.ARCHIVED)
                    },
                    onOpenConversation = onOpenConversation,
                    onOpenProfile = onOpenProfile,
                    onOpenContextMenu = onOpenContextMenu,
                    onResetFilters = {
                        onSelectFilter(ChatFilterType.ALL)
                        onSearchQueryChange("")
                    },
                    onStartNewChat = {
                        onSelectFabAction(FabNewActionType.NEW_MESSAGE)
                    }
                )
            }

            // Subtle Encrypted Footer + Cloud Sync Status
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isCloudSynced) {
                            Icons.Default.CloudDone
                        } else {
                            Icons.Default.CloudQueue
                        },
                        contentDescription = null,
                        tint = ArenaPrimaryEmerald,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isCloudSynced) {
                            "Your personal messages are end-to-end encrypted • Cloud Synced"
                        } else {
                            "Your personal messages are end-to-end encrypted • Tap to sync"
                        },
                        style = TextStyle(
                            fontFamily = PlusJakartaSansFont,
                            fontSize = 11.sp,
                            color = ArenaTextSecondary
                        ),
                        modifier = Modifier.clickable {
                            if (!isCloudSynced) {
                                onRequestCloudSync()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ArenaChatFiltersRow(
    selectedFilter: ChatFilterType,
    conversations: List<ConversationItem>,
    onSelectFilter: (ChatFilterType) -> Unit
) {
    val unreadTotal = conversations.count { !it.isArchived && it.unreadCount > 0 }
    val archivedTotal = conversations.count { it.isArchived }
    val favoritesTotal = conversations.count { !it.isArchived && it.isFavorite }

    Surface(
        color = Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .testTag("chat_filters_row"),
            contentPadding = PaddingValues(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(ChatFilterType.entries) { filter ->
                val isSelected = filter == selectedFilter
                val badgeCount = when (filter) {
                    ChatFilterType.UNREAD -> unreadTotal
                    ChatFilterType.FAVORITES -> favoritesTotal
                    ChatFilterType.ARCHIVED -> archivedTotal
                    else -> 0
                }

                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { onSelectFilter(filter) }
                        .testTag("chat_filter_${filter.name.lowercase()}"),
                    color = if (isSelected) Color(0xFFD9FDD3) else Color(0xFFF0F2F5),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = filter.label,
                            style = TextStyle(
                                fontFamily = PlusJakartaSansFont,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = if (isSelected) ArenaPrimaryEmerald else Color(0xFF54656F)
                            )
                        )
                        if (badgeCount > 0) {
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = badgeCount.toString(),
                                style = TextStyle(
                                    fontFamily = PlusJakartaSansFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (isSelected) ArenaPrimaryEmerald else Color(0xFF54656F)
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ArenaConversationItemCard(
    item: ConversationItem,
    onOpenConversation: (ConversationItem) -> Unit,
    onOpenProfile: (ConversationItem) -> Unit,
    onOpenContextMenu: (ConversationItem) -> Unit
) {
    // Authentic WhatsApp flat edge-to-edge row with circular user profile avatar and subtle divider
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { onOpenConversation(item) },
                onLongClick = { onOpenContextMenu(item) }
            )
            .testTag("conversation_item_${item.conversationId}"),
        color = Color.White
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 14.dp, end = 10.dp, top = 11.dp, bottom = 11.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circular User Profile Avatar + Online Status Dot / Category Badge
                CircularConversationUserAvatar(
                    item = item,
                    onClick = { onOpenProfile(item) }
                )

                Spacer(modifier = Modifier.width(14.dp))

                // Center Content: Top Row (Name + Timestamp), Bottom Row (Ticks + Preview + Badges)
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = item.displayName,
                                style = TextStyle(
                                    fontFamily = PlusJakartaSansFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color(0xFF111B21)
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (item.isVerified) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Verified",
                                    tint = Color(0xFF00A884),
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                            if (item.isFavorite) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Favorite",
                                    tint = ArenaGoldPrimary,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }

                        // WhatsApp Timestamp (Green when unread, gray otherwise)
                        Text(
                            text = item.lastMessageTimeLabel,
                            style = TextStyle(
                                fontFamily = PlusJakartaSansFont,
                                fontWeight = if (item.unreadCount > 0) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp,
                                color = if (item.unreadCount > 0) Color(0xFF00A884) else Color(0xFF667781)
                            ),
                            modifier = Modifier.testTag("conversation_timestamp_${item.conversationId}")
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("conversation_preview_${item.conversationId}")
                        ) {
                            when {
                                item.isTyping -> {
                                    Text(
                                        text = "typing...",
                                        style = TextStyle(
                                            fontFamily = PlusJakartaSansFont,
                                            fontWeight = FontWeight.Bold,
                                            fontStyle = FontStyle.Italic,
                                            fontSize = 13.5.sp,
                                            color = Color(0xFF00A884)
                                        )
                                    )
                                }
                                item.draftText.isNotBlank() || item.deliveryStatus == "draft" -> {
                                    Text(
                                        text = "Draft: ",
                                        style = TextStyle(
                                            fontFamily = PlusJakartaSansFont,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.5.sp,
                                            color = Color(0xFFD9480F)
                                        )
                                    )
                                    Text(
                                        text = item.draftText.ifBlank { item.lastMessagePreview },
                                        style = TextStyle(
                                            fontFamily = PlusJakartaSansFont,
                                            fontSize = 13.5.sp,
                                            color = Color(0xFF667781)
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                else -> {
                                    val statusIcon = if (item.deliveryStatus == "sent") {
                                        Icons.Default.Done
                                    } else {
                                        Icons.Default.DoneAll
                                    }
                                    val statusTint = if (item.deliveryStatus == "read") {
                                        Color(0xFF53BDEB)
                                    } else {
                                        Color(0xFF8696A0)
                                    }
                                    Icon(
                                        imageVector = statusIcon,
                                        contentDescription = item.deliveryStatus,
                                        tint = statusTint,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = item.lastMessagePreview,
                                        style = TextStyle(
                                            fontFamily = PlusJakartaSansFont,
                                            fontWeight = if (item.unreadCount > 0) FontWeight.Medium else FontWeight.Normal,
                                            fontSize = 13.5.sp,
                                            color = if (item.unreadCount > 0) Color(0xFF111B21) else Color(0xFF667781)
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Right Indicators: Pinned, Muted, Unread Count Pill, Quick Action Menu
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            if (item.isPinned) {
                                Icon(
                                    imageVector = Icons.Default.PushPin,
                                    contentDescription = "Pinned",
                                    tint = Color(0xFF8696A0),
                                    modifier = Modifier
                                        .size(15.dp)
                                        .testTag("conversation_pinned_indicator_${item.conversationId}")
                                )
                            }
                            if (item.isMuted) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.VolumeOff,
                                    contentDescription = "Muted",
                                    tint = Color(0xFF8696A0),
                                    modifier = Modifier
                                        .size(16.dp)
                                        .testTag("conversation_muted_indicator_${item.conversationId}")
                                )
                            }
                            if (item.unreadCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF25D366))
                                        .testTag("conversation_unread_badge_${item.conversationId}"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = item.unreadCount.toString(),
                                        style = TextStyle(
                                            fontFamily = PlusJakartaSansFont,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.5.sp,
                                            color = Color.White
                                        )
                                    )
                                }
                            }

                            IconButton(
                                onClick = { onOpenContextMenu(item) },
                                modifier = Modifier
                                    .size(26.dp)
                                    .testTag("conversation_menu_${item.conversationId}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "Conversation Actions",
                                    tint = Color(0xFF8696A0),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // WhatsApp Indented Hairline Divider
            HorizontalDivider(
                modifier = Modifier.padding(start = 80.dp),
                thickness = 0.5.dp,
                color = Color(0xFFE9EDEF)
            )
        }
    }
}

@Composable
fun CircularConversationUserAvatar(
    item: ConversationItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val customBitmap = remember(item.avatarUri) {
        item.avatarUri?.let { uriStr ->
            runCatching {
                context.contentResolver.openInputStream(Uri.parse(uriStr))?.use { stream ->
                    BitmapFactory.decodeStream(stream)?.asImageBitmap()
                }
            }.getOrNull()
        }
    }

    val avatarGradients = listOf(
        listOf(ArenaPrimaryEmerald, ArenaEmeraldDark),
        listOf(Color(0xFF0B7285), Color(0xFF074652)),
        listOf(Color(0xFF2B8A3E), Color(0xFF174F22)),
        listOf(ArenaGoldDark, ArenaEmeraldDark),
        listOf(Color(0xFF495057), Color(0xFF212529)),
        listOf(Color(0xFF364FC7), Color(0xFF1E2E78))
    )
    val brush = Brush.linearGradient(
        avatarGradients.getOrElse(item.avatarPreset % avatarGradients.size) {
            listOf(ArenaPrimaryEmerald, ArenaEmeraldDark)
        }
    )

    val ringBrush = if (item.isVerified || item.unreadCount > 0) {
        Brush.linearGradient(listOf(ArenaGoldPrimary, ArenaPrimaryEmerald))
    } else {
        Brush.linearGradient(listOf(ArenaMintBorder, ArenaMintBorder))
    }

    val categoryBadgeIcon: ImageVector? = when (item.category) {
        "group" -> Icons.Default.Groups
        "channel" -> Icons.Default.Campaign
        "community" -> Icons.Default.Hub
        else -> null
    }

    val initials = item.displayName
        .split(" ")
        .mapNotNull { it.firstOrNull()?.uppercase() }
        .take(2)
        .joinToString("")
        .ifBlank { "AC" }

    val isArenaAiItem = item.displayName.contains("ARENA AI", ignoreCase = true) ||
        item.handleOrArenaId.contains("@arena_ai", ignoreCase = true)

    Box(
        modifier = modifier
            .size(54.dp)
            .clickable(onClick = onClick)
            .testTag("conversation_avatar_${item.conversationId}"),
        contentAlignment = Alignment.Center
    ) {
        if (isArenaAiItem && customBitmap == null) {
            ArenaAiRingLogo(
                size = 50.dp,
                innerBackground = Color(0xFF06291E)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .border(
                        width = if (item.isVerified || item.unreadCount > 0) 1.8.dp else 1.dp,
                        brush = ringBrush,
                        shape = CircleShape
                    )
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(brush),
                contentAlignment = Alignment.Center
            ) {
                if (customBitmap != null) {
                    Image(
                        bitmap = customBitmap,
                        contentDescription = "${item.displayName} profile avatar",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Subtle portrait silhouette watermark + crisp initials
                    Icon(
                        imageVector = categoryBadgeIcon ?: Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.18f),
                        modifier = Modifier.size(34.dp)
                    )
                    Text(
                        text = initials,
                        style = TextStyle(
                            fontFamily = PlusJakartaSansFont,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.5.sp,
                            color = Color.White
                        )
                    )
                }
            }
        }

        if (item.isOnline && item.category == "personal") {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(13.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF25D366))
                    .border(2.dp, Color.White, CircleShape)
                    .testTag("conversation_online_dot_${item.conversationId}")
            )
        } else if (categoryBadgeIcon != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(ArenaEmeraldDark)
                    .border(1.5.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = categoryBadgeIcon,
                    contentDescription = item.category,
                    tint = Color.White,
                    modifier = Modifier.size(10.dp)
                )
            }
        }
    }
}

@Composable
fun ArenaChatListSection(
    conversations: List<ConversationItem>,
    selectedFilter: ChatFilterType,
    searchQuery: String,
    archivedCount: Int = 0,
    onSelectArchivedFilter: () -> Unit = {},
    onOpenConversation: (ConversationItem) -> Unit,
    onOpenProfile: (ConversationItem) -> Unit,
    onOpenContextMenu: (ConversationItem) -> Unit,
    onResetFilters: () -> Unit,
    onStartNewChat: () -> Unit
) {
    Surface(
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("chat_list_component")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // WhatsApp-style "Archived" Row at top when viewing All chats
            if (selectedFilter == ChatFilterType.ALL && searchQuery.isEmpty() && archivedCount > 0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onSelectArchivedFilter)
                        .padding(horizontal = 22.dp, vertical = 11.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Archive,
                            contentDescription = "Archived",
                            tint = Color(0xFF00A884),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(22.dp))
                        Text(
                            text = "Archived",
                            style = TextStyle(
                                fontFamily = PlusJakartaSansFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.5.sp,
                                color = Color(0xFF111B21)
                            )
                        )
                    }
                    Text(
                        text = archivedCount.toString(),
                        style = TextStyle(
                            fontFamily = PlusJakartaSansFont,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.5.sp,
                            color = Color(0xFF00A884)
                        )
                    )
                }
                HorizontalDivider(
                    modifier = Modifier.padding(start = 64.dp),
                    thickness = 0.5.dp,
                    color = Color(0xFFE9EDEF)
                )
            }

            if (conversations.isEmpty()) {
                ArenaEmptyChatListState(
                    selectedFilter = selectedFilter,
                    searchQuery = searchQuery,
                    onResetFilters = onResetFilters,
                    onStartNewChat = onStartNewChat
                )
            } else {
                conversations.forEach { item ->
                    ArenaConversationItemCard(
                        item = item,
                        onOpenConversation = onOpenConversation,
                        onOpenProfile = onOpenProfile,
                        onOpenContextMenu = onOpenContextMenu
                    )
                }
            }
        }
    }
}

@Composable
fun ArenaEmptyChatListState(
    selectedFilter: ChatFilterType,
    searchQuery: String,
    onResetFilters: () -> Unit,
    onStartNewChat: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(28.dp)
            .testTag("empty_conversations_state"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(ArenaMintSoft),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Chat,
                contentDescription = null,
                tint = ArenaPrimaryEmerald,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = if (searchQuery.isNotBlank()) {
                "No matches for \"$searchQuery\""
            } else {
                "No chats in ${selectedFilter.label}"
            },
            style = TextStyle(
                fontFamily = PlusJakartaSansFont,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                color = ArenaCharcoalText
            ),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Tap the green + button below to start a new WhatsApp-speed encrypted chat.",
            style = TextStyle(
                fontFamily = PlusJakartaSansFont,
                fontSize = 13.sp,
                color = ArenaTextSecondary
            ),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            if (selectedFilter != ChatFilterType.ALL || searchQuery.isNotBlank()) {
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .clickable(onClick = onResetFilters),
                    color = ArenaMintSoft,
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text(
                        text = "Show All Chats",
                        style = TextStyle(
                            fontFamily = PlusJakartaSansFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp,
                            color = ArenaPrimaryEmerald
                        ),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .clickable(onClick = onStartNewChat),
                color = ArenaPrimaryEmerald,
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(
                    text = "Start New Chat",
                    style = TextStyle(
                        fontFamily = PlusJakartaSansFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp,
                        color = Color.White
                    ),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun ArenaDashboardFabAndMenu(
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onSelectAction: (FabNewActionType) -> Unit,
    onOpenArenaAi: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val actionIcons = mapOf(
        FabNewActionType.NEW_MESSAGE to Icons.Default.Edit,
        FabNewActionType.NEW_GROUP to Icons.Default.GroupAdd,
        FabNewActionType.NEW_CHANNEL to Icons.Default.Campaign,
        FabNewActionType.NEW_COMMUNITY to Icons.Default.Hub,
        FabNewActionType.NEW_CONTACT to Icons.Default.PersonAdd
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + scaleIn(initialScale = 0.85f),
            exit = fadeOut() + scaleOut(targetScale = 0.85f)
        ) {
            Surface(
                modifier = Modifier
                    .width(240.dp)
                    .shadow(8.dp, RoundedCornerShape(18.dp))
                    .border(1.dp, ArenaMintBorder, RoundedCornerShape(18.dp))
                    .testTag("fab_new_conversation_menu"),
                color = Color.White,
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    FabNewActionType.entries.forEach { action ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectAction(action) }
                                .padding(horizontal = 14.dp, vertical = 9.dp)
                                .testTag("fab_menu_${action.name.lowercase()}"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFD9FDD3)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = actionIcons[action] ?: Icons.Default.Add,
                                    contentDescription = action.title,
                                    tint = ArenaPrimaryEmerald,
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = action.title,
                                    style = TextStyle(
                                        fontFamily = PlusJakartaSansFont,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.5.sp,
                                        color = ArenaCharcoalText
                                    )
                                )
                                Text(
                                    text = action.subtitle,
                                    style = TextStyle(
                                        fontFamily = PlusJakartaSansFont,
                                        fontSize = 10.5.sp,
                                        color = ArenaTextSecondary
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        // WhatsApp-style Floating ARENA AI Icon Directly Above the + FAB Button
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(end = 4.dp)
        ) {
            Surface(
                modifier = Modifier
                    .size(48.dp)
                    .shadow(6.dp, CircleShape)
                    .clip(CircleShape)
                    .clickable(onClick = onOpenArenaAi)
                    .testTag("arena_ai_fab_button"),
                color = Color.White,
                shape = CircleShape
            ) {
                Box(contentAlignment = Alignment.Center) {
                    ArenaAiRingLogo(size = 42.dp)
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Surface(
                color = Color(0xFF0A2E23),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .clickable(onClick = onOpenArenaAi)
                    .border(0.8.dp, ArenaGoldPrimary, RoundedCornerShape(8.dp))
            ) {
                Text(
                    text = "ARENA AI",
                    style = TextStyle(
                        fontFamily = PlusJakartaSansFont,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 8.5.sp,
                        letterSpacing = 0.4.sp,
                        color = ArenaGoldLight
                    ),
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.5.dp)
                )
            }
        }

        // WhatsApp Android Style Rounded-Square / Emerald FloatingActionButton (+)
        FloatingActionButton(
            onClick = onToggleExpand,
            shape = RoundedCornerShape(16.dp),
            containerColor = ArenaPrimaryEmerald,
            contentColor = Color.White,
            modifier = Modifier
                .size(56.dp)
                .shadow(6.dp, RoundedCornerShape(16.dp))
                .testTag("dashboard_fab_button")
        ) {
            Icon(
                imageVector = if (isExpanded) Icons.Default.Close else Icons.Default.Add,
                contentDescription = if (isExpanded) "Close New Menu" else "New Conversation",
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}

@Composable
fun ArenaSixSectionBottomNavigation(
    selectedTab: ArenaDashboardTab,
    unreadChatsCount: Int,
    missedCallsCount: Int,
    onSelectTab: (ArenaDashboardTab) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp)
            .testTag("six_section_bottom_nav"),
        color = Color.White
    ) {
        Column {
            HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFE9EDEF))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = 4.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ArenaDashboardTab.entries.forEach { tab ->
                    val isSelected = tab == selectedTab
                    val (activeIcon, inactiveIcon) = getTabIcons(tab)
                    val badge = when (tab) {
                        ArenaDashboardTab.CHATS -> unreadChatsCount
                        ArenaDashboardTab.CALLS -> missedCallsCount
                        else -> 0
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onSelectTab(tab) }
                            .padding(vertical = 3.dp)
                            .testTag("bottom_nav_tab_${tab.name.lowercase()}")
                    ) {
                        Box(contentAlignment = Alignment.TopEnd) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        if (isSelected) Color(0xFFD9FDD3) else Color.Transparent
                                    )
                                    .padding(horizontal = 14.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isSelected) activeIcon else inactiveIcon,
                                    contentDescription = tab.label,
                                    tint = if (isSelected) ArenaPrimaryEmerald else Color(0xFF54656F),
                                    modifier = Modifier.size(21.dp)
                                )
                            }

                            if (badge > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF25D366))
                                        .border(1.5.dp, Color.White, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = badge.coerceAtMost(99).toString(),
                                        style = TextStyle(
                                            fontFamily = PlusJakartaSansFont,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 8.5.sp,
                                            color = Color.White
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = tab.label,
                            style = TextStyle(
                                fontFamily = PlusJakartaSansFont,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 10.5.sp,
                                color = if (isSelected) Color(0xFF111B21) else Color(0xFF54656F)
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ArenaSixSectionNavigationRail(
    selectedTab: ArenaDashboardTab,
    unreadChatsCount: Int,
    missedCallsCount: Int,
    onSelectTab: (ArenaDashboardTab) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .width(88.dp)
            .border(0.5.dp, Color(0xFFE9EDEF))
            .testTag("six_section_navigation_rail"),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ArenaDashboardTab.entries.forEach { tab ->
                val isSelected = tab == selectedTab
                val (activeIcon, inactiveIcon) = getTabIcons(tab)
                val badge = when (tab) {
                    ArenaDashboardTab.CHATS -> unreadChatsCount
                    ArenaDashboardTab.CALLS -> missedCallsCount
                    else -> 0
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) Color(0xFFD9FDD3) else Color.Transparent)
                        .clickable { onSelectTab(tab) }
                        .padding(vertical = 8.dp)
                        .testTag("rail_nav_tab_${tab.name.lowercase()}")
                ) {
                    Box(contentAlignment = Alignment.TopEnd) {
                        Icon(
                            imageVector = if (isSelected) activeIcon else inactiveIcon,
                            contentDescription = tab.label,
                            tint = if (isSelected) ArenaPrimaryEmerald else ArenaTextSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                        if (badge > 0) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF25D366)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = badge.coerceAtMost(9).toString(),
                                    style = TextStyle(
                                        fontFamily = PlusJakartaSansFont,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 8.sp,
                                        color = Color.White
                                    )
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = tab.label,
                        style = TextStyle(
                            fontFamily = PlusJakartaSansFont,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 10.sp,
                            color = if (isSelected) ArenaPrimaryEmerald else ArenaTextSecondary
                        ),
                        maxLines = 1
                    )
                }
            }
        }
    }
}

private fun getTabIcons(tab: ArenaDashboardTab): Pair<ImageVector, ImageVector> {
    return when (tab) {
        ArenaDashboardTab.CHATS -> Icons.AutoMirrored.Filled.Chat to Icons.AutoMirrored.Outlined.Chat
        ArenaDashboardTab.CALLS -> Icons.Default.Phone to Icons.Outlined.Phone
        ArenaDashboardTab.COMMUNITIES -> Icons.Default.Groups to Icons.Outlined.Groups
        ArenaDashboardTab.CHANNELS -> Icons.Default.Campaign to Icons.Outlined.Campaign
        ArenaDashboardTab.DISCOVER -> Icons.Default.Explore to Icons.Outlined.Explore
        ArenaDashboardTab.PROFILE -> Icons.Default.Person to Icons.Outlined.Person
    }
}
