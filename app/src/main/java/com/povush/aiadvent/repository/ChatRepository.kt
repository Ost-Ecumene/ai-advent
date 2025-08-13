package com.povush.aiadvent.repository

import android.R.attr.description
import com.povush.aiadvent.AppConfig
import com.povush.aiadvent.model.ChatItem
import com.povush.aiadvent.model.Role
import com.povush.aiadvent.network.OpenRouterService
import com.povush.aiadvent.network.dto.ChatMessageDto
import com.povush.aiadvent.network.dto.ChatRequestDto
import com.povush.aiadvent.network.dto.toText
import com.povush.aiadvent.service.QuestGeneratorLLMService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val openRouterService: OpenRouterService,
    private val questGeneratorLLMService: QuestGeneratorLLMService
) {
    private val chatSystemPrompt = listOf(ChatMessageDto("system", AppConfig.companionSystemPrompt))
    private val basicHistory = listOf(
        ChatItem.Message(
            text = AppConfig.FIRST_MESSAGE,
            role = Role.Assistant
        )
    )

    private val _chatHistory = MutableStateFlow<List<ChatItem>>(basicHistory)
    val chatHistory = _chatHistory.asStateFlow()

    suspend fun sendRequest(message: String) {
        _chatHistory.update {
            it + ChatItem.Message(
                text = message,
                role = Role.User
            )
        }

        val messages = chatSystemPrompt + chatHistory.value.map { chatItem ->
            when (chatItem) {
                is ChatItem.Message -> ChatMessageDto(
                    role = chatItem.role.internalName,
                    content = chatItem.text
                )
                is ChatItem.Quest -> ChatMessageDto(
                    role = Role.User.internalName,
                    content = chatItem.quest.toText()
                )
                is ChatItem.Log -> ChatMessageDto(
                    role = chatItem.role.internalName,
                    content = chatItem.text
                )
            }
        }

        val request = ChatRequestDto(messages = messages)
        val response = openRouterService.chatCompletion(request)
        val responseContent = response.choices.firstOrNull()?.message?.content ?: throw NullPointerException("Ответ от ИИ не должен быть null!")

        val answer = if (responseContent.startsWith("Инициировать создание квеста!")) {
            val description = responseContent.removePrefix("Инициировать создание квеста!")
            val log = ChatItem.Log(
                text = description,
                role = Role.Assistant
            )
            _chatHistory.update { it + log }
            val quest = questGeneratorLLMService.createQuest(description)
            ChatItem.Quest(quest = quest)
        } else {
            ChatItem.Message(
                text = responseContent,
                role = Role.Assistant
            )
        }

        _chatHistory.update { it + answer }
    }
}