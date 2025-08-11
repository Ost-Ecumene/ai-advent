package com.povush.aiadvent.repository

import com.povush.aiadvent.network.OpenRouterService
import com.povush.aiadvent.network.model.ChatMessageDto
import com.povush.aiadvent.network.model.ChatRequestDto
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val openRouterService: OpenRouterService
) {
    private val systemPrompt = """
        Определяй, просит ли пользователь сгенерировать квест.
        Если да, ответь строго в формате JSON с полями "title", "description" (2-4 предложения) и "tasks" (список строк).
        Если нет – отвечай обычным текстом.
    """.trimIndent()

    suspend fun completeOnce(history: List<Pair<String, String>>): String {
        val messages = listOf(ChatMessageDto("system", systemPrompt)) +
            history.map { (role, content) -> ChatMessageDto(role, content) }
        val res = openRouterService.chatCompletion(
            ChatRequestDto(messages = messages)
        )
        return res.choices.firstOrNull()?.message?.content ?: ""
    }
}