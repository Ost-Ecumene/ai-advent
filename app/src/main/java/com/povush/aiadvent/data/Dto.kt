package com.povush.aiadvent.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChatMessageDto(
    val role: String,
    val content: String
)

@JsonClass(generateAdapter = true)
data class ChatRequestDto(
    val model: String,
    val messages: List<ChatMessageDto>,
    val stream: Boolean = false,
    @Json(name = "temperature") val temperature: Double? = null,
    @Json(name = "max_tokens") val maxTokens: Int? = null,
)

@JsonClass(generateAdapter = true)
data class ChatChoiceDto(
    val index: Int,
    val message: ChatMessageDto?,
    @Json(name = "finish_reason") val finishReason: String?
)

@JsonClass(generateAdapter = true)
data class ChatResponseDto(
    val id: String?,
    val object_: String? = null,
    val created: Long? = null,
    val model: String? = null,
    val choices: List<ChatChoiceDto> = emptyList()
)

// Streaming chunk (SSE line payload: choices[0].delta.content)
@JsonClass(generateAdapter = true)
data class ChatDeltaDto(
    val content: String? = null,
    val role: String? = null
)

@JsonClass(generateAdapter = true)
data class ChatChunkChoiceDto(
    val index: Int,
    val delta: ChatDeltaDto,
    @Json(name = "finish_reason") val finishReason: String? = null
)

@JsonClass(generateAdapter = true)
data class ChatChunkDto(
    val id: String? = null,
    val object_: String? = null,
    val created: Long? = null,
    val model: String? = null,
    val choices: List<ChatChunkChoiceDto> = emptyList()
)