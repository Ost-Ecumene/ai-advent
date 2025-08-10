package com.povush.aiadvent.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import okio.Buffer
import okio.BufferedSource

object SseParser {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val chunkAdapter = moshi.adapter(ChatChunkDto::class.java)

    fun parseStream(body: ResponseBody): Flow<String> = flow {
        val source: BufferedSource = body.source()
        try {
            while (true) {
                val raw = source.readUtf8Line() ?: break
                val line = raw.trim()
                if (line.isEmpty() || line.startsWith(":")) continue // comments/keep-alive
                if (!line.startsWith("data:")) continue
                val payload = line.removePrefix("data:").trim()
                if (payload.isEmpty()) continue
                if (payload == "[DONE]") break
                runCatching { chunkAdapter.fromJson(payload) }
                    .onSuccess { dto ->
                        val token = dto?.choices?.firstOrNull()?.delta?.content
                        if (!token.isNullOrEmpty()) emit(token)
                    }
                    .onFailure { /* ignore malformed line, keep stream alive */ }
            }
        } finally {
            withContext(Dispatchers.IO) { body.close() }
        }
    }
}