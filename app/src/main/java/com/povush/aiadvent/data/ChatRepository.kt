package com.povush.aiadvent.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val api: OpenRouterService
) {
    suspend fun completeOnce(model: String, history: List<Pair<String,String>>): String {
        val messages = history.map { (role, content) -> ChatMessageDto(role, content) }
        val res = api.chatCompletion(ChatRequestDto(model = model, messages = messages, stream = false))
        return res.choices.firstOrNull()?.message?.content ?: ""
    }

    fun streamCompletion(model: String, history: List<Pair<String,String>>): Flow<String> = flow {
        val messages = history.map { (role, content) -> ChatMessageDto(role, content) }
        val response = api.streamChatCompletion(ChatRequestDto(model = model, messages = messages, stream = true))
        val body = response.body() ?: throw IllegalStateException("Empty body")
        emitAll(SseParser.parseStream(body))
    }
}