package com.example.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.automirrored.filled.CallMissed
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CallHistoryEntry
import com.example.data.model.ConversationItem
import com.example.data.model.ProfileSettingsSection
import com.example.ui.theme.ArenaEmeraldDark
import com.example.ui.theme.ArenaError
import com.example.ui.theme.ArenaGoldLight
import com.example.ui.theme.ArenaGoldPrimary
import com.example.ui.theme.ArenaMintBorder
import com.example.ui.theme.ArenaMintSoft
import com.example.ui.theme.ArenaTextSecondary
import com.example.ui.theme.MontserratFont
import com.example.ui.theme.PlusJakartaSansFont

@Composable
fun ArenaCallsTabContent(
    calls: List<CallHistoryEntry>,
    onCallActionClick: (CallHistoryEntry, Boolean?) -> Unit,
    onClearCallHistory: () -> Unit = {},
    onCopyCallLink: (String) -> Unit = {}
) {
    var selectedCallFilter by remember { mutableStateOf("All") }
    var generatedCallLink by remember { mutableStateOf<String?>(null) }
    val filters = listOf("All", "Missed", "Video", "Voice")

    val filteredCalls = calls.filter { entry ->
        when (selectedCallFilter) {
            "Missed" -> entry.isMissed
            "Video" -> entry.isVideoCall
            "Voice" -> !entry.isVideoCall
            else -> true
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .testTag("calls_tab_content"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // WhatsApp "Create call link" row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val code = "e2ee-${(1000..9999).random()}-hd"
                        val link = "https://arena.chat/call/$code"
                        generatedCallLink = link
                        onCopyCallLink("Call link ready: $link")
                    }
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .testTag("create_call_link_row"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(ArenaPrimaryEmerald),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Link,
                        contentDescription = "Create call link",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Create call link",
                        style = TextStyle(
                            fontFamily = PlusJakartaSansFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.5.sp,
                            color = ArenaCharcoalText
                        )
                    )
                    Text(
                        text = generatedCallLink ?: "Share a link for your Arena E2EE voice or video call",
                        style = TextStyle(
                            fontFamily = PlusJakartaSansFont,
                            fontSize = 12.5.sp,
                            color = if (generatedCallLink != null) ArenaPrimaryEmerald else ArenaTextSecondary
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (generatedCallLink != null) {
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                val instantEntry = CallHistoryEntry(
                                    callId = "call_link_room",
                                    contactName = "Arena Call Link Lounge",
                                    handleOrArenaId = generatedCallLink ?: "arena.chat/call",
                                    isVideoCall = true,
                                    isIncoming = false,
                                    isMissed = false,
                                    timeLabel = "Live now",
                                    durationLabel = "Instant Link Room",
                                    avatarPreset = 0
                                )
                                onCallActionClick(instantEntry, true)
                            },
                        color = ArenaMintSoft,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "Join",
                            style = TextStyle(
                                fontFamily = PlusJakartaSansFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = ArenaPrimaryEmerald
                            ),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            HorizontalDivider(color = Color(0xFFF0F2F5), thickness = 1.dp)
        }

        // Section Header + Filters + Clear Action
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent",
                    style = TextStyle(
                        fontFamily = PlusJakartaSansFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.5.sp,
                        color = ArenaTextSecondary
                    )
                )
                if (calls.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable(onClick = onClearCallHistory)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("clear_call_history_button"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Clear Call Log",
                            tint = ArenaTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Clear log",
                            style = TextStyle(
                                fontFamily = PlusJakartaSansFont,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = ArenaTextSecondary
                            )
                        )
                    }
                }
            }

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filters) { filter ->
                    val selected = filter == selectedCallFilter
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .clickable { selectedCallFilter = filter },
                        color = if (selected) Color(0xFFD8FDD2) else Color(0xFFF0F2F5),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Text(
                            text = filter,
                            style = TextStyle(
                                fontFamily = PlusJakartaSansFont,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.5.sp,
                                color = if (selected) ArenaPrimaryEmerald else Color(0xFF54656F)
                            ),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        if (filteredCalls.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null,
                        tint = ArenaTextSecondary.copy(alpha = 0.5f),
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "No $selectedCallFilter calls in your log",
                        style = TextStyle(
                            fontFamily = PlusJakartaSansFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = ArenaCharcoalText
                        )
                    )
                    Text(
                        text = "Tap 'Create call link' above or open any chat to start an HD E2EE call.",
                        style = TextStyle(
                            fontFamily = PlusJakartaSansFont,
                            fontSize = 12.5.sp,
                            color = ArenaTextSecondary
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            items(filteredCalls, key = { it.callId }) { call ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCallActionClick(call, null) }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(ArenaPrimaryEmerald, ArenaEmeraldDark)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = call.contactName.take(2).uppercase(),
                                    style = TextStyle(
                                        fontFamily = PlusJakartaSansFont,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 15.sp,
                                        color = Color.White
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = call.contactName,
                                    style = TextStyle(
                                        fontFamily = PlusJakartaSansFont,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 16.sp,
                                        color = if (call.isMissed) ArenaError else ArenaCharcoalText
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = when {
                                            call.isMissed -> Icons.AutoMirrored.Filled.CallMissed
                                            call.isIncoming -> Icons.AutoMirrored.Filled.CallReceived
                                            else -> Icons.AutoMirrored.Filled.CallMade
                                        },
                                        contentDescription = null,
                                        tint = if (call.isMissed) ArenaError else Color(0xFF25D366),
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = "${call.timeLabel} • ${call.durationLabel}",
                                        style = TextStyle(
                                            fontFamily = PlusJakartaSansFont,
                                            fontSize = 12.5.sp,
                                            color = ArenaTextSecondary
                                        )
                                    )
                                }
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            IconButton(
                                onClick = { onCallActionClick(call, false) },
                                modifier = Modifier.size(42.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = "Voice Call ${call.contactName}",
                                    tint = ArenaPrimaryEmerald,
                                    modifier = Modifier.size(21.dp)
                                )
                            }
                            IconButton(
                                onClick = { onCallActionClick(call, true) },
                                modifier = Modifier.size(42.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Videocam,
                                    contentDescription = "Video Call ${call.contactName}",
                                    tint = ArenaPrimaryEmerald,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(start = 78.dp),
                        color = Color(0xFFF0F2F5),
                        thickness = 0.8.dp
                    )
                }
            }
        }
    }
}

@Composable
fun ArenaCommunitiesTabContent(
    conversations: List<ConversationItem>,
    onOpenCommunity: (ConversationItem) -> Unit,
    onOpenCommunityRoom: (ConversationItem, String) -> Unit = { conv, _ -> onOpenCommunity(conv) },
    onCreateCommunityClick: () -> Unit
) {
    val communities = conversations.filter { it.category == "community" || it.category == "group" }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .testTag("communities_tab_content"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // WhatsApp "New community" action row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onCreateCommunityClick)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .testTag("create_community_header_button"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(ArenaPrimaryEmerald),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Groups,
                        contentDescription = "New Community",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "New community",
                        style = TextStyle(
                            fontFamily = PlusJakartaSansFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = ArenaCharcoalText
                        )
                    )
                    Text(
                        text = "Bring members together in topic groups & live voice stages",
                        style = TextStyle(
                            fontFamily = PlusJakartaSansFont,
                            fontSize = 12.5.sp,
                            color = ArenaTextSecondary
                        )
                    )
                }
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = ArenaPrimaryEmerald,
                    modifier = Modifier.size(22.dp)
                )
            }

            HorizontalDivider(color = Color(0xFFF0F2F5), thickness = 6.dp)
        }

        items(communities, key = { it.conversationId }) { community ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                // Community Header Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenCommunity(community) }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(ArenaPrimaryEmerald, ArenaEmeraldDark)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Hub,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = community.displayName,
                                style = TextStyle(
                                    fontFamily = PlusJakartaSansFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = ArenaCharcoalText
                                )
                            )
                            if (community.isVerified) {
                                Spacer(modifier = Modifier.width(5.dp))
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Verified Community",
                                    tint = ArenaPrimaryEmerald,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Text(
                            text = "${community.memberCount} members • ${community.handleOrArenaId}",
                            style = TextStyle(
                                fontFamily = PlusJakartaSansFont,
                                fontSize = 12.sp,
                                color = ArenaTextSecondary
                            )
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                        tint = ArenaTextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                }

                HorizontalDivider(color = Color(0xFFF0F2F5), thickness = 0.8.dp)

                // Announcements Row (WhatsApp Community style)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenCommunityRoom(community, "# announcements") }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background( Color(0xFFD8FDD2)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = "Announcements",
                            tint = ArenaPrimaryEmerald,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Announcements",
                                style = TextStyle(
                                    fontFamily = PlusJakartaSansFont,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.5.sp,
                                    color = ArenaCharcoalText
                                )
                            )
                            Text(
                                text = community.lastMessageTimeLabel,
                                style = TextStyle(
                                    fontFamily = PlusJakartaSansFont,
                                    fontSize = 11.5.sp,
                                    color = ArenaTextSecondary
                                )
                            )
                        }
                        Text(
                            text = community.lastMessagePreview,
                            style = TextStyle(
                                fontFamily = PlusJakartaSansFont,
                                fontSize = 13.sp,
                                color = ArenaTextSecondary
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Interactive Sub-Rooms Pills (# general-chat, 🔊 voice-stage)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onOpenCommunityRoom(community, "# general-chat") },
                        color = Color(0xFFF0F2F5),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Chat,
                                contentDescription = null,
                                tint = ArenaPrimaryEmerald,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "# general-chat",
                                style = TextStyle(
                                    fontFamily = PlusJakartaSansFont,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp,
                                    color = ArenaCharcoalText
                                )
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onOpenCommunityRoom(community, "🔊 voice-stage") },
                        color = Color(0xFFD8FDD2),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = null,
                                tint = ArenaPrimaryEmerald,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Join Voice Stage",
                                style = TextStyle(
                                    fontFamily = PlusJakartaSansFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = ArenaPrimaryEmerald
                                )
                            )
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFFF0F2F5), thickness = 6.dp)
            }
        }
    }
}

@Composable
fun ArenaChannelsTabContent(
    conversations: List<ConversationItem>,
    onOpenChannel: (ConversationItem) -> Unit,
    onCreateChannelClick: () -> Unit,
    onOpenProfile: (ConversationItem) -> Unit = onOpenChannel,
    onOpenContextMenu: (ConversationItem) -> Unit = onOpenChannel,
    onFollowDirectoryChannel: (String, String) -> Unit = { _, _ -> }
) {
    val channels = conversations.filter { it.category == "channel" }
    val followedIds = remember { mutableStateListOf<String>() }

    val suggestedChannels = listOf(
        Triple("ch_tech", "Arena AI & Quantum Updates", "420K followers • Official Tech"),
        Triple("ch_design", "WhatsApp & Material 3 Craft", "185K followers • Design Guild"),
        Triple("ch_security", "Zero-Trust Cyber Alerts", "94K followers • Security Advisory")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .testTag("channels_tab_content"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Channels",
                        style = TextStyle(
                            fontFamily = PlusJakartaSansFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 19.sp,
                            color = ArenaCharcoalText
                        )
                    )
                    Text(
                        text = "Stay updated on topics that matter to you",
                        style = TextStyle(
                            fontFamily = PlusJakartaSansFont,
                            fontSize = 12.5.sp,
                            color = ArenaTextSecondary
                        )
                    )
                }

                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .clickable(onClick = onCreateChannelClick)
                        .testTag("create_channel_header_button"),
                    color = ArenaPrimaryEmerald,
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Create",
                            style = TextStyle(
                                fontFamily = PlusJakartaSansFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp,
                                color = Color.White
                            )
                        )
                    }
                }
            }
        }

        items(channels, key = { it.conversationId }) { channel ->
            ArenaConversationItemCard(
                item = channel,
                onOpenConversation = onOpenChannel,
                onOpenProfile = onOpenProfile,
                onOpenContextMenu = onOpenContextMenu
            )
        }

        // Find Channels to Follow section (WhatsApp Updates tab style)
        item {
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Find channels to follow",
                style = TextStyle(
                    fontFamily = PlusJakartaSansFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.5.sp,
                    color = ArenaTextSecondary
                ),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        items(suggestedChannels, key = { it.first }) { (id, title, subtitle) ->
            val isFollowed = followedIds.contains(id)
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFD8FDD2)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Campaign,
                                contentDescription = null,
                                tint = ArenaPrimaryEmerald,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = title,
                                    style = TextStyle(
                                        fontFamily = PlusJakartaSansFont,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 15.sp,
                                        color = ArenaCharcoalText
                                    )
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Verified",
                                    tint = ArenaPrimaryEmerald,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                            Text(
                                text = subtitle,
                                style = TextStyle(
                                    fontFamily = PlusJakartaSansFont,
                                    fontSize = 12.sp,
                                    color = ArenaTextSecondary
                                )
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .clickable {
                                if (!isFollowed) {
                                    followedIds.add(id)
                                    onFollowDirectoryChannel(title, subtitle)
                                } else {
                                    followedIds.remove(id)
                                }
                            },
                        color = if (isFollowed) Color(0xFFF0F2F5) else Color(0xFFD8FDD2),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isFollowed) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = ArenaCharcoalText,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text(
                                text = if (isFollowed) "Following" else "Follow",
                                style = TextStyle(
                                    fontFamily = PlusJakartaSansFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp,
                                    color = if (isFollowed) ArenaCharcoalText else ArenaPrimaryEmerald
                                )
                            )
                        }
                    }
                }
                HorizontalDivider(
                    modifier = Modifier.padding(start = 74.dp),
                    color = Color(0xFFF0F2F5),
                    thickness = 0.8.dp
                )
            }
        }
    }
}

@Composable
fun ArenaDiscoverTabContent(
    onConnectDiscoverItem: (String, String, String) -> Unit
) {
    var discoverQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("All") }

    val categories = listOf("All", "Personal", "Community", "Channel", "Group")
    val directoryItems = listOf(
        Triple("Arena Quantum Security Lab", "@quantum_lab • ARENA-1001", "community"),
        Triple("Global Creators Broadcast", "@arena_creators • 140K Subscribers", "channel"),
        Triple("Sophia Laurent (Verified)", "@sophia_ai_arch • ARENA-4490", "personal"),
        Triple("Android & Kotlin Architects", "@kotlin_guild • 12,800 Members", "group"),
        Triple("Dr. Zainab Al-Rashid", "@zainab_med • ARENA-8821", "personal"),
        Triple("WebRTC Ultra-Low Latency Guild", "@webrtc_stage • 6,400 Members", "community")
    )

    val filteredDirectory = directoryItems.filter { (title, handle, category) ->
        val matchesCategory = selectedCategoryFilter == "All" ||
            category.equals(selectedCategoryFilter, ignoreCase = true)
        val matchesQuery = discoverQuery.isBlank() ||
            title.contains(discoverQuery, ignoreCase = true) ||
            handle.contains(discoverQuery, ignoreCase = true)
        matchesCategory && matchesQuery
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .testTag("discover_tab_content"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                Text(
                    text = "Discover Global Directory",
                    style = TextStyle(
                        fontFamily = PlusJakartaSansFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp,
                        color = ArenaCharcoalText
                    )
                )
                Text(
                    text = "Find verified people, Arena IDs, public groups & channels",
                    style = TextStyle(
                        fontFamily = PlusJakartaSansFont,
                        fontSize = 12.5.sp,
                        color = ArenaTextSecondary
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Pill Search inside Discover
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFF0F2F5),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Directory",
                            tint = Color(0xFF54656F),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Box(modifier = Modifier.weight(1f)) {
                            if (discoverQuery.isEmpty()) {
                                Text(
                                    text = "Filter directory by name or Arena ID...",
                                    style = TextStyle(
                                        fontFamily = PlusJakartaSansFont,
                                        fontSize = 13.5.sp,
                                        color = Color(0xFF54656F)
                                    )
                                )
                            }
                            BasicTextField(
                                value = discoverQuery,
                                onValueChange = { discoverQuery = it },
                                singleLine = true,
                                textStyle = TextStyle(
                                    fontFamily = PlusJakartaSansFont,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 13.5.sp,
                                    color = ArenaCharcoalText
                                ),
                                cursorBrush = SolidColor(ArenaPrimaryEmerald),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        if (discoverQuery.isNotEmpty()) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = Color(0xFF54656F),
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable { discoverQuery = "" }
                            )
                        }
                    }
                }
            }

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    val selected = cat == selectedCategoryFilter
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .clickable { selectedCategoryFilter = cat },
                        color = if (selected) Color(0xFFD8FDD2) else Color(0xFFF0F2F5),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Text(
                            text = cat,
                            style = TextStyle(
                                fontFamily = PlusJakartaSansFont,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.5.sp,
                                color = if (selected) ArenaPrimaryEmerald else Color(0xFF54656F)
                            ),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(filteredDirectory, key = { it.first }) { (title, handle, category) ->
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onConnectDiscoverItem(title, handle, category) }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFD8FDD2)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (category) {
                                    "community" -> Icons.Default.Hub
                                    "channel" -> Icons.Default.Campaign
                                    "group" -> Icons.Default.Groups
                                    else -> Icons.Default.Explore
                                },
                                contentDescription = null,
                                tint = ArenaPrimaryEmerald,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = title,
                                    style = TextStyle(
                                        fontFamily = PlusJakartaSansFont,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 15.5.sp,
                                        color = ArenaCharcoalText
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = ArenaPrimaryEmerald,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                            Text(
                                text = handle,
                                style = TextStyle(
                                    fontFamily = PlusJakartaSansFont,
                                    fontSize = 12.5.sp,
                                    color = ArenaTextSecondary
                                )
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .clickable { onConnectDiscoverItem(title, handle, category) },
                        color = ArenaPrimaryEmerald,
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Text(
                            text = if (category == "personal") "Message" else "Join",
                            style = TextStyle(
                                fontFamily = PlusJakartaSansFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color.White
                            ),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                        )
                    }
                }
                HorizontalDivider(
                    modifier = Modifier.padding(start = 76.dp),
                    color = Color(0xFFF0F2F5),
                    thickness = 0.8.dp
                )
            }
        }
    }
}

@Composable
fun ArenaProfileTabContent(
    displayName: String,
    username: String,
    email: String,
    country: String,
    arenaId: String,
    isHumanVerified: Boolean,
    onOpenSettingsSection: (ProfileSettingsSection) -> Unit,
    onBackToOnboardingClick: () -> Unit,
    onSignOutClick: () -> Unit,
    onStatusNoteChanged: (String) -> Unit = {}
) {
    var aboutStatus by remember { mutableStateOf("Hey there! I am using ARENA CHAT (Quantum E2EE)") }
    var isEditingAbout by remember { mutableStateOf(false) }
    val statusPresets = listOf(
        "Available",
        "Busy • In Deep Work",
        "Building on Arena Chat 🚀",
        "Only encrypted calls"
    )

    val sectionIcons = mapOf(
        ProfileSettingsSection.MY_PROFILE to Icons.Default.Person,
        ProfileSettingsSection.ARENA_ID to Icons.Default.QrCode2,
        ProfileSettingsSection.PRIVACY to Icons.Default.Lock,
        ProfileSettingsSection.SECURITY to Icons.Default.Security,
        ProfileSettingsSection.NOTIFICATIONS to Icons.Default.Notifications,
        ProfileSettingsSection.APPEARANCE to Icons.Default.Palette,
        ProfileSettingsSection.LANGUAGE to Icons.Default.Language,
        ProfileSettingsSection.STORAGE to Icons.Default.Storage,
        ProfileSettingsSection.HELP_SUPPORT to Icons.Default.HelpOutline
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .testTag("profile_tab_content"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // WhatsApp Settings Profile Header Row
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(ArenaPrimaryEmerald, ArenaEmeraldDark)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = displayName.take(2).uppercase(),
                            style = TextStyle(
                                fontFamily = PlusJakartaSansFont,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 22.sp,
                                color = Color.White
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = displayName,
                                style = TextStyle(
                                    fontFamily = PlusJakartaSansFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = ArenaCharcoalText
                                )
                            )
                            if (isHumanVerified) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.VerifiedUser,
                                    contentDescription = "Verified Human",
                                    tint = ArenaPrimaryEmerald,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Text(
                            text = "@$username  •  $arenaId",
                            style = TextStyle(
                                fontFamily = PlusJakartaSansFont,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = ArenaPrimaryEmerald
                            )
                        )
                        Text(
                            text = aboutStatus,
                            style = TextStyle(
                                fontFamily = PlusJakartaSansFont,
                                fontSize = 12.5.sp,
                                color = ArenaTextSecondary
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    IconButton(
                        onClick = { onOpenSettingsSection(ProfileSettingsSection.ARENA_ID) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCode2,
                            contentDescription = "Arena ID QR",
                            tint = ArenaPrimaryEmerald,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    IconButton(
                        onClick = { isEditingAbout = !isEditingAbout }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit About Status",
                            tint = ArenaPrimaryEmerald,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                if (isEditingAbout) {
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(statusPresets) { preset ->
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable {
                                        aboutStatus = preset
                                        isEditingAbout = false
                                        onStatusNoteChanged("About status updated to \"$preset\"")
                                    },
                                color = if (aboutStatus == preset) Color(0xFFD8FDD2) else Color(0xFFF0F2F5),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text(
                                    text = preset,
                                    style = TextStyle(
                                        fontFamily = PlusJakartaSansFont,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp,
                                        color = if (aboutStatus == preset) ArenaPrimaryEmerald else ArenaCharcoalText
                                    ),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            HorizontalDivider(color = Color(0xFFF0F2F5), thickness = 1.dp)
        }

        // All 9 Profile & Settings Access items from Section 6
        items(ProfileSettingsSection.entries) { section ->
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenSettingsSection(section) }
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                        .testTag("settings_entry_${section.name.lowercase()}"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = sectionIcons[section] ?: Icons.Default.Person,
                            contentDescription = section.title,
                            tint = Color(0xFF54656F),
                            modifier = Modifier.size(23.dp)
                        )
                        Spacer(modifier = Modifier.width(18.dp))
                        Column {
                            Text(
                                text = section.title,
                                style = TextStyle(
                                    fontFamily = PlusJakartaSansFont,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.5.sp,
                                    color = ArenaCharcoalText
                                )
                            )
                            Text(
                                text = section.subtitle,
                                style = TextStyle(
                                    fontFamily = PlusJakartaSansFont,
                                    fontSize = 12.5.sp,
                                    color = ArenaTextSecondary
                                )
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                        tint = ArenaTextSecondary.copy(alpha = 0.6f),
                        modifier = Modifier.size(14.dp)
                    )
                }
                HorizontalDivider(
                    modifier = Modifier.padding(start = 56.dp),
                    color = Color(0xFFF0F2F5),
                    thickness = 0.8.dp
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, ArenaPrimaryEmerald, RoundedCornerShape(14.dp))
                        .clickable(onClick = onBackToOnboardingClick)
                        .testTag("view_part1_onboarding_button"),
                    color = Color(0xFFD8FDD2).copy(alpha = 0.45f),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = "Part 1 Onboarding",
                        style = TextStyle(
                            fontFamily = PlusJakartaSansFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp,
                            color = ArenaPrimaryEmerald
                        ),
                        modifier = Modifier.padding(vertical = 11.dp),
                        textAlign = TextAlign.Center
                    )
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, ArenaError.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                        .clickable(onClick = onSignOutClick)
                        .testTag("dashboard_sign_out_button"),
                    color = Color.White,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = "Sign Out",
                        style = TextStyle(
                            fontFamily = PlusJakartaSansFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp,
                            color = ArenaError
                        ),
                        modifier = Modifier.padding(vertical = 11.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
