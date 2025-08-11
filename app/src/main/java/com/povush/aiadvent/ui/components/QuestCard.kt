package com.povush.aiadvent.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.povush.aiadvent.network.model.QuestDto

@Composable
fun QuestCard(
    quest: QuestDto,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val checks = remember(quest.tasks) {
        mutableStateListOf<Boolean>().apply { repeat(quest.tasks.size) { add(false) } }
    }
    Surface(
        modifier = modifier,
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                quest.title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.clickable { expanded = !expanded }
            )
            if (expanded) {
                Spacer(Modifier.height(4.dp))
                Text(quest.description, style = MaterialTheme.typography.bodyMedium)
                quest.tasks.forEachIndexed { index, task ->
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = checks.getOrNull(index) ?: false,
                            onCheckedChange = { if (index < checks.size) checks[index] = it }
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(task, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}