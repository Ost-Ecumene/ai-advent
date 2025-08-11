package com.povush.aiadvent.network.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChatChoiceDto(
    val index: Int,
    val message: ChatMessageDto?,
)