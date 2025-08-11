package com.povush.aiadvent.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.povush.aiadvent.model.ChatItem
import com.povush.aiadvent.model.Role
import com.povush.aiadvent.ui.ChatViewModel

@Composable
fun MessagesList(
    messages: List<ChatItem>,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.lastIndex)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(12.dp)
    ) {
        itemsIndexed(messages) { _, item ->
            when (item) {
                is ChatItem.Quest -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        QuestCard(
                            quest = item.quest,
                            modifier = Modifier.widthIn(max = 520.dp)
                        )
                    }
                }
                is ChatItem.Message -> {
                    val isUser = item.role == Role.User

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                    ) {
                        Surface(
                            color = if (isUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer,
                            tonalElevation = 1.dp,
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Column(
                                Modifier
                                    .widthIn(max = 520.dp)
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = if (isUser) "Ты" else "ПовБот \uD83E\uDD16",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(item.text, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}