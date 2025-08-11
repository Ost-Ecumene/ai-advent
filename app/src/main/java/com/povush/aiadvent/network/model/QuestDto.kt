package com.povush.aiadvent.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class QuestDto(
    val title: String,
    val description: String,
    val tasks: List<String> = emptyList(),
)