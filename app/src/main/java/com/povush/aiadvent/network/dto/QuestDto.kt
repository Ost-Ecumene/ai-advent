package com.povush.aiadvent.network.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class QuestDto(
    val title: String,
    val description: String,
    val tasks: List<String> = emptyList(),
)

fun QuestDto.toText(): String {
    return buildString {
        append("# Заголовок: $title")
        append("\n")
        append("### Описание:")
        append("\n")
        append(description)
        append("### Задачи:")
        tasks.forEach { task ->
            append(task)
            append("\n")
        }
    }
}