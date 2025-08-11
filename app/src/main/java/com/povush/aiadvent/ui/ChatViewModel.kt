package com.povush.aiadvent.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.povush.aiadvent.repository.ChatRepository
import com.povush.aiadvent.network.dto.QuestDto
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
) : ViewModel() {
    val chatHistory = chatRepository.chatHistory
    val input = MutableStateFlow("")
    val error = MutableStateFlow<String?>(null)
    val isStreaming = MutableStateFlow(false)

    fun onInputChange(value: String) {
        input.value = value
    }

    fun send() {
        val userInput = input.value.trim()
        if (userInput.isEmpty() || isStreaming.value) return
        input.value = ""
        error.value = null

        viewModelScope.launch {
            isStreaming.value = true
            try {
                chatRepository.sendRequest(userInput)
            } catch (t: Throwable) {
                error.value = t.message ?: "Error"
            } finally {
                isStreaming.value = false
            }
        }
    }
}

