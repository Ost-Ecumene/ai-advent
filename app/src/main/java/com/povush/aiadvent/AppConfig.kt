package com.povush.aiadvent

object AppConfig {
    const val GPT_OSS_20B_FREE = "openai/gpt-oss-20b:free"
    const val OPEN_ROUTER_BASE_URL = "https://openrouter.ai/api/v1/"
    const val BASE_TEMPERATURE = 0.8

    val basicSystemPrompt = """
        Определяй, просит ли пользователь сгенерировать квест.
        Если да, ответь строго в формате JSON с полями "title", "description" (2-4 предложения) и "tasks" (список строк).
        Если нет – отвечай обычным текстом.
    """.trimIndent()
}