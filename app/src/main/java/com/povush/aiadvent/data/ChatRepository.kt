package com.povush.aiadvent.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val api: OpenRouterService
) {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val questAdapter = moshi.adapter(QuestDto::class.java)

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

    suspend fun requestQuest(model: String, prompt: String): QuestDto {
        val messages = listOf(
            ChatMessageDto("user", prompt)
        )
        val schema: Map<String, Any> = mapOf(
            "type" to "object",
            "properties" to mapOf(
                "title" to mapOf("type" to "string"),
                "description" to mapOf("type" to "string")
            ),
            "required" to listOf("title", "description"),
            "additionalProperties" to false
        )
        val responseFormat = ResponseFormatDto(
            type = "json_schema",
            jsonSchema = JsonSchemaDto(
                name = "quest",
                schema = schema
            )
        )
        val res = api.questCompletion(
            ChatRequestDto(
                model = model,
                messages = messages,
                stream = false,
                responseFormat = responseFormat
            )
        )
        val content = res.choices.firstOrNull()?.message?.content ?: "{}"
        return questAdapter.fromJson(content) ?: QuestDto("", "")
    }
}