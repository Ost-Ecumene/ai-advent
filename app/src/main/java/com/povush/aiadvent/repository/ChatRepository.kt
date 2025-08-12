package com.povush.aiadvent.repository

import com.povush.aiadvent.AppConfig
import com.povush.aiadvent.model.ChatItem
import com.povush.aiadvent.model.Role
import com.povush.aiadvent.network.OpenRouterService
import com.povush.aiadvent.network.dto.ChatMessageDto
import com.povush.aiadvent.network.dto.ChatRequestDto
import com.povush.aiadvent.network.dto.QuestDto
import com.povush.aiadvent.network.dto.toText
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val openRouterService: OpenRouterService,
    moshi: Moshi
) {
    private val questAdapter = moshi.adapter(QuestDto::class.java)
    private val chatSystemPrompt = listOf(ChatMessageDto("system", AppConfig.basicSystemPrompt))
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
                    role = chatItem.role.nameText,
                    content = chatItem.text
                )
                is ChatItem.Quest -> ChatMessageDto(
                    role = Role.User.nameText,
                    content = chatItem.quest.toText()
                )
            }
        }

        val request = ChatRequestDto(messages = messages)
        val response = openRouterService.chatCompletion(request)
        val responseContent = response.choices.firstOrNull()?.message?.content ?: throw NullPointerException("Ответ от ИИ не должен быть null!")
        val quest = runCatching { questAdapter.fromJson(responseContent) }.getOrNull()

        val answer = quest?.let {
            ChatItem.Quest(quest = it)
        } ?: run {
            ChatItem.Message(
                text = responseContent,
                role = Role.Assistant
            )
        }

        _chatHistory.update { it + answer }
    }
}