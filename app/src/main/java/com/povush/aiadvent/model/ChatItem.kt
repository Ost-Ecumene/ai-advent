package com.povush.aiadvent.model

import com.povush.aiadvent.network.dto.QuestDto

sealed class ChatItem {

    data class Message(
        val text: String,
        val role: Role
    ) : ChatItem()

    data class Quest(
        val quest: QuestDto
    ) : ChatItem()
}