package com.example.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MarkChatUnread
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ArenaNotification
import com.example.data.model.ArenaUserSettings
import com.example.data.model.ConversationItem
import com.example.data.model.FabNewActionType
import com.example.data.model.ProfileSettingsSection
import com.example.data.model.StoryItem
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
fun ArenaNotificationCenterDialog(
    notifications: List<ArenaNotification>,
    onDismiss: () -> Unit,
    onMarkRead: (String) -> Unit,
    onMarkAllRead: () -> Unit,
    onClearAll: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, ArenaGoldPrimary, RoundedCornerShape(22.dp))
                .testTag("notification_center_dialog"),
            color = ArenaSoftCream,
            shape = RoundedCornerShape(22.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Notification Center",
                            style = TextStyle(
                                fontFamily = PlusJakartaSansFont,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                color = ArenaCharcoalText
                            )
                        )
                        Text(
                            text = "${notifications.count { !it.isRead }} unread alerts",
                            style = TextStyle(
                                fontFamily = PlusJakartaSansFont,
                                fontSize = 12.sp,
                                color = ArenaPrimaryEmerald
                            )
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Notification Center",
                            tint = ArenaCharcoalText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(onClick = onMarkAllRead)
                            .testTag("mark_all_notifications_read_button"),
                        color = ArenaMintSoft,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.DoneAll,
                                contentDescription = null,
                                tint = ArenaPrimaryEmerald,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Mark all read",
                                style = TextStyle(
                                    fontFamily = PlusJakartaSansFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp,
                                    color = ArenaEmeraldDark
                                )
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, ArenaMintBorder, RoundedCornerShape(12.dp))
                            .clickable(onClick = onClearAll)
                            .testTag("clear_all_notifications_button"),
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = null,
                                tint = ArenaTextSecondary,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Clear all",
                                style = TextStyle(
                                    fontFamily = PlusJakartaSansFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp,
                                    color = ArenaTextSecondary
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (notifications.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "You're all caught up! No active notifications.",
                            style = TextStyle(
                                fontFamily = PlusJakartaSansFont,
                                fontSize = 13.sp,
                                color = ArenaTextSecondary
                            )
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 340.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(notifications, key = { it.notificationId }) { notif ->
                            val icon: ImageVector = when (notif.type) {
                                "call" -> Icons.Default.Phone
                                "mention", "group", "community" -> Icons.Default.Groups
                                "channel" -> Icons.Default.Campaign
                                "security" -> Icons.Default.Security
                                else -> Icons.Default.Notifications
                            }
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(
                                        width = 1.dp,
                                        color = if (!notif.isRead) ArenaPrimaryEmerald else ArenaMintBorder,
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .clickable { onMarkRead(notif.notificationId) },
                                color = if (!notif.isRead) Color.White else Color.White.copy(alpha = 0.75f),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(ArenaMintSoft),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = notif.type,
                                            tint = ArenaPrimaryEmerald,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = notif.title,
                                                style = TextStyle(
                                                    fontFamily = PlusJakartaSansFont,
                                                    fontWeight = if (!notif.isRead) FontWeight.ExtraBold else FontWeight.SemiBold,
                                                    fontSize = 12.5.sp,
                                                    color = ArenaCharcoalText
                                                ),
                                                modifier = Modifier.weight(1f)
                                            )
                                            Text(
                                                text = notif.timeLabel,
                                                style = TextStyle(
                                                    fontFamily = PlusJakartaSansFont,
                                                    fontSize = 10.sp,
                                                    color = ArenaTextSecondary
                                                )
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = notif.body,
                                            style = TextStyle(
                                                fontFamily = PlusJakartaSansFont,
                                                fontSize = 11.5.sp,
                                                color = ArenaTextSecondary
                                            )
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
}

@Composable
fun ArenaConversationContextMenuDialog(
    conversation: ConversationItem,
    onDismiss: () -> Unit,
    onOpenChat: () -> Unit,
    onOpenProfile: () -> Unit,
    onTogglePin: () -> Unit,
    onToggleFavorite: () -> Unit,
    onToggleMute: () -> Unit,
    onToggleUnread: () -> Unit,
    onToggleArchive: () -> Unit,
    onRequestDelete: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, ArenaGoldPrimary, RoundedCornerShape(22.dp))
                .testTag("conversation_context_menu_dialog"),
            color = Color.White,
            shape = RoundedCornerShape(22.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = conversation.displayName,
                    style = TextStyle(
                        fontFamily = PlusJakartaSansFont,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 17.sp,
                        color = ArenaCharcoalText
                    )
                )
                Text(
                    text = conversation.handleOrArenaId,
                    style = TextStyle(
                        fontFamily = PlusJakartaSansFont,
                        fontSize = 12.sp,
                        color = ArenaTextSecondary
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))

                ContextActionRow(
                    icon = Icons.Default.Person,
                    label = "Open User / Group Profile",
                    testTag = "context_action_profile",
                    onClick = onOpenProfile
                )
                ContextActionRow(
                    icon = Icons.Default.PushPin,
                    label = if (conversation.isPinned) "Unpin Conversation" else "Pin to Top",
                    testTag = "context_action_pin",
                    onClick = onTogglePin
                )
                ContextActionRow(
                    icon = Icons.Default.Star,
                    label = if (conversation.isFavorite) "Remove from Favorites" else "Add to Favorites",
                    testTag = "context_action_favorite",
                    onClick = onToggleFavorite
                )
                ContextActionRow(
                    icon = Icons.Default.VolumeOff,
                    label = if (conversation.isMuted) "Unmute Notifications" else "Mute Notifications",
                    testTag = "context_action_mute",
                    onClick = onToggleMute
                )
                ContextActionRow(
                    icon = Icons.Default.MarkChatUnread,
                    label = if (conversation.unreadCount > 0) "Mark as Read" else "Mark as Unread",
                    testTag = "context_action_unread",
                    onClick = onToggleUnread
                )
                ContextActionRow(
                    icon = Icons.Default.Archive,
                    label = if (conversation.isArchived) "Unarchive Conversation" else "Archive Conversation",
                    testTag = "context_action_archive",
                    onClick = onToggleArchive
                )
                ContextActionRow(
                    icon = Icons.Default.DeleteOutline,
                    label = "Delete Conversation",
                    testTag = "context_action_delete",
                    tint = ArenaError,
                    onClick = onRequestDelete
                )
            }
        }
    }
}

@Composable
private fun ContextActionRow(
    icon: ImageVector,
    label: String,
    testTag: String,
    tint: Color = ArenaCharcoalText,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (tint == ArenaError) ArenaError else ArenaPrimaryEmerald,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            style = TextStyle(
                fontFamily = PlusJakartaSansFont,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.5.sp,
                color = tint
            )
        )
    }
}

@Composable
fun ArenaDeleteConfirmationDialog(
    conversation: ConversationItem,
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ArenaSoftCream,
        title = {
            Text(
                text = "Delete \"${conversation.displayName}\"?",
                style = TextStyle(
                    fontFamily = PlusJakartaSansFont,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 17.sp,
                    color = ArenaCharcoalText
                )
            )
        },
        text = {
            Text(
                text = "This will remove the conversation from your Arena Chat dashboard. This action cannot be undone.",
                style = TextStyle(
                    fontFamily = PlusJakartaSansFont,
                    fontSize = 13.sp,
                    color = ArenaTextSecondary
                )
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirmDelete,
                modifier = Modifier.testTag("confirm_delete_conversation_button")
            ) {
                Text(
                    text = "Delete",
                    style = TextStyle(
                        fontFamily = PlusJakartaSansFont,
                        fontWeight = FontWeight.Bold,
                        color = ArenaError
                    )
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                    style = TextStyle(
                        fontFamily = PlusJakartaSansFont,
                        fontWeight = FontWeight.SemiBold,
                        color = ArenaCharcoalText
                    )
                )
            }
        }
    )
}

@Composable
fun ArenaConversationPreviewModal(
    conversation: ConversationItem,
    onDismiss: () -> Unit,
    onSendMessage: (String) -> Unit,
    onOpenProfile: () -> Unit
) {
    var messageInput by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, ArenaGoldPrimary, RoundedCornerShape(22.dp))
                .testTag("opened_conversation_modal"),
            color = ArenaSoftCream,
            shape = RoundedCornerShape(22.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .clickable(onClick = onOpenProfile)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(ArenaPrimaryEmerald, ArenaEmeraldDark)
                                    )
                                )
                                .border(1.5.dp, ArenaGoldPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = conversation.displayName.take(2).uppercase(),
                                style = TextStyle(
                                    fontFamily = PlusJakartaSansFont,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp,
                                    color = Color.White
                                )
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = conversation.displayName,
                                    style = TextStyle(
                                        fontFamily = PlusJakartaSansFont,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 15.sp,
                                        color = ArenaCharcoalText
                                    )
                                )
                                if (conversation.isVerified) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = null,
                                        tint = ArenaPrimaryEmerald,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Text(
                                text = "${conversation.handleOrArenaId} • ${if (conversation.isOnline) "Online" else "Encrypted"}",
                                style = TextStyle(
                                    fontFamily = PlusJakartaSansFont,
                                    fontSize = 11.sp,
                                    color = ArenaTextSecondary
                                )
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Conversation",
                            tint = ArenaCharcoalText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // End-to-End Encryption Notice Banner
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = ArenaMintSoft,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = ArenaPrimaryEmerald,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Messages are protected with Arena Quantum-Safe E2EE.",
                            style = TextStyle(
                                fontFamily = PlusJakartaSansFont,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp,
                                color = ArenaEmeraldDark
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Last Message Bubble
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, ArenaMintBorder, RoundedCornerShape(16.dp)),
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = conversation.lastMessagePreview,
                            style = TextStyle(
                                fontFamily = PlusJakartaSansFont,
                                fontSize = 13.5.sp,
                                color = ArenaCharcoalText
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${conversation.lastMessageTimeLabel} • ${conversation.deliveryStatus.uppercase()}",
                            style = TextStyle(
                                fontFamily = PlusJakartaSansFont,
                                fontSize = 10.sp,
                                color = ArenaTextSecondary
                            ),
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quick Reply Input
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = messageInput,
                        onValueChange = { messageInput = it },
                        placeholder = {
                            Text(
                                "Write an encrypted reply...",
                                style = TextStyle(fontFamily = PlusJakartaSansFont, fontSize = 12.5.sp)
                            )
                        },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quick_reply_input")
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(ArenaPrimaryEmerald)
                            .clickable {
                                if (messageInput.isNotBlank()) {
                                    onSendMessage(messageInput)
                                    messageInput = ""
                                }
                            }
                            .testTag("send_quick_message_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send Message",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ArenaFabCreationModal(
    actionType: FabNewActionType,
    onDismiss: () -> Unit,
    onCreate: (String, String, String) -> Unit
) {
    var nameInput by remember { mutableStateOf("") }
    var handleInput by remember { mutableStateOf("") }
    var messageInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ArenaSoftCream,
        title = {
            Text(
                text = actionType.title,
                style = TextStyle(
                    fontFamily = PlusJakartaSansFont,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = ArenaCharcoalText
                )
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = actionType.subtitle,
                    style = TextStyle(
                        fontFamily = PlusJakartaSansFont,
                        fontSize = 12.sp,
                        color = ArenaTextSecondary
                    )
                )
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Display Name / Title") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("fab_create_name_input")
                )
                OutlinedTextField(
                    value = handleInput,
                    onValueChange = { handleInput = it },
                    label = { Text("@username or Arena ID") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("fab_create_handle_input")
                )
                OutlinedTextField(
                    value = messageInput,
                    onValueChange = { messageInput = it },
                    label = { Text("Initial Message or Description") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("fab_create_message_input")
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onCreate(nameInput, handleInput, messageInput) },
                modifier = Modifier.testTag("fab_create_submit_button")
            ) {
                Text(
                    text = "Create",
                    style = TextStyle(
                        fontFamily = PlusJakartaSansFont,
                        fontWeight = FontWeight.Bold,
                        color = ArenaPrimaryEmerald
                    )
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ArenaStoryViewerModal(
    story: StoryItem,
    onDismiss: () -> Unit,
    onReplyToStory: (String) -> Unit = {}
) {
    var replyInput by remember { mutableStateOf("") }
    val quickEmojis = listOf("❤️", "🔥", "👏", "😂", "😮", "🎉")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, ArenaGoldPrimary, RoundedCornerShape(24.dp))
                .testTag("story_viewer_modal"),
            color = Color(0xFF0B141A),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // WhatsApp Status top progress bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.White.copy(alpha = 0.28f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(3.dp)
                            .background( Color(0xFF25D366))
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(ArenaPrimaryEmerald),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = story.authorName.take(2).uppercase(),
                                style = TextStyle(
                                    fontFamily = PlusJakartaSansFont,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = story.authorName,
                                style = TextStyle(
                                    fontFamily = PlusJakartaSansFont,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "@${story.authorUsername} • ${story.timestampLabel}",
                                style = TextStyle(
                                    fontFamily = PlusJakartaSansFont,
                                    fontSize = 11.5.sp,
                                    color = ArenaGoldLight
                                )
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Story",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = ArenaEmeraldDark,
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = story.caption,
                            style = TextStyle(
                                fontFamily = PlusJakartaSansFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                lineHeight = 26.sp,
                                color = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Privacy: ${story.privacyLevel.replace("_", " ").uppercase()}",
                            style = TextStyle(
                                fontFamily = MontserratFont,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 10.sp,
                                color = ArenaGoldLight
                            )
                        )
                    }
                }

                if (!story.isOwnStory) {
                    Spacer(modifier = Modifier.height(14.dp))

                    // Quick Reaction Emojis
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        quickEmojis.forEach { emoji ->
                            Surface(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .clickable { onReplyToStory(emoji) },
                                color = Color.White.copy(alpha = 0.12f),
                                shape = CircleShape
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = emoji, fontSize = 17.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // WhatsApp Status Reply Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = replyInput,
                            onValueChange = { replyInput = it },
                            placeholder = {
                                Text(
                                    text = "Reply to ${story.authorName}...",
                                    style = TextStyle(
                                        fontFamily = PlusJakartaSansFont,
                                        fontSize = 12.5.sp,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                )
                            },
                            singleLine = true,
                            textStyle = TextStyle(
                                fontFamily = PlusJakartaSansFont,
                                fontSize = 13.sp,
                                color = Color.White
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("story_reply_input")
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF25D366))
                                .clickable {
                                    if (replyInput.isNotBlank()) {
                                        onReplyToStory(replyInput)
                                        replyInput = ""
                                    }
                                }
                                .testTag("story_reply_send_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send Story Reply",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ArenaAddStoryModal(
    defaultAuthorName: String,
    defaultUsername: String,
    currentPrivacy: String,
    onDismiss: () -> Unit,
    onPublish: (String, String, Int) -> Unit
) {
    var captionInput by remember { mutableStateOf("") }
    var selectedPrivacy by remember { mutableStateOf(currentPrivacy) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ArenaSoftCream,
        title = {
            Text(
                text = "Publish Status Story",
                style = TextStyle(
                    fontFamily = PlusJakartaSansFont,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = ArenaCharcoalText
                )
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Posting as $defaultAuthorName (@$defaultUsername)",
                    style = TextStyle(
                        fontFamily = PlusJakartaSansFont,
                        fontSize = 12.sp,
                        color = ArenaTextSecondary
                    )
                )
                OutlinedTextField(
                    value = captionInput,
                    onValueChange = { captionInput = it },
                    label = { Text("What's on your mind?") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_story_caption_input")
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("all_contacts" to "Contacts", "close_friends" to "Close Friends", "selected_only" to "Selected").forEach { (key, label) ->
                        val selected = selectedPrivacy == key
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedPrivacy = key },
                            color = if (selected) ArenaPrimaryEmerald else Color.White,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = label,
                                style = TextStyle(
                                    fontFamily = PlusJakartaSansFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = if (selected) Color.White else ArenaCharcoalText
                                ),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onPublish(captionInput, selectedPrivacy, (0..4).random()) },
                modifier = Modifier.testTag("publish_story_submit_button")
            ) {
                Text(
                    text = "Share Status",
                    style = TextStyle(
                        fontFamily = PlusJakartaSansFont,
                        fontWeight = FontWeight.Bold,
                        color = ArenaPrimaryEmerald
                    )
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ArenaSettingsSectionModal(
    section: ProfileSettingsSection,
    settings: ArenaUserSettings,
    arenaId: String,
    username: String,
    onDismiss: () -> Unit,
    onSaveSettings: (ArenaUserSettings) -> Unit
) {
    var e2eeStrict by remember(settings) { mutableStateOf(settings.e2eeStrictMode) }
    var readReceipts by remember(settings) { mutableStateOf(settings.readReceiptsEnabled) }
    var typingIndicators by remember(settings) { mutableStateOf(settings.typingIndicatorsEnabled) }
    var notificationsEnabled by remember(settings) { mutableStateOf(settings.notificationsEnabled) }
    var storyPrivacy by remember(settings) { mutableStateOf(settings.storyPrivacy) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ArenaSoftCream,
        title = {
            Text(
                text = section.title,
                style = TextStyle(
                    fontFamily = PlusJakartaSansFont,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = ArenaCharcoalText
                )
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = section.subtitle,
                    style = TextStyle(
                        fontFamily = PlusJakartaSansFont,
                        fontSize = 12.5.sp,
                        color = ArenaTextSecondary
                    )
                )

                when (section) {
                    ProfileSettingsSection.ARENA_ID, ProfileSettingsSection.MY_PROFILE -> {
                        Surface(
                            color = ArenaMintSoft,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Arena ID: $arenaId",
                                    style = TextStyle(
                                        fontFamily = MontserratFont,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 14.sp,
                                        color = ArenaEmeraldDark
                                    )
                                )
                                Text(
                                    text = "Handle: @$username\nStatus: Human Verified & E2EE Active",
                                    style = TextStyle(
                                        fontFamily = PlusJakartaSansFont,
                                        fontSize = 12.sp,
                                        color = ArenaCharcoalText
                                    )
                                )
                            }
                        }
                    }

                    ProfileSettingsSection.PRIVACY, ProfileSettingsSection.SECURITY, ProfileSettingsSection.NOTIFICATIONS -> {
                        SettingToggleRow("Strict Quantum-Safe E2EE", e2eeStrict) { e2eeStrict = it }
                        SettingToggleRow("Send Read Receipts", readReceipts) { readReceipts = it }
                        SettingToggleRow("Share Typing Indicators", typingIndicators) { typingIndicators = it }
                        SettingToggleRow("Real-time Push & In-App Alerts", notificationsEnabled) { notificationsEnabled = it }
                    }

                    else -> {
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, ArenaMintBorder, RoundedCornerShape(14.dp))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "ARENA CHAT Part 2.1 Architecture Ready",
                                    style = TextStyle(
                                        fontFamily = PlusJakartaSansFont,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = ArenaPrimaryEmerald
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Theme: Emerald (#087F5B) & Soft Cream (#FFF9EF)\n" +
                                        "Language: ${settings.languageCode}\n" +
                                        "Firestore Collections: /users, /usernames, /conversations, /stories, /notifications, /user_settings.\n" +
                                        "Upcoming Phase 2.2+ modules: Live WebRTC Media Relay, Multi-room Voice Stages & Cloud File Vault.",
                                    style = TextStyle(
                                        fontFamily = PlusJakartaSansFont,
                                        fontSize = 11.5.sp,
                                        lineHeight = 17.sp,
                                        color = ArenaTextSecondary
                                    )
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSaveSettings(
                        settings.copy(
                            e2eeStrictMode = e2eeStrict,
                            readReceiptsEnabled = readReceipts,
                            typingIndicatorsEnabled = typingIndicators,
                            notificationsEnabled = notificationsEnabled,
                            storyPrivacy = storyPrivacy
                        )
                    )
                    onDismiss()
                }
            ) {
                Text(
                    text = "Save & Close",
                    style = TextStyle(
                        fontFamily = PlusJakartaSansFont,
                        fontWeight = FontWeight.Bold,
                        color = ArenaPrimaryEmerald
                    )
                )
            }
        }
    )
}

@Composable
private fun SettingToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontFamily = PlusJakartaSansFont,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = ArenaCharcoalText
            ),
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = ArenaPrimaryEmerald
            )
        )
    }
}

@Composable
fun WhatsAppContactInfoModal(
    conversation: ConversationItem,
    messageCount: Int,
    onDismiss: () -> Unit,
    onOpenChat: () -> Unit,
    onStartVoiceCall: () -> Unit,
    onStartVideoCall: () -> Unit,
    onToggleMute: () -> Unit,
    onToggleFavorite: () -> Unit,
    onRequestDelete: () -> Unit
) {
    var safetyCodeVerified by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, ArenaMintBorder, RoundedCornerShape(24.dp))
                .testTag("contact_info_modal"),
            color = Color.White,
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (conversation.category == "personal") "Contact info" else "Group / Channel info",
                        style = TextStyle(
                            fontFamily = PlusJakartaSansFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = ArenaTextSecondary
                        )
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Contact Info",
                            tint = ArenaCharcoalText
                        )
                    }
                }

                // Large Avatar
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(ArenaPrimaryEmerald, ArenaEmeraldDark)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = conversation.displayName.take(2).uppercase(),
                        style = TextStyle(
                            fontFamily = PlusJakartaSansFont,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 26.sp,
                            color = Color.White
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = conversation.displayName,
                        style = TextStyle(
                            fontFamily = PlusJakartaSansFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 19.sp,
                            color = ArenaCharcoalText
                        )
                    )
                    if (conversation.isVerified) {
                        Spacer(modifier = Modifier.width(5.dp))
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verified",
                            tint = ArenaPrimaryEmerald,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Text(
                    text = "${conversation.handleOrArenaId} • ${if (conversation.isOnline) "Online" else "${conversation.memberCount} members"}",
                    style = TextStyle(
                        fontFamily = PlusJakartaSansFont,
                        fontSize = 12.5.sp,
                        color = ArenaTextSecondary
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // WhatsApp 3-Action Row: Audio, Video, Message
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ContactInfoActionCard(
                        icon = Icons.Default.Phone,
                        label = "Audio",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onDismiss()
                            onStartVoiceCall()
                        }
                    )
                    ContactInfoActionCard(
                        icon = Icons.Default.Videocam,
                        label = "Video",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onDismiss()
                            onStartVideoCall()
                        }
                    )
                    ContactInfoActionCard(
                        icon = Icons.AutoMirrored.Filled.Send,
                        label = "Message",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onDismiss()
                            onOpenChat()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Encryption & Media Summary Box
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { safetyCodeVerified = !safetyCodeVerified },
                    color = Color(0xFFF0F2F5),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (safetyCodeVerified) Icons.Default.Check else Icons.Default.Lock,
                            contentDescription = "Encryption",
                            tint = ArenaPrimaryEmerald,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (safetyCodeVerified) {
                                    "Quantum Safety Number Verified ✓"
                                } else {
                                    "Encryption • $messageCount messages in chat"
                                },
                                style = TextStyle(
                                    fontFamily = PlusJakartaSansFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = ArenaCharcoalText
                                )
                            )
                            Text(
                                text = if (safetyCodeVerified) {
                                    "Safety code: 84920 11940 77312 55019"
                                } else {
                                    "Messages and calls are end-to-end encrypted. Tap to verify."
                                },
                                style = TextStyle(
                                    fontFamily = PlusJakartaSansFont,
                                    fontSize = 11.5.sp,
                                    color = ArenaTextSecondary
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                SettingToggleRow(
                    label = "Mute notifications",
                    checked = conversation.isMuted,
                    onCheckedChange = { onToggleMute() }
                )
                SettingToggleRow(
                    label = "Add to Favorites",
                    checked = conversation.isFavorite,
                    onCheckedChange = { onToggleFavorite() }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            onDismiss()
                            onRequestDelete()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = null,
                            tint = ArenaError,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Delete Chat",
                            style = TextStyle(
                                fontFamily = PlusJakartaSansFont,
                                fontWeight = FontWeight.Bold,
                                color = ArenaError
                            )
                        )
                    }

                    TextButton(onClick = onDismiss) {
                        Text(
                            text = "Done",
                            style = TextStyle(
                                fontFamily = PlusJakartaSansFont,
                                fontWeight = FontWeight.Bold,
                                color = ArenaPrimaryEmerald
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ContactInfoActionCard(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, Color(0xFFE2E8EC), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        color = Color.White,
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = ArenaPrimaryEmerald,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = TextStyle(
                    fontFamily = PlusJakartaSansFont,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = ArenaPrimaryEmerald
                )
            )
        }
    }
}
