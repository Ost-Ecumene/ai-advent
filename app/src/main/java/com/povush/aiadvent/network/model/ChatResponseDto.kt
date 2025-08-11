package com.povush.aiadvent.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChatResponseDto(
    val choices: List<ChatChoiceDto> = emptyList(),
)