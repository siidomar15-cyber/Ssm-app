package com.ssm.study.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ssm.study.data.Topic
import com.ssm.study.data.TopicProgress
import com.ssm.study.viewmodel.SearchUiState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TopicsScreen(
    topics: List<TopicProgress>,
    search: SearchUiState,
    onSearchChanged: (String, String) -> Unit,
    onStartSearchQuiz: () -> Unit,
    onTopicClick: (Topic) -> Unit
) {
    Column(Modifier.fillMaxSize().padding(ScreenPadding), verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Text("Topics", style = MaterialTheme.typography.displaySmall)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = search.query,
                onValueChange = { onSearchChanged(it, search.difficulty) },
                label = { Text("Search stems, subtopics, tags") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = search.difficulty,
                onValueChange = { onSearchChanged(search.query, it.lowercase()) },
                label = { Text("Difficulty") },
                modifier = Modifier.weight(0.35f)
            )
            Button(onClick = onStartSearchQuiz, enabled = search.results.isNotEmpty()) { Text("Quiz ${search.results.size}") }
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            topics.forEach { progress ->
                Card(Modifier.clickable { onTopicClick(progress.topic) }) {
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(progress.topic.displayName, style = MaterialTheme.typography.titleLarge)
                        Text("${progress.total} questions · ${progress.attempted} seen")
                        Text("Mastery ${progress.mastery}%")
                        AssistChip(onClick = { onTopicClick(progress.topic) }, label = { Text("Start practice") })
                    }
                }
            }
        }
    }
}
