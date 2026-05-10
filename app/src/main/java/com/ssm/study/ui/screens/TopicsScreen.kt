package com.ssm.study.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ssm.study.data.Topic
import com.ssm.study.data.TopicProgress

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TopicsScreen(topics: List<TopicProgress>, onTopicClick: (Topic) -> Unit) {
    Column(Modifier.fillMaxSize().padding(ScreenPadding), verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Text("Topics", style = MaterialTheme.typography.displaySmall)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            topics.forEach { progress ->
                Card(Modifier.clickable { onTopicClick(progress.topic) }) {
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(progress.topic.displayName, style = MaterialTheme.typography.titleLarge)
                        Text("${progress.total} questions · ${progress.yearLabel()}")
                        AssistChip(onClick = { onTopicClick(progress.topic) }, label = { Text("Start fast quiz") })
                    }
                }
            }
        }
    }
}

private fun TopicProgress.yearLabel(): String = if (total == 0) "mock bank pending" else "previous SSM style"
