package com.povush.aiadvent.network.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChatMessageDto(
    val role: String,
    val content: String,
)