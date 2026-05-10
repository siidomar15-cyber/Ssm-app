package com.ssm.study.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ssm.study.data.TopicProgress

val ScreenPadding = PaddingValues(horizontal = 32.dp, vertical = 24.dp)

@Composable
fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    ElevatedCard(modifier) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            Text(value, style = MaterialTheme.typography.headlineMedium)
        }
    }
}

@Composable
fun TopicProgressRow(progress: TopicProgress, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(progress.topic.displayName, style = MaterialTheme.typography.titleMedium)
            Text("${progress.total} questions", style = MaterialTheme.typography.labelMedium)
        }
        LinearProgressIndicator(progress = { progress.accuracy }, modifier = Modifier.fillMaxWidth())
        Text(
            "${(progress.accuracy * 100).toInt()}% accuracy · ${progress.attempted}/${progress.total} questions seen",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun LoadingState(message: String, modifier: Modifier = Modifier) {
    Surface(modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.padding(ScreenPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
            Text(message, Modifier.padding(top = 16.dp), style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun ErrorState(title: String, message: String, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().padding(ScreenPadding), verticalArrangement = Arrangement.Center) {
        Card(shape = RoundedCornerShape(28.dp)) {
            Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(title, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.error)
                Text(message, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
