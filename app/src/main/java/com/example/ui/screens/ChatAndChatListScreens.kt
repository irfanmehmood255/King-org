package com.example.ui.screens

import androidx.compose.runtime.Composable
import com.example.data.model.ChatFilterType
import com.example.data.model.ChatMessage
import com.example.data.model.ConversationItem
import com.example.data.model.FabNewActionType
import com.example.data.model.GlobalSearchCategory
import com.example.data.model.StoryItem
import com.example.data.repository.ArenaDashboardRepository
import com.example.ui.screens.dashboard.ChatListScreen as DashboardChatListScreen
import com.example.ui.screens.dashboard.ChatScreen as DashboardChatScreen
import com.example.ui.viewmodel.defaultSeededMessages

@Composable
fun ChatScreen(
    conversation: ConversationItem = ArenaDashboardRepository.defaultConversations().first(),
    messages: List<ChatMessage> = defaultSeededMessages()[conversation.conversationId].orEmpty(),
    onSendMessage: (String) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    DashboardChatScreen(
        conversation = conversation,
        messages = messages,
        onSendMessage = onSendMessage,
        onBackClick = onBackClick
    )
}

@Composable
fun ChatListScreen(
    conversations: List<ConversationItem> = ArenaDashboardRepository.defaultConversations().filter { !it.isArchived },
    selectedFilter: ChatFilterType = ChatFilterType.ALL,
    searchQuery: String = "",
    selectedSearchCategory: GlobalSearchCategory = GlobalSearchCategory.PEOPLE,
    stories: List<StoryItem> = ArenaDashboardRepository.defaultStories(),
    onOpenConversation: (ConversationItem) -> Unit = {},
    onSelectFabAction: (FabNewActionType) -> Unit = {}
) {
    DashboardChatListScreen(
        conversations = conversations,
        selectedFilter = selectedFilter,
        searchQuery = searchQuery,
        selectedSearchCategory = selectedSearchCategory,
        stories = stories,
        onOpenConversation = onOpenConversation,
        onSelectFabAction = onSelectFabAction
    )
}
