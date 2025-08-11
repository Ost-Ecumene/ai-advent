package com.povush.aiadvent.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChatChoiceDto(
    val index: Int,
    val message: ChatMessageDto?,
)