package com.povush.aiadvent.data

import com.povush.aiadvent.AppConfig
import com.povush.aiadvent.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Streaming

interface OpenRouterService {
    @POST("chat/completions")
    suspend fun chatCompletion(
        @Body body: ChatRequestDto
    ): ChatResponseDto

    @Streaming
    @POST("chat/completions")
    suspend fun streamChatCompletion(
        @Body body: ChatRequestDto
    ): Response<ResponseBody>

    companion object {
        fun create(): OpenRouterService {
            val logging = HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                else HttpLoggingInterceptor.Level.BASIC
            }
            val authInterceptor = Interceptor { chain ->
                val req = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${BuildConfig.OPENROUTER_API_KEY}")
                    .addHeader("Content-Type", "application/json")
                    .build()
                chain.proceed(req)
            }

            val okHttp = OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(logging)
                .build()

            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            return Retrofit.Builder()
                .baseUrl("https://openrouter.ai/api/v1/")
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .client(okHttp)
                .build()
                .create(OpenRouterService::class.java)
        }
    }
}