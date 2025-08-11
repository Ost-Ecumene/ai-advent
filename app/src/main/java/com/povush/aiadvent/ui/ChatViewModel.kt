package com.povush.aiadvent.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.povush.aiadvent.AppConfig
import com.povush.aiadvent.data.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repo: ChatRepository
) : ViewModel() {

    data class Message(val role: String, val content: String)
    data class UiState(
        val messages: List<Message> = listOf(
            Message("system", "Привет с планеты Пов-500!"),
        ),
        val input: String = "",
        val isStreaming: Boolean = false,
        val model: String = AppConfig.GPT_OSS_20B_FREE,
        val error: String? = null
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state

    fun onInputChange(value: String) { _state.update { it.copy(input = value) } }

    fun send(stream: Boolean = true) {
        val prompt = state.value.input.trim()
        if (prompt.isEmpty() || state.value.isStreaming) return
        _state.update {
            it.copy(
                input = "",
                messages = it.messages + Message("user", prompt),
                error = null
            )
        }
        if (stream) sendStreaming() else sendOnce()
    }

    private fun sendOnce() = viewModelScope.launch {
        _state.update { it.copy(isStreaming = true) }
        try {
            val answer = repo.completeOnce(
                state.value.model,
                history = state.value.messages.map { it.role to it.content }
            )
            _state.update { it.copy(messages = it.messages + Message("assistant", answer)) }
        } catch (t: Throwable) {
            _state.update { it.copy(error = t.message ?: "Error") }
        } finally {
            _state.update { it.copy(isStreaming = false) }
        }
    }

    private fun sendStreaming() = viewModelScope.launch {
        _state.update { it.copy(isStreaming = true) }
        val base = state.value.messages.map { it.role to it.content }
        val sb = StringBuilder()
        try {
            repo.streamCompletion(state.value.model, base).collect { token ->
                sb.append(token)
                _state.update { it.copy(messages = it.messages.dropLast(0)) } // trigger
                // show the partial assistant message by replacing/adding last
                _state.update { st ->
                    val withoutDraft = st.messages.filterIndexed { idx, _ -> idx < base.size }
                    st.copy(messages = withoutDraft + Message("assistant", sb.toString()))
                }
            }
        } catch (t: Throwable) {
            _state.update { it.copy(error = t.message) }
        } finally {
            _state.update { it.copy(isStreaming = false) }
        }
    }
}