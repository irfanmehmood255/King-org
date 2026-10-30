package com.example.ui.screens.dashboard

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
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
import com.example.data.model.ActiveCallSession
import com.example.data.model.ArenaAiMessage
import com.example.data.model.ArenaAiMode
import com.example.data.model.ArenaConversationAiInsight
import com.example.data.model.ChatMessage
import com.example.data.model.ChatMessageType
import com.example.data.model.ChatRoom
import com.example.data.model.ConversationItem
import com.example.ui.theme.ArenaEmeraldDark
import com.example.ui.theme.ArenaEmeraldDeep
import com.example.ui.theme.ArenaError
import com.example.ui.theme.ArenaGoldLight
import com.example.ui.theme.ArenaGoldPrimary
import com.example.ui.theme.ArenaMintBorder
import com.example.ui.theme.ArenaMintSoft
import com.example.data.repository.ArenaDashboardRepository
import com.example.ui.theme.ArenaTextMuted
import com.example.ui.theme.ArenaTextSecondary
import com.example.ui.theme.PlusJakartaSansFont
import com.example.ui.viewmodel.defaultSeededMessages
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val WhatsAppOutgoingBubbleGreen = Color(0xFFD9FDD3)
private val WhatsAppChatWallpaperCream = Color(0xFFEFEAE2)
private val WhatsAppReadTickBlueEmerald = Color(0xFF00A884)

@Composable
fun WhatsAppChatRoomScreen(
    conversation: ConversationItem,
    messages: List<ChatMessage>,
    replyingToMessage: ChatMessage?,
    inChatSearchQuery: String,
    isInChatSearchActive: Boolean,
    wallpaperPreset: Int,
    onBackClick: () -> Unit,
    onOpenContactInfo: () -> Unit,
    onStartVoiceCall: () -> Unit,
    onStartVideoCall: () -> Unit,
    onSendMessage: (String) -> Unit,
    onSendVoiceNote: (Int) -> Unit,
    onSendImageAttachment: (String?, String) -> Unit,
    onSendDocument: (String, String) -> Unit,
    onSendLocation: (String, String) -> Unit,
    onSendPoll: (String, List<String>) -> Unit,
    onVotePoll: (String, String) -> Unit,
    onSetReplyMessage: (ChatMessage?) -> Unit,
    onToggleReaction: (String, String) -> Unit,
    onToggleStarMessage: (String) -> Unit,
    onDeleteMessage: (String) -> Unit,
    onToggleSearchActive: (Boolean) -> Unit,
    onUpdateSearchQuery: (String) -> Unit,
    onToggleMuteChat: () -> Unit,
    onToggleFavoriteChat: () -> Unit,
    onCycleWallpaper: () -> Unit,
    onClearChat: () -> Unit
) {
    ChatScreen(
        conversation = conversation,
        messages = messages,
        onSendMessage = onSendMessage,
        onBackClick = onBackClick,
        replyingToMessage = replyingToMessage,
        inChatSearchQuery = inChatSearchQuery,
        isInChatSearchActive = isInChatSearchActive,
        wallpaperPreset = wallpaperPreset,
        onOpenContactInfo = onOpenContactInfo,
        onStartVoiceCall = onStartVoiceCall,
        onStartVideoCall = onStartVideoCall,
        onSendVoiceNote = onSendVoiceNote,
        onSendImageAttachment = onSendImageAttachment,
        onSendDocument = onSendDocument,
        onSendLocation = onSendLocation,
        onSendPoll = onSendPoll,
        onVotePoll = onVotePoll,
        onSetReplyMessage = onSetReplyMessage,
        onToggleReaction = onToggleReaction,
        onToggleStarMessage = onToggleStarMessage,
        onDeleteMessage = onDeleteMessage,
        onToggleSearchActive = onToggleSearchActive,
        onUpdateSearchQuery = onUpdateSearchQuery,
        onToggleMuteChat = onToggleMuteChat,
        onToggleFavoriteChat = onToggleFavoriteChat,
        onCycleWallpaper = onCycleWallpaper,
        onClearChat = onClearChat
    )
}

@Composable
fun ChatScreen(
    conversation: ConversationItem = ArenaDashboardRepository.defaultConversations().first(),
    messages: List<ChatMessage> = defaultSeededMessages()[conversation.conversationId].orEmpty(),
    onSendMessage: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    chatRoom: ChatRoom? = null,
    replyingToMessage: ChatMessage? = null,
    inChatSearchQuery: String = "",
    isInChatSearchActive: Boolean = false,
    wallpaperPreset: Int = 0,
    onOpenContactInfo: () -> Unit = {},
    onStartVoiceCall: () -> Unit = {},
    onStartVideoCall: () -> Unit = {},
    onSendVoiceNote: (Int) -> Unit = {},
    onSendImageAttachment: (String?, String) -> Unit = { _, _ -> },
    onSendDocument: (String, String) -> Unit = { _, _ -> },
    onSendLocation: (String, String) -> Unit = { _, _ -> },
    onSendPoll: (String, List<String>) -> Unit = { _, _ -> },
    onVotePoll: (String, String) -> Unit = { _, _ -> },
    onSetReplyMessage: (ChatMessage?) -> Unit = {},
    onToggleReaction: (String, String) -> Unit = { _, _ -> },
    onToggleStarMessage: (String) -> Unit = {},
    onDeleteMessage: (String) -> Unit = {},
    onToggleSearchActive: (Boolean) -> Unit = {},
    onUpdateSearchQuery: (String) -> Unit = {},
    onToggleMuteChat: () -> Unit = {},
    onToggleFavoriteChat: () -> Unit = {},
    onCycleWallpaper: () -> Unit = {},
    onClearChat: () -> Unit = {},
    onOpenArenaAi: ((ArenaAiMode, String) -> Unit)? = null
) {
    val activeConversation = remember(conversation, chatRoom) {
        chatRoom?.toConversationItem() ?: conversation
    }
    var messageInput by remember { mutableStateOf("") }
    var localAppendedMessages by remember(activeConversation.conversationId) {
        mutableStateOf<List<ChatMessage>>(emptyList())
    }
    var showAttachmentSheet by remember { mutableStateOf(false) }
    var showEmojiPicker by remember { mutableStateOf(false) }
    var showOverflowMenu by remember { mutableStateOf(false) }
    var showCreatePollDialog by remember { mutableStateOf(false) }
    var selectedMessageForActions by remember { mutableStateOf<String?>(null) }
    var showLocalArenaAiStudio by remember { mutableStateOf(false) }
    var localArenaAiDraft by remember { mutableStateOf("") }
    var localArenaAiGenerating by remember { mutableStateOf(false) }
    var localArenaAiMessages by remember {
        mutableStateOf(
            listOf(
                ArenaAiMessage(
                    id = "ai_local_welcome",
                    isUser = false,
                    text = "Assalam-o-Alaikum & Welcome to **ARENA AI** ✨\n\n" +
                        "I am automatically analyzing your conversation with **${activeConversation.displayName}**. " +
                        "Ask anything, request real-time web facts, or say **\"Create an 8K image of...\"** right here.",
                    timeLabel = "Now",
                    mode = ArenaAiMode.DEEP_CHAT,
                    sourceHighlights = listOf("Arena AI Auto-Intelligence • gemini-3.8-flash")
                )
            )
        )
    }
    val coroutineScope = rememberCoroutineScope()

    // Voice note live recording state
    var isRecordingVoiceNote by remember { mutableStateOf(false) }
    var recordingSeconds by remember { mutableIntStateOf(0) }

    LaunchedEffect(isRecordingVoiceNote) {
        if (isRecordingVoiceNote) {
            recordingSeconds = 0
            while (isRecordingVoiceNote) {
                delay(1000)
                recordingSeconds += 1
            }
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onSendImageAttachment(uri.toString(), "Shared photo from gallery")
        }
    }

    LaunchedEffect(messages.isEmpty()) {
        if (messages.isEmpty()) {
            localAppendedMessages = emptyList()
        }
    }

    val mergedMessages = remember(messages, localAppendedMessages) {
        val existingTextsFromMe = messages.filter { it.isFromMe }.map { it.text }.toSet()
        val nonDuplicatedLocal = localAppendedMessages.filter { it.text !in existingTextsFromMe }
        messages + nonDuplicatedLocal
    }

    val displayedMessages = remember(mergedMessages, inChatSearchQuery) {
        val q = inChatSearchQuery.trim().lowercase()
        if (q.isEmpty()) mergedMessages
        else mergedMessages.filter {
            it.text.lowercase().contains(q) ||
                (it.documentTitle?.lowercase()?.contains(q) == true) ||
                (it.pollQuestion?.lowercase()?.contains(q) == true)
        }
    }

    val baselineInsight = remember(activeConversation, displayedMessages) {
        ArenaConversationAiService.analyzeConversationHistory(activeConversation, displayedMessages)
    }
    var liveConversationInsight by remember(activeConversation.conversationId) {
        mutableStateOf(baselineInsight)
    }
    LaunchedEffect(activeConversation.conversationId, displayedMessages.size, displayedMessages.lastOrNull()?.text) {
        liveConversationInsight = baselineInsight
        ArenaConversationAiService.streamConversationHistoryInsight(
            conversation = activeConversation,
            messages = displayedMessages
        ).collect { updatedInsight ->
            liveConversationInsight = updatedInsight
        }
    }

    val listState = rememberLazyListState()
    LaunchedEffect(displayedMessages.size) {
        if (displayedMessages.isNotEmpty()) {
            listState.animateScrollToItem(displayedMessages.size - 1)
        }
    }

    BackHandler {
        when {
            showLocalArenaAiStudio -> showLocalArenaAiStudio = false
            showAttachmentSheet -> showAttachmentSheet = false
            showEmojiPicker -> showEmojiPicker = false
            isInChatSearchActive -> onToggleSearchActive(false)
            isRecordingVoiceNote -> isRecordingVoiceNote = false
            replyingToMessage != null -> onSetReplyMessage(null)
            else -> onBackClick()
        }
    }

    val wallpaperColor = when (wallpaperPreset % 4) {
        0 -> WhatsAppChatWallpaperCream
        1 -> Color(0xFFE6F4EE)
        2 -> Color(0xFF1E2623)
        else -> Color(0xFFFBF5E6)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(wallpaperColor)
            .windowInsetsPadding(WindowInsets.statusBars)
            .imePadding()
            .testTag("opened_conversation_modal")
    ) {
        // ====================================================================
        // 1. WHATSAPP-STYLE CHAT TOP BAR
        // ====================================================================
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(3.dp),
            color = ArenaPrimaryEmerald
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(42.dp)
                            .testTag("chat_room_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Chats",
                            tint = Color.White
                        )
                    }

                    // Avatar + Name + Live Status (Clickable to open Contact Info)
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable(onClick = onOpenContactInfo)
                            .padding(vertical = 2.dp, horizontal = 4.dp)
                            .testTag("chat_room_header_profile"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val isArenaAiRoom = conversation.displayName.contains("ARENA AI", ignoreCase = true) ||
                            conversation.handleOrArenaId.contains("@arena_ai", ignoreCase = true)
                        if (isArenaAiRoom) {
                            ArenaAiRingLogo(size = 40.dp)
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(ArenaEmeraldDark, ArenaEmeraldDeep)
                                        )
                                    )
                                    .border(1.5.dp, ArenaGoldPrimary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = conversation.displayName
                                        .split(" ")
                                        .mapNotNull { it.firstOrNull()?.uppercase() }
                                        .take(2)
                                        .joinToString(""),
                                    style = TextStyle(
                                        fontFamily = PlusJakartaSansFont,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 14.sp,
                                        color = Color.White
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = conversation.displayName,
                                    style = TextStyle(
                                        fontFamily = PlusJakartaSansFont,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color.White
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (conversation.isVerified) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = "Verified",
                                        tint = ArenaGoldPrimary,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }

                            val subtitleText = when {
                                conversation.isTyping && isArenaAiRoom -> "ARENA AI is synthesizing..."
                                conversation.isTyping -> "typing..."
                                isArenaAiRoom -> "Auto Neural Engine • Chat, Live Web & 8K Images"
                                conversation.category == "group" || conversation.category == "community" ->
                                    "${conversation.memberCount} members • ${conversation.handleOrArenaId}"
                                conversation.category == "channel" ->
                                    "${conversation.memberCount} subscribers • Broadcast"
                                conversation.isOnline -> "online"
                                else -> "last seen today at ${conversation.lastMessageTimeLabel}"
                            }
                            Text(
                                text = subtitleText,
                                style = TextStyle(
                                    fontFamily = PlusJakartaSansFont,
                                    fontWeight = if (conversation.isTyping) FontWeight.Bold else FontWeight.Medium,
                                    fontStyle = if (conversation.isTyping) FontStyle.Italic else FontStyle.Normal,
                                    fontSize = 12.sp,
                                    color = if (conversation.isTyping) ArenaGoldLight else Color.White.copy(alpha = 0.88f)
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Video Call, Voice Call, and 3-Dot Menu
                    IconButton(
                        onClick = onStartVideoCall,
                        modifier = Modifier
                            .size(44.dp)
                            .testTag("chat_top_video_call_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = "Video Call",
                            tint = Color.White,
                            modifier = Modifier.size(23.dp)
                        )
                    }

                    IconButton(
                        onClick = onStartVoiceCall,
                        modifier = Modifier
                            .size(44.dp)
                            .testTag("chat_top_voice_call_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Voice Call",
                            tint = Color.White,
                            modifier = Modifier.size(21.dp)
                        )
                    }

                    Box {
                        IconButton(
                            onClick = { showOverflowMenu = true },
                            modifier = Modifier
                                .size(44.dp)
                                .testTag("chat_top_more_menu_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More Options",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showOverflowMenu,
                            onDismissRequest = { showOverflowMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("View contact / info") },
                                onClick = {
                                    showOverflowMenu = false
                                    onOpenContactInfo()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Search in chat") },
                                onClick = {
                                    showOverflowMenu = false
                                    onToggleSearchActive(true)
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(if (conversation.isFavorite) "Remove from Favorites" else "Add to Favorites")
                                },
                                onClick = {
                                    showOverflowMenu = false
                                    onToggleFavoriteChat()
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(if (conversation.isMuted) "Unmute notifications" else "Mute notifications")
                                },
                                onClick = {
                                    showOverflowMenu = false
                                    onToggleMuteChat()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Change chat wallpaper") },
                                onClick = {
                                    showOverflowMenu = false
                                    onCycleWallpaper()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Clear chat", color = ArenaError) },
                                onClick = {
                                    showOverflowMenu = false
                                    localAppendedMessages = emptyList()
                                    onClearChat()
                                }
                            )
                        }
                    }
                }

                // Expandable In-Chat Search Bar
                AnimatedVisibility(visible = isInChatSearchActive) {
                    Surface(
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = ArenaPrimaryEmerald,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            BasicTextField(
                                value = inChatSearchQuery,
                                onValueChange = onUpdateSearchQuery,
                                singleLine = true,
                                textStyle = TextStyle(
                                    fontFamily = PlusJakartaSansFont,
                                    fontSize = 14.sp,
                                    color = ArenaCharcoalText
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("in_chat_search_input"),
                                decorationBox = { inner ->
                                    if (inChatSearchQuery.isEmpty()) {
                                        Text(
                                            text = "Search messages in ${conversation.displayName}...",
                                            style = TextStyle(
                                                fontFamily = PlusJakartaSansFont,
                                                fontSize = 13.5.sp,
                                                color = ArenaTextMuted
                                            )
                                        )
                                    }
                                    inner()
                                }
                            )
                            IconButton(
                                onClick = { onToggleSearchActive(false) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close Search",
                                    tint = ArenaTextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // ====================================================================
        // 2. WHATSAPP MESSAGE LIST STREAM + AUTOMATIC ARENA AI INJECTION + FAB
        // ====================================================================
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("whatsapp_chat_messages_list"),
                contentPadding = PaddingValues(
                    start = 10.dp,
                    end = 10.dp,
                    top = 10.dp,
                    bottom = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Date Pill + WhatsApp End-to-End Encryption Notice
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            color = Color(0xFFD9E6DF),
                            shape = RoundedCornerShape(8.dp),
                            shadowElevation = 0.5.dp
                        ) {
                            Text(
                                text = "TODAY",
                                style = TextStyle(
                                    fontFamily = PlusJakartaSansFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = ArenaEmeraldDark
                                ),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            color = Color(0xFFFFF5C4),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .widthIn(max = 360.dp)
                                .border(0.8.dp, ArenaGoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = Color(0xFF5C4D1B),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Messages and calls are end-to-end encrypted. No one outside of this chat, not even Arena Chat, can read or listen to them.",
                                    style = TextStyle(
                                        fontFamily = PlusJakartaSansFont,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp,
                                        color = Color(0xFF5C4D1B)
                                    ),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                items(displayedMessages, key = { it.messageId }) { message ->
                    val isActionMenuOpen = selectedMessageForActions == message.messageId
                    WhatsAppMessageBubbleItem(
                        message = message,
                        isGroupChat = activeConversation.category == "group" || activeConversation.category == "community",
                        isActionMenuOpen = isActionMenuOpen,
                        onBubbleClick = {
                            selectedMessageForActions = if (isActionMenuOpen) null else message.messageId
                        },
                        onReplyClick = {
                            selectedMessageForActions = null
                            onSetReplyMessage(message)
                        },
                        onReactEmoji = { emoji ->
                            selectedMessageForActions = null
                            onToggleReaction(message.messageId, emoji)
                        },
                        onToggleStar = {
                            selectedMessageForActions = null
                            onToggleStarMessage(message.messageId)
                        },
                        onDeleteClick = {
                            selectedMessageForActions = null
                            onDeleteMessage(message.messageId)
                        },
                        onVotePollOption = { optionId ->
                            onVotePoll(message.messageId, optionId)
                        }
                    )
                }

                // Automatic Arena AI In-Stream Contextual Analysis & Suggestions Card
                item(key = "arena_ai_contextual_stream_item") {
                    ArenaAiContextualStreamCard(
                        insight = liveConversationInsight,
                        onSuggestionClick = { suggestion ->
                            localAppendedMessages = localAppendedMessages + ChatMessage(
                                messageId = "local_${System.currentTimeMillis()}",
                                conversationId = activeConversation.conversationId,
                                senderName = "You",
                                isFromMe = true,
                                text = suggestion,
                                timeLabel = "Just now",
                                deliveryStatus = "sent"
                            )
                            onSendMessage(suggestion)
                        },
                        onOpenFullArenaAi = {
                            if (onOpenArenaAi != null) {
                                onOpenArenaAi(liveConversationInsight.detectedMode, "")
                            } else {
                                showLocalArenaAiStudio = true
                            }
                        }
                    )
                }
            }
        }

        // ====================================================================
        // 3. EXPANDABLE WHATSAPP ATTACHMENT SHEET (Paperclip Menu)
        // ====================================================================
        AnimatedVisibility(
            visible = showAttachmentSheet,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp)
                    .shadow(6.dp, RoundedCornerShape(20.dp))
                    .border(1.dp, ArenaMintBorder, RoundedCornerShape(20.dp))
                    .testTag("whatsapp_attachment_sheet"),
                color = Color.White,
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        WhatsAppAttachmentOption(
                            icon = Icons.Default.Description,
                            label = "Document",
                            bgColor = Color(0xFF7F66FF),
                            onClick = {
                                showAttachmentSheet = false
                                onSendDocument(
                                    "Arena_Architecture_Report.pdf",
                                    "1.8 MB • PDF Document"
                                )
                            }
                        )
                        WhatsAppAttachmentOption(
                            icon = Icons.Default.CameraAlt,
                            label = "Camera",
                            bgColor = Color(0xFFFF2E74),
                            onClick = {
                                showAttachmentSheet = false
                                onSendImageAttachment(null, "HD Camera Snapshot 📸")
                            }
                        )
                        WhatsAppAttachmentOption(
                            icon = Icons.Default.Image,
                            label = "Gallery",
                            bgColor = Color(0xFFC861FA),
                            onClick = {
                                showAttachmentSheet = false
                                runCatching {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }.onFailure {
                                    onSendImageAttachment(null, "Shared Gallery Photo 🖼️")
                                }
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        WhatsAppAttachmentOption(
                            icon = Icons.Default.AudioFile,
                            label = "Audio",
                            bgColor = Color(0xFFFA6533),
                            onClick = {
                                showAttachmentSheet = false
                                onSendVoiceNote(15)
                            }
                        )
                        WhatsAppAttachmentOption(
                            icon = Icons.Default.LocationOn,
                            label = "Location",
                            bgColor = Color(0xFF1FA855),
                            onClick = {
                                showAttachmentSheet = false
                                onSendLocation(
                                    "Arena Innovation Hub",
                                    "Live Encrypted GPS • Updated just now"
                                )
                            }
                        )
                        WhatsAppAttachmentOption(
                            icon = Icons.Default.Poll,
                            label = "Poll",
                            bgColor = Color(0xFFFFBC38),
                            onClick = {
                                showAttachmentSheet = false
                                showCreatePollDialog = true
                            }
                        )
                    }
                }
            }
        }

        // ====================================================================
        // 4. EXPANDABLE EMOJI PICKER BAR
        // ====================================================================
        AnimatedVisibility(
            visible = showEmojiPicker,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            val emojis = listOf(
                "😀", "😂", "❤️", "🔥", "👍", "🙏", "🎉", "✨",
                "😎", "🤝", "🚀", "💯", "😍", "🤔", "👏", "✅"
            )
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 2.dp),
                color = Color.White,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 2.dp
            ) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(emojis) { emoji ->
                        Text(
                            text = emoji,
                            fontSize = 22.sp,
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable { messageInput += emoji }
                                .padding(4.dp)
                        )
                    }
                }
            }
        }

        // ====================================================================
        // 5. QUOTED REPLY PREVIEW BAR (Slides up when replying to a message)
        // ====================================================================
        AnimatedVisibility(visible = replyingToMessage != null) {
            replyingToMessage?.let { replyMsg ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    color = Color.White,
                    shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(ArenaMintSoft)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(34.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(ArenaPrimaryEmerald)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = replyMsg.senderName,
                                style = TextStyle(
                                    fontFamily = PlusJakartaSansFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = ArenaPrimaryEmerald
                                )
                            )
                            Text(
                                text = replyMsg.text,
                                style = TextStyle(
                                    fontFamily = PlusJakartaSansFont,
                                    fontSize = 12.sp,
                                    color = ArenaTextSecondary
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        IconButton(
                            onClick = { onSetReplyMessage(null) },
                            modifier = Modifier.size(26.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cancel Reply",
                                tint = ArenaTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // ====================================================================
        // 6. WHATSAPP BOTTOM COMPOSER BAR (Input Pill + Mic / Send Button)
        // ====================================================================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 6.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isRecordingVoiceNote) {
                // Live Voice Note Recording Bar
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    color = Color.White,
                    shape = RoundedCornerShape(24.dp),
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(ArenaError)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            val mins = recordingSeconds / 60
                            val secs = recordingSeconds % 60
                            Text(
                                text = String.format(Locale.US, "Recording %d:%02d", mins, secs),
                                style = TextStyle(
                                    fontFamily = PlusJakartaSansFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = ArenaError
                                )
                            )
                        }

                        Text(
                            text = "Cancel",
                            style = TextStyle(
                                fontFamily = PlusJakartaSansFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = ArenaTextSecondary
                            ),
                            modifier = Modifier
                                .clickable { isRecordingVoiceNote = false }
                                .padding(6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Send recorded voice note button
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(ArenaPrimaryEmerald)
                        .clickable {
                            val dur = recordingSeconds.coerceAtLeast(2)
                            isRecordingVoiceNote = false
                            onSendVoiceNote(dur)
                        }
                        .testTag("send_voice_note_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send Voice Note",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            } else {
                // Standard WhatsApp Pill Composer
                Surface(
                    modifier = Modifier.weight(1f),
                    color = Color.White,
                    shape = RoundedCornerShape(26.dp),
                    shadowElevation = 1.5.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 6.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                showEmojiPicker = !showEmojiPicker
                                if (showEmojiPicker) showAttachmentSheet = false
                            },
                            modifier = Modifier
                                .size(38.dp)
                                .testTag("chat_emoji_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEmotions,
                                contentDescription = "Emoji",
                                tint = if (showEmojiPicker) ArenaPrimaryEmerald else ArenaTextSecondary,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp, vertical = 8.dp)
                        ) {
                            if (messageInput.isEmpty()) {
                                Text(
                                    text = "Message",
                                    style = TextStyle(
                                        fontFamily = PlusJakartaSansFont,
                                        fontSize = 15.sp,
                                        color = ArenaTextMuted
                                    )
                                )
                            }
                            BasicTextField(
                                value = messageInput,
                                onValueChange = { messageInput = it },
                                textStyle = TextStyle(
                                    fontFamily = PlusJakartaSansFont,
                                    fontSize = 15.sp,
                                    color = ArenaCharcoalText
                                ),
                                cursorBrush = SolidColor(ArenaPrimaryEmerald),
                                maxLines = 4,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("quick_reply_input")
                            )
                        }

                        IconButton(
                            onClick = {
                                showAttachmentSheet = !showAttachmentSheet
                                if (showAttachmentSheet) showEmojiPicker = false
                            },
                            modifier = Modifier
                                .size(38.dp)
                                .testTag("chat_attach_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AttachFile,
                                contentDescription = "Attach",
                                tint = if (showAttachmentSheet) ArenaPrimaryEmerald else ArenaTextSecondary,
                                modifier = Modifier.size(21.dp)
                            )
                        }

                        if (messageInput.isEmpty()) {
                            IconButton(
                                onClick = {
                                    runCatching {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    }.onFailure {
                                        onSendImageAttachment(null, "Quick Camera Photo 📸")
                                    }
                                },
                                modifier = Modifier
                                    .size(38.dp)
                                    .testTag("chat_camera_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Camera",
                                    tint = ArenaTextSecondary,
                                    modifier = Modifier.size(21.dp)
                                )
                            }

                            IconButton(
                                onClick = { isRecordingVoiceNote = true },
                                modifier = Modifier
                                    .size(38.dp)
                                    .testTag("chat_voice_record_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Record Voice Note",
                                    tint = ArenaPrimaryEmerald,
                                    modifier = Modifier.size(21.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // WhatsApp Emerald Green Circular Send Button
                val hasText = messageInput.trim().isNotEmpty()
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .shadow(3.dp, CircleShape)
                        .clip(CircleShape)
                        .background(ArenaPrimaryEmerald)
                        .clickable {
                            val trimmed = messageInput.trim()
                            if (trimmed.isNotEmpty()) {
                                localAppendedMessages = localAppendedMessages + ChatMessage(
                                    messageId = "local_${System.currentTimeMillis()}",
                                    conversationId = activeConversation.conversationId,
                                    senderName = "You",
                                    isFromMe = true,
                                    text = trimmed,
                                    timeLabel = "Just now",
                                    deliveryStatus = "sent"
                                )
                                onSendMessage(trimmed)
                                messageInput = ""
                                showEmojiPicker = false
                                showAttachmentSheet = false
                            } else {
                                isRecordingVoiceNote = true
                            }
                        }
                        .testTag("send_quick_message_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = if (hasText) "Send Message" else "Send Message",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }

    // Create Poll Modal
    if (showCreatePollDialog) {
        WhatsAppCreatePollDialog(
            onDismiss = { showCreatePollDialog = false },
            onCreatePoll = { question, options ->
                showCreatePollDialog = false
                onSendPoll(question, options)
            }
        )
    }

    // Standalone Dedicated Arena AI Studio when triggered via the ChatScreen FAB
    if (showLocalArenaAiStudio) {
        ArenaAiAssistantScreen(
            messages = localArenaAiMessages,
            activeMode = liveConversationInsight.detectedMode,
            isGenerating = localArenaAiGenerating,
            conversations = listOf(activeConversation),
            initialDraftPrompt = localArenaAiDraft,
            onSendPrompt = { prompt, _ ->
                val clean = prompt.trim()
                if (clean.isNotEmpty()) {
                    val autoMode = ArenaAiService.detectAutomaticIntent(clean)
                    val userMsg = ArenaAiMessage(
                        id = "ai_u_${System.currentTimeMillis()}",
                        isUser = true,
                        text = clean,
                        timeLabel = "Just now",
                        mode = autoMode
                    )
                    val respId = "ai_r_${System.currentTimeMillis() + 1}"
                    val placeholder = ArenaAiMessage(
                        id = respId,
                        isUser = false,
                        text = "Synthesizing via ${autoMode.label}...",
                        timeLabel = "Just now",
                        mode = autoMode,
                        isStreaming = true
                    )
                    localArenaAiGenerating = true
                    localArenaAiMessages = localArenaAiMessages + userMsg + placeholder
                    coroutineScope.launch {
                        try {
                            if (autoMode == ArenaAiMode.IMAGE_STUDIO) {
                                val bmp = runCatching { ArenaAiService.generateArenaAiImage(clean) }.getOrNull()
                                val text = if (bmp != null) {
                                    "Generated 8K visual for: \"$clean\""
                                } else {
                                    ArenaAiService.buildSmartOfflineFallback(clean, autoMode)
                                }
                                localArenaAiMessages = localArenaAiMessages.map {
                                    if (it.id == respId) {
                                        it.copy(
                                            text = text,
                                            generatedBitmap = bmp,
                                            isStreaming = false,
                                            sourceHighlights = ArenaAiService.extractSourceBadges(autoMode, clean)
                                        )
                                    } else it
                                }
                            } else {
                                var latest = ""
                                runCatching {
                                    ArenaAiService.streamArenaAiResponse(clean, autoMode).collect { chunk ->
                                        latest = chunk
                                        localArenaAiMessages = localArenaAiMessages.map {
                                            if (it.id == respId) {
                                                it.copy(
                                                    text = chunk,
                                                    isStreaming = true,
                                                    sourceHighlights = ArenaAiService.extractSourceBadges(autoMode, clean)
                                                )
                                            } else it
                                        }
                                    }
                                }
                                val finalReply = latest.ifBlank {
                                    ArenaAiService.buildSmartOfflineFallback(clean, autoMode)
                                }
                                localArenaAiMessages = localArenaAiMessages.map {
                                    if (it.id == respId) {
                                        it.copy(
                                            text = finalReply,
                                            isStreaming = false,
                                            sourceHighlights = ArenaAiService.extractSourceBadges(autoMode, clean)
                                        )
                                    } else it
                                }
                            }
                        } finally {
                            localArenaAiGenerating = false
                        }
                    }
                }
            },
            onClearHistory = {
                localArenaAiMessages = localArenaAiMessages.take(1)
            },
            onShareToConversation = { _, aiMsg ->
                localAppendedMessages = localAppendedMessages + ChatMessage(
                    messageId = "local_ai_${System.currentTimeMillis()}",
                    conversationId = activeConversation.conversationId,
                    senderName = "You (via ARENA AI)",
                    isFromMe = true,
                    text = aiMsg.text,
                    timeLabel = "Just now",
                    deliveryStatus = "sent"
                )
                onSendMessage(aiMsg.text)
                showLocalArenaAiStudio = false
            },
            onClose = { showLocalArenaAiStudio = false }
        )
    }
}

@Composable
fun ArenaAiContextualStreamCard(
    insight: ArenaConversationAiInsight,
    onSuggestionClick: (String) -> Unit,
    onOpenFullArenaAi: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 6.dp)
            .animateContentSize()
            .shadow(2.dp, RoundedCornerShape(16.dp))
            .border(1.dp, ArenaGoldPrimary.copy(alpha = 0.55f), RoundedCornerShape(16.dp))
            .testTag("arena_ai_contextual_stream_card"),
        color = Color(0xFFFAFDFB),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ArenaAiRingLogo(size = 24.dp)
                    Spacer(modifier = Modifier.width(7.dp))
                    Text(
                        text = "ARENA AI • IN-STREAM INSIGHT",
                        style = TextStyle(
                            fontFamily = PlusJakartaSansFont,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp,
                            letterSpacing = 0.4.sp,
                            color = ArenaPrimaryEmerald
                        )
                    )
                }

                Surface(
                    color = ArenaMintSoft,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onOpenFullArenaAi)
                ) {
                    Text(
                        text = insight.engineBadge,
                        style = TextStyle(
                            fontFamily = PlusJakartaSansFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.5.sp,
                            color = ArenaEmeraldDark
                        ),
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = insight.contextualAnswer,
                style = TextStyle(
                    fontFamily = PlusJakartaSansFont,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.5.sp,
                    lineHeight = 18.sp,
                    color = ArenaCharcoalText
                ),
                modifier = Modifier.testTag("arena_ai_contextual_answer_text")
            )

            if (insight.smartSuggestions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(end = 4.dp)
                ) {
                    items(insight.smartSuggestions.size) { index ->
                        val suggestion = insight.smartSuggestions[index]
                        Surface(
                            color = ArenaMintSoft,
                            shape = RoundedCornerShape(18.dp),
                            modifier = Modifier
                                .border(0.8.dp, ArenaMintBorder, RoundedCornerShape(18.dp))
                                .clip(RoundedCornerShape(18.dp))
                                .clickable { onSuggestionClick(suggestion) }
                                .testTag("arena_ai_smart_suggestion_$index")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = ArenaPrimaryEmerald,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = suggestion,
                                    style = TextStyle(
                                        fontFamily = PlusJakartaSansFont,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.5.sp,
                                        color = ArenaEmeraldDark
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

@Composable
fun WhatsAppMessageBubbleItem(
    message: ChatMessage,
    isGroupChat: Boolean = false,
    isActionMenuOpen: Boolean = false,
    onBubbleClick: () -> Unit = {},
    onReplyClick: () -> Unit = {},
    onReactEmoji: (String) -> Unit = {},
    onToggleStar: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onVotePollOption: (String) -> Unit = {}
) {
    val isFromMe = message.isFromMe
    val bubbleColor = if (isFromMe) WhatsAppOutgoingBubbleGreen else Color.White
    val bubbleShape = if (isFromMe) {
        RoundedCornerShape(topStart = 12.dp, topEnd = 2.dp, bottomStart = 12.dp, bottomEnd = 12.dp)
    } else {
        RoundedCornerShape(topStart = 2.dp, topEnd = 12.dp, bottomStart = 12.dp, bottomEnd = 12.dp)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 1.dp),
        horizontalAlignment = if (isFromMe) Alignment.End else Alignment.Start
    ) {
        // Floating WhatsApp Emoji Reaction + Action Bar when message is tapped
        AnimatedVisibility(visible = isActionMenuOpen) {
            Surface(
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .shadow(4.dp, RoundedCornerShape(24.dp))
                    .border(1.dp, ArenaMintBorder, RoundedCornerShape(24.dp)),
                color = Color.White,
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("👍", "❤️", "😂", "😮", "😢", "🙏", "🔥").forEach { emoji ->
                        Text(
                            text = emoji,
                            fontSize = 18.sp,
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable { onReactEmoji(emoji) }
                                .padding(2.dp)
                        )
                    }
                    IconButton(
                        onClick = onReplyClick,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Reply,
                            contentDescription = "Reply",
                            tint = ArenaPrimaryEmerald,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = onToggleStar,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Star",
                            tint = if (message.isStarred) ArenaGoldPrimary else ArenaTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete",
                            tint = ArenaError,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Message Bubble Surface
        Box {
            Surface(
                modifier = Modifier
                    .widthIn(min = 90.dp, max = 310.dp)
                    .shadow(0.8.dp, bubbleShape)
                    .clip(bubbleShape)
                    .clickable(onClick = onBubbleClick)
                    .testTag("chat_message_bubble_${message.messageId}"),
                color = bubbleColor,
                shape = bubbleShape
            ) {
                Column(
                    modifier = Modifier.padding(
                        start = 10.dp,
                        end = 10.dp,
                        top = 6.dp,
                        bottom = 6.dp
                    )
                ) {
                    // Group Sender Name or Automatic Arena AI Badge
                    if (!isFromMe && (isGroupChat || !message.aiEngineBadge.isNullOrBlank() || message.senderName.contains("ARENA AI", ignoreCase = true))) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (!message.aiEngineBadge.isNullOrBlank() || message.senderName.contains("ARENA AI", ignoreCase = true)) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = ArenaPrimaryEmerald,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text(
                                text = message.senderName,
                                style = TextStyle(
                                    fontFamily = PlusJakartaSansFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = ArenaPrimaryEmerald
                                )
                            )
                            message.aiEngineBadge?.let { badge ->
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = ArenaMintSoft,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = badge,
                                        style = TextStyle(
                                            fontFamily = PlusJakartaSansFont,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp,
                                            color = ArenaEmeraldDark
                                        ),
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                    }

                    // Quoted Reply Block inside bubble
                    if (!message.replyToPreview.isNullOrBlank()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 5.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.05f))
                                .padding(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(3.dp)
                                    .height(28.dp)
                                    .background(ArenaPrimaryEmerald)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = message.replyToSenderName ?: "Reply",
                                    style = TextStyle(
                                        fontFamily = PlusJakartaSansFont,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = ArenaPrimaryEmerald
                                    )
                                )
                                Text(
                                    text = message.replyToPreview,
                                    style = TextStyle(
                                        fontFamily = PlusJakartaSansFont,
                                        fontSize = 11.sp,
                                        color = ArenaTextSecondary
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    // Rich Message Content by Type
                    when (message.messageType) {
                        ChatMessageType.VOICE_NOTE -> {
                            WhatsAppVoiceNoteContent(
                                durationSeconds = message.voiceDurationSeconds.coerceAtLeast(5)
                            )
                        }
                        ChatMessageType.DOCUMENT -> {
                            WhatsAppDocumentContent(
                                title = message.documentTitle ?: message.text,
                                meta = message.documentMeta ?: "PDF • Encrypted"
                            )
                        }
                        ChatMessageType.IMAGE -> {
                            WhatsAppImageContent(
                                mediaUri = message.mediaUri,
                                generatedBitmap = message.generatedBitmap,
                                caption = message.text
                            )
                        }
                        ChatMessageType.LOCATION -> {
                            WhatsAppLocationContent(
                                title = message.locationTitle ?: message.text,
                                subtitle = message.locationSubtitle ?: "Live GPS Pin"
                            )
                        }
                        ChatMessageType.POLL -> {
                            WhatsAppPollContent(
                                question = message.pollQuestion ?: message.text,
                                options = message.pollOptions,
                                onVoteOption = onVotePollOption
                            )
                        }
                        else -> {
                            Text(
                                text = message.text,
                                style = TextStyle(
                                    fontFamily = PlusJakartaSansFont,
                                    fontSize = 14.5.sp,
                                    lineHeight = 19.sp,
                                    color = ArenaCharcoalText
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // Bottom-Right Timestamp + Star + Read Receipt Ticks
                    Row(
                        modifier = Modifier.align(Alignment.End),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        if (message.isStarred) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Starred",
                                tint = ArenaGoldPrimary,
                                modifier = Modifier.size(11.dp)
                            )
                        }
                        Text(
                            text = message.timeLabel.ifBlank { "Now" },
                            style = TextStyle(
                                fontFamily = PlusJakartaSansFont,
                                fontWeight = FontWeight.Medium,
                                fontSize = 10.5.sp,
                                color = Color(0xFF667781)
                            ),
                            modifier = Modifier.testTag("chat_message_timestamp_${message.messageId}")
                        )
                        if (isFromMe) {
                            val tickIcon = if (message.deliveryStatus == "sent") {
                                Icons.Default.Done
                            } else {
                                Icons.Default.DoneAll
                            }
                            val tickTint = if (message.deliveryStatus == "read") {
                                WhatsAppReadTickBlueEmerald
                            } else {
                                Color(0xFF667781)
                            }
                            Icon(
                                imageVector = tickIcon,
                                contentDescription = message.deliveryStatus,
                                tint = tickTint,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }
            }

            // Reaction Emoji Badge at Bottom of Bubble
            if (!message.reactionEmoji.isNullOrBlank()) {
                Surface(
                    modifier = Modifier
                        .align(if (isFromMe) Alignment.BottomStart else Alignment.BottomEnd)
                        .padding(top = 4.dp)
                        .shadow(2.dp, CircleShape)
                        .border(1.dp, Color.White, CircleShape),
                    color = Color.White,
                    shape = CircleShape
                ) {
                    Text(
                        text = message.reactionEmoji,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun WhatsAppVoiceNoteContent(durationSeconds: Int) {
    var isPlaying by remember { mutableStateOf(false) }
    var elapsed by remember { mutableIntStateOf(0) }
    var speedLabel by remember { mutableStateOf("1x") }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (isPlaying && elapsed < durationSeconds) {
                delay(1000)
                elapsed += 1
            }
            if (elapsed >= durationSeconds) {
                isPlaying = false
                elapsed = 0
            }
        }
    }

    val bars = listOf(0.35f, 0.7f, 0.5f, 0.9f, 0.4f, 0.8f, 1f, 0.6f, 0.75f, 0.45f, 0.85f, 0.5f, 0.65f, 0.4f)
    val progressRatio = if (durationSeconds > 0) elapsed.toFloat() / durationSeconds.toFloat() else 0f

    Row(
        modifier = Modifier
            .width(235.dp)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(ArenaPrimaryEmerald)
                .clickable { isPlaying = !isPlaying },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Pause Voice Note" else "Play Voice Note",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(22.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                bars.forEachIndexed { index, heightFrac ->
                    val barRatio = index.toFloat() / bars.size.toFloat()
                    val active = isPlaying && barRatio <= progressRatio
                    Box(
                        modifier = Modifier
                            .width(3.5.dp)
                            .height((20.dp * heightFrac).coerceAtLeast(5.dp))
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                if (active) ArenaPrimaryEmerald else ArenaPrimaryEmerald.copy(alpha = 0.35f)
                            )
                    )
                }
            }
            val displaySec = if (isPlaying) elapsed else durationSeconds
            Text(
                text = String.format(Locale.US, "%d:%02d", displaySec / 60, displaySec % 60),
                style = TextStyle(
                    fontFamily = PlusJakartaSansFont,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.5.sp,
                    color = ArenaTextSecondary
                )
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        Surface(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .clickable {
                    speedLabel = when (speedLabel) {
                        "1x" -> "1.5x"
                        "1.5x" -> "2x"
                        else -> "1x"
                    }
                },
            color = ArenaEmeraldDark.copy(alpha = 0.12f),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                text = speedLabel,
                style = TextStyle(
                    fontFamily = PlusJakartaSansFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.5.sp,
                    color = ArenaEmeraldDark
                ),
                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
            )
        }
    }
}

@Composable
private fun WhatsAppDocumentContent(title: String, meta: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color.Black.copy(alpha = 0.05f))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(ArenaPrimaryEmerald),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Description,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = TextStyle(
                    fontFamily = PlusJakartaSansFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = ArenaCharcoalText
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = meta,
                style = TextStyle(
                    fontFamily = PlusJakartaSansFont,
                    fontSize = 11.sp,
                    color = ArenaTextSecondary
                )
            )
        }
    }
}

@Composable
private fun WhatsAppImageContent(
    mediaUri: String?,
    generatedBitmap: android.graphics.Bitmap? = null,
    caption: String
) {
    val context = LocalContext.current
    val bitmap = remember(mediaUri, generatedBitmap) {
        if (generatedBitmap != null) {
            generatedBitmap.asImageBitmap()
        } else {
            mediaUri?.let { uriStr ->
                runCatching {
                    context.contentResolver.openInputStream(Uri.parse(uriStr))?.use { stream ->
                        BitmapFactory.decodeStream(stream)?.asImageBitmap()
                    }
                }.getOrNull()
            }
        }
    }

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(ArenaPrimaryEmerald, ArenaEmeraldDark)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap,
                    contentDescription = caption,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = null,
                        tint = ArenaGoldLight,
                        modifier = Modifier.size(38.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "HD Encrypted Media",
                        style = TextStyle(
                            fontFamily = PlusJakartaSansFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    )
                }
            }
        }
        if (caption.isNotBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = caption,
                style = TextStyle(
                    fontFamily = PlusJakartaSansFont,
                    fontSize = 13.5.sp,
                    color = ArenaCharcoalText
                )
            )
        }
    }
}

@Composable
private fun WhatsAppLocationContent(title: String, subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(ArenaMintSoft)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(ArenaPrimaryEmerald),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                style = TextStyle(
                    fontFamily = PlusJakartaSansFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp,
                    color = ArenaCharcoalText
                )
            )
            Text(
                text = subtitle,
                style = TextStyle(
                    fontFamily = PlusJakartaSansFont,
                    fontSize = 11.sp,
                    color = ArenaPrimaryEmerald
                )
            )
        }
    }
}

@Composable
private fun WhatsAppPollContent(
    question: String,
    options: List<com.example.data.model.ChatPollOption>,
    onVoteOption: (String) -> Unit
) {
    val totalVotes = options.sumOf { it.votes }.coerceAtLeast(1)
    Column(modifier = Modifier.width(245.dp)) {
        Text(
            text = question,
            style = TextStyle(
                fontFamily = PlusJakartaSansFont,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                color = ArenaCharcoalText
            )
        )
        Text(
            text = "Select one or more",
            style = TextStyle(
                fontFamily = PlusJakartaSansFont,
                fontSize = 11.sp,
                color = ArenaTextSecondary
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        options.forEach { opt ->
            val ratio = (opt.votes.toFloat() / totalVotes.toFloat()).coerceIn(0.05f, 1f)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onVoteOption(opt.optionId) }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = if (opt.isVotedByMe) ArenaPrimaryEmerald else ArenaTextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = opt.text,
                            style = TextStyle(
                                fontFamily = PlusJakartaSansFont,
                                fontWeight = if (opt.isVotedByMe) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.5.sp,
                                color = ArenaCharcoalText
                            )
                        )
                    }
                    Text(
                        text = "${opt.votes}",
                        style = TextStyle(
                            fontFamily = PlusJakartaSansFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp,
                            color = ArenaPrimaryEmerald
                        )
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color.Black.copy(alpha = 0.08f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(ratio)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(ArenaPrimaryEmerald)
                    )
                }
            }
        }
    }
}

@Composable
private fun WhatsAppAttachmentOption(
    icon: ImageVector,
    label: String,
    bgColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = TextStyle(
                fontFamily = PlusJakartaSansFont,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = ArenaCharcoalText
            )
        )
    }
}

@Composable
private fun WhatsAppCreatePollDialog(
    onDismiss: () -> Unit,
    onCreatePoll: (String, List<String>) -> Unit
) {
    var question by remember { mutableStateOf("") }
    var option1 by remember { mutableStateOf("") }
    var option2 by remember { mutableStateOf("") }
    var option3 by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ArenaSoftCream,
        title = {
            Text(
                text = "Create Poll",
                style = TextStyle(
                    fontFamily = PlusJakartaSansFont,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = ArenaCharcoalText
                )
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = question,
                    onValueChange = { question = it },
                    label = { Text("Ask a question") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = option1,
                    onValueChange = { option1 = it },
                    label = { Text("Option 1") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = option2,
                    onValueChange = { option2 = it },
                    label = { Text("Option 2") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = option3,
                    onValueChange = { option3 = it },
                    label = { Text("Option 3 (optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onCreatePoll(question, listOf(option1, option2, option3))
                }
            ) {
                Text(
                    text = "Send Poll",
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

// ============================================================================
// WHATSAPP ENCRYPTED VOICE & VIDEO CALL FULL-SCREEN OVERLAY
// ============================================================================

@Composable
fun WhatsAppActiveCallOverlay(
    callSession: ActiveCallSession,
    onToggleMute: () -> Unit,
    onToggleSpeaker: () -> Unit,
    onToggleVideo: () -> Unit,
    onEndCall: (String) -> Unit
) {
    var elapsedSeconds by remember { mutableIntStateOf(0) }

    LaunchedEffect(callSession.callId) {
        elapsedSeconds = 0
        while (true) {
            delay(1000)
            elapsedSeconds += 1
        }
    }

    val mins = elapsedSeconds / 60
    val secs = elapsedSeconds % 60
    val formattedTimer = if (elapsedSeconds < 2) {
        "Ringing..."
    } else {
        String.format(Locale.US, "%02d:%02d", mins, secs)
    }

    BackHandler {
        onEndCall(formattedTimer)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0A2E23), Color(0xFF051812))
                )
            )
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("whatsapp_active_call_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Encrypted Call Header
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = ArenaGoldLight,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "End-to-end encrypted",
                        style = TextStyle(
                            fontFamily = PlusJakartaSansFont,
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = callSession.contactName,
                    style = TextStyle(
                        fontFamily = PlusJakartaSansFont,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 26.sp,
                        color = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = formattedTimer,
                    style = TextStyle(
                        fontFamily = PlusJakartaSansFont,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = ArenaGoldLight
                    )
                )
            }

            // Center Avatar / Video Preview
            Box(
                modifier = Modifier.size(160.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape)
                        .background(ArenaPrimaryEmerald.copy(alpha = 0.2f))
                )
                Box(
                    modifier = Modifier
                        .size(126.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(ArenaPrimaryEmerald, ArenaEmeraldDark)
                            )
                        )
                        .border(2.5.dp, ArenaGoldPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = callSession.contactName
                            .split(" ")
                            .mapNotNull { it.firstOrNull()?.uppercase() }
                            .take(2)
                            .joinToString(""),
                        style = TextStyle(
                            fontFamily = PlusJakartaSansFont,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 40.sp,
                            color = Color.White
                        )
                    )
                }
            }

            // Bottom WhatsApp Call Controls Dock
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(28.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Speaker
                    IconButton(
                        onClick = onToggleSpeaker,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(
                                if (callSession.isSpeakerOn) Color.White.copy(alpha = 0.25f)
                                else Color.Transparent
                            )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Speaker",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Video Toggle
                    IconButton(
                        onClick = onToggleVideo,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(
                                if (callSession.isCameraOn) Color.White.copy(alpha = 0.25f)
                                else Color.Transparent
                            )
                    ) {
                        Icon(
                            imageVector = if (callSession.isCameraOn) Icons.Default.Videocam else Icons.Default.VideocamOff,
                            contentDescription = "Toggle Video",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Mute Microphone
                    IconButton(
                        onClick = onToggleMute,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(
                                if (callSession.isMuted) Color.White.copy(alpha = 0.25f)
                                else Color.Transparent
                            )
                    ) {
                        Icon(
                            imageVector = if (callSession.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Mute Mic",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Red End Call Button
                    IconButton(
                        onClick = { onEndCall("$formattedTimer • HD E2EE") },
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(ArenaError)
                            .testTag("end_active_call_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CallEnd,
                            contentDescription = "End Call",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
        }
    }
}
