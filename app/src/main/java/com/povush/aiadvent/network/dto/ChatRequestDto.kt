package com.povush.aiadvent.network.dto

import com.povush.aiadvent.AppConfig
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChatRequestDto(
    val messages: List<ChatMessageDto>,
    val model: String = AppConfig.GPT_OSS_20B_FREE,
    val temperature: Double? = AppConfig.BASE_TEMPERATURE,
)