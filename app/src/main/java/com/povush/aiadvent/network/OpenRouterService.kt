package com.povush.aiadvent.network

import com.povush.aiadvent.network.model.ChatRequestDto
import com.povush.aiadvent.network.model.ChatResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface OpenRouterService {
    @POST("chat/completions")
    suspend fun chatCompletion(
        @Body body: ChatRequestDto,
    ): ChatResponseDto
}