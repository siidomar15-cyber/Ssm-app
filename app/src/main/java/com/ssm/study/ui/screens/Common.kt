package com.ssm.study.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ssm.study.data.TopicProgress

val ScreenPadding = PaddingValues(horizontal = 32.dp, vertical = 24.dp)

@Composable
fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            Text(value, style = MaterialTheme.typography.headlineMedium)
        }
    }
}

@Composable
fun TopicProgressRow(progress: TopicProgress, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(progress.topic.displayName, style = MaterialTheme.typography.titleMedium)
        LinearProgressIndicator(progress = { progress.accuracy }, modifier = Modifier.fillMaxWidth())
        Text(
            "${(progress.accuracy * 100).toInt()}% accuracy · ${progress.attempted}/${progress.total} questions seen",
            style = MaterialTheme.typography.bodySmall
        )
    }
}
