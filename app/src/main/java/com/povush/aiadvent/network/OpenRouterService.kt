package com.povush.aiadvent.network

import com.povush.aiadvent.network.dto.ChatRequestDto
import com.povush.aiadvent.network.dto.ChatResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface OpenRouterService {
    @POST("chat/completions")
    suspend fun chatCompletion(
        @Body body: ChatRequestDto,
    ): ChatResponseDto
}