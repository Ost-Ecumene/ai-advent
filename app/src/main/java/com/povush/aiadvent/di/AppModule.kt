package com.povush.aiadvent.di

import com.povush.aiadvent.AppConfig
import com.povush.aiadvent.BuildConfig
import com.povush.aiadvent.repository.ChatRepository
import com.povush.aiadvent.network.OpenRouterService
import com.povush.aiadvent.service.QuestGeneratorLLMService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideApi(moshi: Moshi): OpenRouterService {
        val authInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer ${BuildConfig.OPENROUTER_API_KEY}")
                .build()
            chain.proceed(request)
        }

        val okHttp = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(AppConfig.OPEN_ROUTER_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttp)
            .build()
            .create(OpenRouterService::class.java)
    }

    @Provides
    @Singleton
    fun provideQuestGeneratorLLMService(
        openRouterService: OpenRouterService,
        moshi: Moshi
    ) = QuestGeneratorLLMService(openRouterService, moshi)

    @Provides
    @Singleton
    fun provideChatRepository(
        openRouterService: OpenRouterService,
        questGeneratorLLMService: QuestGeneratorLLMService
    ) = ChatRepository(openRouterService, questGeneratorLLMService)
}