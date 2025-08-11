package com.povush.aiadvent.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.povush.aiadvent.AppConfig
import com.povush.aiadvent.data.ChatRepository
import com.povush.aiadvent.data.QuestDto
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
    private val repo: ChatRepository,
) : ViewModel() {

    data class Message(
        val role: String,
        val content: String = "",
        val quest: QuestDto? = null,
    )
    data class UiState(
        val messages: List<Message> = listOf(
            Message("system", "Привет с планеты Пов-500!"),
        ),
        val input: String = "",
        val isStreaming: Boolean = false,
        val model: String = AppConfig.GPT_OSS_20B_FREE,
        val error: String? = null,
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val questAdapter = moshi.adapter(QuestDto::class.java)

    fun onInputChange(value: String) { _state.update { it.copy(input = value) } }

    fun send() {
        val userInput = state.value.input.trim()
        if (userInput.isEmpty() || state.value.isStreaming) return
        _state.update {
            it.copy(
                input = "",
                messages = it.messages + Message("user", userInput),
                error = null,
            )
        }
        viewModelScope.launch {
            _state.update { it.copy(isStreaming = true) }
            try {
                val history = state.value.messages.mapNotNull { msg ->
                    if (msg.role == "system") return@mapNotNull null
                    val content = msg.quest?.let { questAdapter.toJson(it) } ?: msg.content
                    msg.role to content
                }
                val response = repo.completeOnce(state.value.model, history)
                val quest = runCatching { questAdapter.fromJson(response) }.getOrNull()
                _state.update { st ->
                    if (
                        quest != null &&
                        quest.title.isNotBlank() &&
                        quest.description.isNotBlank() &&
                        quest.tasks.isNotEmpty()
                    ) {
                        st.copy(messages = st.messages + Message("assistant", quest = quest))
                    } else {
                        st.copy(messages = st.messages + Message("assistant", content = response))
                    }
                }
            } catch (t: Throwable) {
                _state.update { it.copy(error = t.message ?: "Error") }
            } finally {
                _state.update { it.copy(isStreaming = false) }
            }
        }
    }
}

