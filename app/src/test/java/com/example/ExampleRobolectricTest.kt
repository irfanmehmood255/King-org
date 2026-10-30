package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.ArenaDashboardTab
import com.example.data.model.ChatFilterType
import com.example.data.model.ChatMessage
import com.example.data.model.FabNewActionType
import com.example.data.model.GlobalSearchCategory
import com.example.data.model.ProfileSettingsSection
import com.example.data.repository.ArenaDashboardRepository
import com.example.ui.screens.dashboard.ChatListScreen
import com.example.ui.screens.dashboard.ChatScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.util.PasswordStrengthLevel
import com.example.util.PasswordStrengthValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `read Arena Chat string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Arena Chat", appName)
    }

    @Test
    fun `validate password strength rules for registration and reset flows`() {
        val weakResult = PasswordStrengthValidator.evaluate("short")
        assertFalse(weakResult.isValidForSubmission)
        assertEquals(PasswordStrengthLevel.WEAK, weakResult.strengthLevel)
        assertNotNull(weakResult.errorMessage)

        val strongResult = PasswordStrengthValidator.evaluate("ArenaChat#2026")
        assertTrue(strongResult.isValidForSubmission)
        assertTrue(strongResult.hasMinLength)
        assertTrue(strongResult.hasNumber)
        assertTrue(strongResult.hasSpecialChar)
        assertEquals(PasswordStrengthLevel.STRONG, strongResult.strengthLevel)
        assertNull(strongResult.errorMessage)
    }

    @Test
    fun `verify Part 2_1 six navigation sections and filters structure`() {
        val navTabs = ArenaDashboardTab.entries.map { it.label }
        assertEquals(
            listOf("Chats", "Calls", "Communities", "Channels", "Discover", "Profile"),
            navTabs
        )

        val chatFilters = ChatFilterType.entries.map { it.label }
        assertEquals(
            listOf("All", "Unread", "Personal", "Groups", "Favorites", "Archived"),
            chatFilters
        )

        assertEquals(8, GlobalSearchCategory.entries.size)
        assertEquals(5, FabNewActionType.entries.size)
        assertEquals(9, ProfileSettingsSection.entries.size)

        val defaults = ArenaDashboardRepository.defaultConversations()
        assertTrue(defaults.any { it.isPinned })
        assertTrue(defaults.any { it.isMuted })
        assertTrue(defaults.any { it.isArchived })
        assertTrue(defaults.any { it.unreadCount > 0 })
        assertTrue(defaults.any { it.isTyping })
        assertTrue(defaults.any { it.deliveryStatus == "draft" })
        assertTrue(defaults.all { it.lastMessageTimeLabel.isNotBlank() && it.lastMessagePreview.isNotBlank() })

        val primarySearchCategories = listOf(
            GlobalSearchCategory.PEOPLE.label,
            GlobalSearchCategory.ARENA_IDS.label,
            GlobalSearchCategory.MESSAGES.label,
            GlobalSearchCategory.GROUPS.label
        )
        assertEquals(
            listOf("People", "Arena IDs", "Messages", "Groups"),
            primarySearchCategories
        )
    }

    @Test
    fun `verify ChatScreen renders LazyColumn with sent and received bubbles and sends message`() {
        val conversation = ArenaDashboardRepository.defaultConversations().first()
        val initialMessages = listOf(
            ChatMessage(
                messageId = "msg_received_1",
                conversationId = conversation.conversationId,
                senderName = conversation.displayName,
                isFromMe = false,
                text = "Welcome to WhatsApp-styled Arena Chat!",
                timeLabel = "10:30 AM",
                deliveryStatus = "read"
            ),
            ChatMessage(
                messageId = "msg_sent_1",
                conversationId = conversation.conversationId,
                senderName = "You",
                isFromMe = true,
                text = "Testing outgoing green bubble UI!",
                timeLabel = "10:31 AM",
                deliveryStatus = "read"
            )
        )
        var lastSentMessage: String? = null

        composeTestRule.setContent {
            MyApplicationTheme {
                ChatScreen(
                    conversation = conversation,
                    messages = initialMessages,
                    onSendMessage = { sent -> lastSentMessage = sent }
                )
            }
        }

        composeTestRule.onNodeWithTag("whatsapp_chat_messages_list").assertIsDisplayed()
        composeTestRule.onNodeWithTag("chat_message_bubble_msg_received_1").assertIsDisplayed()
        composeTestRule.onNodeWithTag("chat_message_bubble_msg_sent_1").assertIsDisplayed()
        composeTestRule.onNodeWithTag("chat_message_timestamp_msg_received_1", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithTag("chat_message_timestamp_msg_sent_1", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("10:30 AM", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("10:31 AM", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Welcome to WhatsApp-styled Arena Chat!").assertIsDisplayed()
        composeTestRule.onNodeWithText("Testing outgoing green bubble UI!").assertIsDisplayed()

        // Verify automatic in-stream Arena AI contextual card is displayed, while floating Arena AI icon is NOT inside ChatScreen
        composeTestRule.onNodeWithTag("arena_ai_contextual_stream_card").assertIsDisplayed()
        composeTestRule.onNodeWithTag("arena_ai_contextual_answer_text", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithTag("chat_screen_arena_ai_fab").assertDoesNotExist()

        composeTestRule.onNodeWithTag("quick_reply_input").performTextInput("Instant WhatsApp reply!")
        composeTestRule.onNodeWithTag("send_quick_message_button").performClick()

        assertEquals("Instant WhatsApp reply!", lastSentMessage)
        composeTestRule.onNodeWithText("Instant WhatsApp reply!").assertIsDisplayed()
    }

    @Test
    fun `verify ChatListScreen displays circular avatars, Arena AI button above FAB, and recent conversations`() {
        val conversations = ArenaDashboardRepository.defaultConversations().filter { !it.isArchived }
        var selectedFabAction: FabNewActionType? = null
        var arenaAiOpened = false

        composeTestRule.setContent {
            MyApplicationTheme {
                ChatListScreen(
                    conversations = conversations,
                    onSelectFabAction = { action -> selectedFabAction = action },
                    onOpenArenaAi = { _, _ -> arenaAiOpened = true }
                )
            }
        }

        composeTestRule.onNodeWithTag("chat_list_screen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("chats_tab_lazy_column").assertIsDisplayed()
        composeTestRule.onNodeWithTag("arena_ai_fab_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("dashboard_fab_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("search_bar_arena_ai_mode_button").assertIsDisplayed()

        // Verify circular user profile avatar and conversation row are displayed
        val firstConv = conversations.first()
        composeTestRule.onNodeWithText(firstConv.displayName).assertIsDisplayed()
        composeTestRule.onNodeWithTag("conversation_avatar_${firstConv.conversationId}").assertIsDisplayed()

        // Click the Arena AI button above the + FAB
        composeTestRule.onNodeWithTag("arena_ai_fab_button").performClick()
        assertTrue(arenaAiOpened)

        // Click the + FloatingActionButton to initiate a new chat
        composeTestRule.onNodeWithTag("dashboard_fab_button").performClick()
        composeTestRule.onNodeWithTag("fab_new_conversation_menu").assertIsDisplayed()
        composeTestRule.onNodeWithTag("fab_menu_new_message").performClick()

        assertEquals(FabNewActionType.NEW_MESSAGE, selectedFabAction)
    }

    @Test
    fun `verify ChatRoom data model, ArenaConversationAiService, and automatic Arena AI intent detection`() {
        val chatRoom = com.example.data.model.ChatRoom(
            roomId = "room_test_1",
            ownerId = "user_1",
            roomName = "ARENA AI",
            handleOrArenaId = "@arena_ai",
            lastMessageText = "Auto Neural Engine ready",
            lastMessageTimeLabel = "Now",
            isOnline = true,
            isPinned = true,
            isVerified = true,
            avatarPreset = 99
        )
        val convertedConversation = chatRoom.toConversationItem()
        assertEquals("room_test_1", convertedConversation.conversationId)
        assertEquals("ARENA AI", convertedConversation.displayName)
        assertTrue(convertedConversation.isPinned)
        assertTrue(convertedConversation.isVerified)

        // Verify ArenaConversationAiService analyzes conversation history automatically
        val sampleHistory = listOf(
            ChatMessage(
                messageId = "m1",
                conversationId = "room_test_1",
                senderName = "Elena Vance",
                isFromMe = false,
                text = "Have we verified the E2EE security benchmark for the voice call engine?"
            )
        )
        val insight = com.example.ui.screens.dashboard.ArenaConversationAiService.analyzeConversationHistory(
            convertedConversation,
            sampleHistory
        )
        assertTrue(insight.contextualAnswer.isNotBlank())
        assertEquals(3, insight.smartSuggestions.size)

        // Verify automatic intent classification across English & Roman Urdu prompts
        assertEquals(
            com.example.data.model.ArenaAiMode.IMAGE_STUDIO,
            com.example.ui.screens.dashboard.ArenaAiService.detectAutomaticIntent("Generate an 8K image of an emerald palace")
        )
        assertEquals(
            com.example.data.model.ArenaAiMode.IMAGE_STUDIO,
            com.example.ui.screens.dashboard.ArenaAiService.detectAutomaticIntent("ek luxury car ki photo banao")
        )
        assertEquals(
            com.example.data.model.ArenaAiMode.LIVE_WEB_SEARCH,
            com.example.ui.screens.dashboard.ArenaAiService.detectAutomaticIntent("What is the latest tech news today?")
        )
        assertEquals(
            com.example.data.model.ArenaAiMode.DEEP_CHAT,
            com.example.ui.screens.dashboard.ArenaAiService.detectAutomaticIntent("Write a clean Kotlin coroutine retry helper")
        )
    }
}


