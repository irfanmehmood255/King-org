package com.example.ui.screens.dashboard

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ArenaAiMode
import com.example.data.model.GlobalSearchCategory
import com.example.data.model.StoryItem
import com.example.ui.theme.ArenaEmeraldDark
import com.example.ui.theme.ArenaEmeraldDeep
import com.example.ui.theme.ArenaError
import com.example.ui.theme.ArenaGoldDark
import com.example.ui.theme.ArenaGoldLight
import com.example.ui.theme.ArenaGoldPrimary
import com.example.ui.theme.ArenaMintBorder
import com.example.ui.theme.ArenaMintSoft
import com.example.ui.theme.ArenaTextMuted
import com.example.ui.theme.ArenaTextSecondary
import com.example.ui.theme.MontserratFont
import com.example.ui.theme.PlusJakartaSansFont

val ArenaPrimaryEmerald = Color(0xFF087F5B)
val ArenaSoftCream = Color(0xFFFFF9EF)
val ArenaCharcoalText = Color(0xFF202520)

@Composable
fun ArenaDashboardTopHeader(
    displayName: String,
    profileAvatarUri: String?,
    selectedAvatarPreset: Int,
    isOnline: Boolean,
    unreadNotificationCount: Int,
    isSearchExpanded: Boolean,
    onToggleOnlineStatus: () -> Unit,
    onToggleSearch: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenProfileTab: () -> Unit,
    onQuickCameraStatus: () -> Unit = {}
) {
    val context = LocalContext.current
    var showOverflowMenu by remember { mutableStateOf(false) }

    val customBitmap = remember(profileAvatarUri) {
        profileAvatarUri?.let { uriStr ->
            runCatching {
                context.contentResolver.openInputStream(Uri.parse(uriStr))?.use { stream ->
                    BitmapFactory.decodeStream(stream)?.asImageBitmap()
                }
            }.getOrNull()
        }
    }
    val presetIcons = listOf(
        Icons.Default.Face,
        Icons.Default.WorkspacePremium,
        Icons.Default.AutoAwesome,
        Icons.Default.Public
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("dashboard_top_header"),
        color = Color.White
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 14.dp, end = 6.dp, top = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // WhatsApp-style Left Title + Logo
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.arena_logo_header),
                        contentDescription = "ARENA CHAT Logo",
                        modifier = Modifier.size(34.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Arena Chat",
                        style = TextStyle(
                            fontFamily = PlusJakartaSansFont,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 21.sp,
                            color = ArenaPrimaryEmerald
                        )
                    )
                }

                // WhatsApp-style Right Action Icons: Camera, Search, Notifications, Settings, Profile Avatar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    IconButton(
                        onClick = onQuickCameraStatus,
                        modifier = Modifier
                            .size(42.dp)
                            .testTag("header_camera_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CameraAlt,
                            contentDescription = "Camera / Status",
                            tint = ArenaCharcoalText,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    IconButton(
                        onClick = onToggleSearch,
                        modifier = Modifier
                            .size(42.dp)
                            .testTag("header_search_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (isSearchExpanded) Icons.Default.Close else Icons.Outlined.Search,
                            contentDescription = "Global Search",
                            tint = if (isSearchExpanded) ArenaPrimaryEmerald else ArenaCharcoalText,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Box(contentAlignment = Alignment.TopEnd) {
                        IconButton(
                            onClick = onOpenNotifications,
                            modifier = Modifier
                                .size(42.dp)
                                .testTag("notification_bell_button")
                        ) {
                            Icon(
                                imageVector = if (unreadNotificationCount > 0) {
                                    Icons.Default.Notifications
                                } else {
                                    Icons.Outlined.Notifications
                                },
                                contentDescription = "Notification Center",
                                tint = if (unreadNotificationCount > 0) ArenaPrimaryEmerald else ArenaCharcoalText,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        if (unreadNotificationCount > 0) {
                            Box(
                                modifier = Modifier
                                    .offset(x = (-5).dp, y = 5.dp)
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(ArenaPrimaryEmerald)
                                    .border(1.5.dp, Color.White, CircleShape)
                                    .testTag("notification_unread_badge"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = unreadNotificationCount.coerceAtMost(99).toString(),
                                    style = TextStyle(
                                        fontFamily = PlusJakartaSansFont,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp,
                                        color = Color.White
                                    )
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier
                            .size(42.dp)
                            .testTag("header_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Settings Shortcut",
                            tint = ArenaCharcoalText,
                            modifier = Modifier.size(21.dp)
                        )
                    }

                    // User Profile Avatar + Online Status Dot
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onOpenProfileTab)
                            .padding(4.dp)
                            .testTag("header_profile_avatar"),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(ArenaPrimaryEmerald, ArenaEmeraldDark)
                                    )
                                )
                                .border(1.2.dp, ArenaGoldPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (customBitmap != null) {
                                Image(
                                    bitmap = customBitmap,
                                    contentDescription = "User Profile Avatar",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = presetIcons.getOrElse(selectedAvatarPreset) { Icons.Default.Face },
                                    contentDescription = displayName,
                                    tint = ArenaGoldLight,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(11.dp)
                                .clip(CircleShape)
                                .background(if (isOnline) Color(0xFF12B886) else Color(0xFF9AA5A0))
                                .border(1.5.dp, Color.White, CircleShape)
                                .clickable(onClick = onToggleOnlineStatus)
                                .testTag("header_online_status_dot")
                        )
                    }

                    // WhatsApp 3-Dot Overflow Menu
                    Box {
                        IconButton(
                            onClick = { showOverflowMenu = true },
                            modifier = Modifier.size(38.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More options",
                                tint = ArenaCharcoalText,
                                modifier = Modifier.size(21.dp)
                            )
                        }
                        DropdownMenu(
                            expanded = showOverflowMenu,
                            onDismissRequest = { showOverflowMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("My Profile & Arena ID") },
                                onClick = {
                                    showOverflowMenu = false
                                    onOpenProfileTab()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Add Status Update") },
                                onClick = {
                                    showOverflowMenu = false
                                    onQuickCameraStatus()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Toggle Online / Away") },
                                onClick = {
                                    showOverflowMenu = false
                                    onToggleOnlineStatus()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Privacy & Settings") },
                                onClick = {
                                    showOverflowMenu = false
                                    onOpenSettings()
                                }
                            )
                        }
                    }
                }
            }
            HorizontalDivider(thickness = 0.5.dp, color = ArenaMintBorder.copy(alpha = 0.5f))
        }
    }
}

@Composable
fun ArenaWelcomeProfileBanner(
    displayName: String,
    username: String,
    arenaId: String,
    isOnline: Boolean,
    isHumanVerified: Boolean,
    profileAvatarUri: String?,
    selectedAvatarPreset: Int,
    onToggleOnlineStatus: () -> Unit,
    onOpenProfileClick: () -> Unit
) {
    // Sleek, compact 1-line WhatsApp identity strip so chats stay front and center
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenProfileClick)
            .testTag("welcome_profile_banner"),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = displayName,
                    style = TextStyle(
                        fontFamily = PlusJakartaSansFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = ArenaCharcoalText
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (isHumanVerified) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = "Human Verified Account",
                        tint = ArenaPrimaryEmerald,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "• @$username • $arenaId",
                    style = TextStyle(
                        fontFamily = PlusJakartaSansFont,
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.5.sp,
                        color = ArenaTextSecondary
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .clickable(onClick = onToggleOnlineStatus)
                    .testTag("presence_status_chip"),
                color = if (isOnline) ArenaMintSoft else Color(0xFFF1F3F5),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (isOnline) Color(0xFF12B886) else Color(0xFF8C9690))
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isOnline) "Online" else "Away",
                        style = TextStyle(
                            fontFamily = PlusJakartaSansFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.5.sp,
                            color = if (isOnline) ArenaEmeraldDark else ArenaTextSecondary
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun ArenaGlobalSearchSection(
    searchQuery: String,
    isSearchExpanded: Boolean,
    selectedCategory: GlobalSearchCategory,
    isSearching: Boolean,
    resultCount: Int = 0,
    recentSearches: List<String>,
    suggestions: List<String>,
    onSearchQueryChange: (String) -> Unit,
    onSearchFocusExpand: () -> Unit,
    onSelectCategory: (GlobalSearchCategory) -> Unit,
    onClearRecentSearches: () -> Unit,
    onOpenArenaAiSearch: (String, ArenaAiMode) -> Unit = { _, _ -> }
) {
    Surface(
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("global_search_section")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            // WhatsApp Android Pill Search Bar with Integrated ARENA AI Mode Trigger
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clickable(onClick = onSearchFocusExpand)
                    .testTag("global_search_bar"),
                color = Color(0xFFF0F2F5),
                shape = RoundedCornerShape(22.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 12.dp, end = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable {
                                val q = searchQuery.trim()
                                onOpenArenaAiSearch(
                                    q,
                                    ArenaAiService.detectAutomaticIntent(q)
                                )
                            }
                            .testTag("search_bar_arena_ai_logo"),
                        contentAlignment = Alignment.Center
                    ) {
                        ArenaAiRingLogo(size = 24.dp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "Ask ARENA AI or search chats, IDs...",
                                style = TextStyle(
                                    fontFamily = PlusJakartaSansFont,
                                    fontSize = 13.5.sp,
                                    color = ArenaTextSecondary
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = {
                                if (!isSearchExpanded) onSearchFocusExpand()
                                onSearchQueryChange(it)
                            },
                            singleLine = true,
                            textStyle = TextStyle(
                                fontFamily = PlusJakartaSansFont,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                color = ArenaCharcoalText
                            ),
                            cursorBrush = SolidColor(ArenaPrimaryEmerald),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("global_search_input")
                        )
                    }
                    if (isSearching) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(15.dp)
                                .testTag("global_search_spinner"),
                            color = ArenaPrimaryEmerald,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { onSearchQueryChange("") },
                            modifier = Modifier
                                .size(28.dp)
                                .testTag("clear_search_query_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear Search",
                                tint = ArenaTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                    }

                    // Unified Automatic ARENA AI Pill inside the Search Bar
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                val q = searchQuery.trim()
                                onOpenArenaAiSearch(
                                    q,
                                    ArenaAiService.detectAutomaticIntent(q)
                                )
                            }
                            .testTag("search_bar_arena_ai_mode_button"),
                        color = Color(0xFF0A2E23),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Arena AI",
                                tint = ArenaGoldPrimary,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "ARENA AI",
                                style = TextStyle(
                                    fontFamily = PlusJakartaSansFont,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 10.sp,
                                    color = ArenaGoldLight
                                )
                            )
                        }
                    }
                }
            }

            // Categorized Search Filters Row + Automatic ARENA AI Card (Visible when search is expanded or active)
            AnimatedVisibility(visible = isSearchExpanded || searchQuery.isNotEmpty()) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    // Unified Automatic ARENA AI Banner (No manual mode buttons!)
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                val q = searchQuery.trim()
                                onOpenArenaAiSearch(
                                    q,
                                    ArenaAiService.detectAutomaticIntent(q)
                                )
                            }
                            .border(1.dp, ArenaGoldPrimary.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                            .testTag("search_ask_arena_ai_card"),
                        color = Color(0xFF071F18),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ArenaAiRingLogo(size = 28.dp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (searchQuery.isNotBlank()) {
                                        "ARENA AI Auto-Answer: \"$searchQuery\""
                                    } else {
                                        "ARENA AI • Automatic Deep Chat, Live Web & 8K Images"
                                    },
                                    style = TextStyle(
                                        fontFamily = PlusJakartaSansFont,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 12.sp,
                                        color = Color.White
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Automatically answers questions, searches live web data, or generates 8K images",
                                    style = TextStyle(
                                        fontFamily = PlusJakartaSansFont,
                                        fontSize = 10.5.sp,
                                        color = ArenaGoldLight
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("global_search_categories_row"),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(GlobalSearchCategory.entries) { category ->
                            val selected = category == selectedCategory
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable { onSelectCategory(category) }
                                    .testTag("search_category_${category.name.lowercase()}"),
                                color = if (selected) ArenaPrimaryEmerald else Color(0xFFF0F2F5),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text(
                                    text = category.label,
                                    style = TextStyle(
                                        fontFamily = PlusJakartaSansFont,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 11.5.sp,
                                        color = if (selected) Color.White else ArenaCharcoalText
                                    ),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }

                    if (isSearching) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 4.dp)
                                .testTag("search_loading_state"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                color = ArenaPrimaryEmerald,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Searching ${selectedCategory.label}...",
                                style = TextStyle(
                                    fontFamily = PlusJakartaSansFont,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.5.sp,
                                    color = ArenaEmeraldDark
                                )
                            )
                        }
                    } else if (searchQuery.isNotBlank() && resultCount == 0) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 4.dp)
                                .testTag("search_empty_results_state"),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "No results in ${selectedCategory.label} for \"$searchQuery\"",
                                style = TextStyle(
                                    fontFamily = PlusJakartaSansFont,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp,
                                    color = ArenaTextSecondary
                                )
                            )
                            Text(
                                text = "Reset",
                                style = TextStyle(
                                    fontFamily = PlusJakartaSansFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = ArenaPrimaryEmerald
                                ),
                                modifier = Modifier.clickable { onSearchQueryChange("") }
                            )
                        }
                    } else if (searchQuery.isNotBlank() && resultCount > 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$resultCount result(s) in ${selectedCategory.label}",
                            style = TextStyle(
                                fontFamily = PlusJakartaSansFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp,
                                color = ArenaPrimaryEmerald
                            ),
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .testTag("search_results_count_badge")
                        )
                    }

                    if (searchQuery.isEmpty() && recentSearches.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = null,
                                    tint = ArenaTextSecondary,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Recent",
                                    style = TextStyle(
                                        fontFamily = PlusJakartaSansFont,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = ArenaTextSecondary
                                    )
                                )
                            }
                            Text(
                                text = "Clear",
                                style = TextStyle(
                                    fontFamily = PlusJakartaSansFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = ArenaPrimaryEmerald
                                ),
                                modifier = Modifier
                                    .clickable(onClick = onClearRecentSearches)
                                    .testTag("clear_recent_searches_button")
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(recentSearches) { item ->
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { onSearchQueryChange(item) },
                                    color = ArenaMintSoft,
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = item,
                                        style = TextStyle(
                                            fontFamily = PlusJakartaSansFont,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 11.sp,
                                            color = ArenaEmeraldDark
                                        ),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ArenaStoriesSection(
    stories: List<StoryItem>,
    onAddStoryClick: () -> Unit,
    onViewStoryClick: (StoryItem) -> Unit,
    onOpenStoryPrivacyClick: () -> Unit
) {
    val accentPalettes = listOf(
        listOf(ArenaPrimaryEmerald, ArenaEmeraldDark),
        listOf(Color(0xFF0B7285), Color(0xFF084C59)),
        listOf(Color(0xFF2B8A3E), Color(0xFF1B5E28)),
        listOf(Color(0xFF5F3DC4), Color(0xFF3B2382)),
        listOf(ArenaGoldDark, ArenaEmeraldDark)
    )

    Surface(
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("stories_status_section")
    ) {
        Column(modifier = Modifier.padding(top = 4.dp, bottom = 6.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Status Updates",
                    style = TextStyle(
                        fontFamily = PlusJakartaSansFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = ArenaTextSecondary
                    )
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(onClick = onOpenStoryPrivacyClick)
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                            .testTag("story_privacy_button"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PrivacyTip,
                            contentDescription = "Story Privacy",
                            tint = ArenaPrimaryEmerald,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Privacy",
                            style = TextStyle(
                                fontFamily = PlusJakartaSansFont,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp,
                                color = ArenaPrimaryEmerald
                            )
                        )
                    }

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(ArenaMintSoft)
                            .clickable(onClick = onAddStoryClick)
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                            .testTag("add_status_button"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Status",
                            tint = ArenaEmeraldDark,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "Add Status",
                            style = TextStyle(
                                fontFamily = PlusJakartaSansFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = ArenaEmeraldDark
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(stories, key = { it.storyId }) { story ->
                    val colors = accentPalettes.getOrElse(story.accentPreset % accentPalettes.size) {
                        listOf(ArenaPrimaryEmerald, ArenaEmeraldDark)
                    }
                    val ringBrush = if (story.hasUnseen) {
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF25D366), ArenaPrimaryEmerald, ArenaGoldPrimary)
                        )
                    } else {
                        Brush.linearGradient(
                            colors = listOf(ArenaMintBorder, ArenaMintBorder)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(64.dp)
                            .clickable { onViewStoryClick(story) }
                            .testTag(if (story.isOwnStory) "my_status_story_item" else "story_item_${story.storyId}")
                    ) {
                        Box(
                            modifier = Modifier.size(54.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .border(
                                        width = if (story.hasUnseen) 2.2.dp else 1.2.dp,
                                        brush = ringBrush,
                                        shape = CircleShape
                                    )
                                    .padding(3.5.dp)
                                    .clip(CircleShape)
                                    .background(Brush.linearGradient(colors)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = story.authorName.take(2).uppercase(),
                                    style = TextStyle(
                                        fontFamily = PlusJakartaSansFont,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 14.sp,
                                        color = Color.White
                                    )
                                )
                            }

                            if (story.isOwnStory) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(ArenaPrimaryEmerald)
                                        .border(1.5.dp, Color.White, CircleShape)
                                        .clickable(onClick = onAddStoryClick),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add to My Status",
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = if (story.isOwnStory) "My status" else story.authorName.substringBefore(" "),
                            style = TextStyle(
                                fontFamily = PlusJakartaSansFont,
                                fontWeight = if (story.hasUnseen) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 11.sp,
                                color = ArenaCharcoalText
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
