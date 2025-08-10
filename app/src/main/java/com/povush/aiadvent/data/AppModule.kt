package com.povush.aiadvent.data

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton fun provideApi(): OpenRouterService = OpenRouterService.create()

    @Provides
    @Singleton fun provideRepo(api: OpenRouterService) = ChatRepository(api)
}