package com.povush.aiadvent.service

import com.povush.aiadvent.AppConfig
import com.povush.aiadvent.network.OpenRouterService
import com.povush.aiadvent.network.dto.ChatMessageDto
import com.povush.aiadvent.network.dto.ChatRequestDto
import com.povush.aiadvent.network.dto.QuestDto
import com.squareup.moshi.Moshi
import javax.inject.Inject

class QuestGeneratorLLMService @Inject constructor(
    private val openRouterService: OpenRouterService,
    moshi: Moshi
) {
    private val questAdapter = moshi.adapter(QuestDto::class.java)
    private val basicSystemPrompt = listOf(ChatMessageDto("system", AppConfig.questGeneratorSystemPrompt))

    suspend fun createQuest(description: String): QuestDto {
        val descriptionSystemPrompt = listOf(ChatMessageDto("system", description))
        val finalPrompt = basicSystemPrompt + descriptionSystemPrompt
        val request = ChatRequestDto(messages = finalPrompt)

        val response = openRouterService.chatCompletion(request)
        val responseContent = response.choices.firstOrNull()?.message?.content ?: throw NullPointerException("Ответ от ИИ не должен быть null!")
        val quest = runCatching { questAdapter.fromJson(responseContent) }.getOrNull() ?: throw NullPointerException("Квест не должен быть null!")

        return quest
    }
}