package com.ssm.study.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ssm.study.viewmodel.DashboardUiState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(state: DashboardUiState, onStartSimulation: () -> Unit, onOpenTopics: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(ScreenPadding),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("SSM Studio", style = MaterialTheme.typography.displaySmall)
                Text("Fast tablet-first prep for the Italian specialization exam", style = MaterialTheme.typography.titleMedium)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onOpenTopics) { Text("Topic drill") }
                Button(onClick = onStartSimulation) { Text("Timed simulation") }
            }
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard("Question bank", state.totalQuestions.toString())
            StatCard("Attempted", state.attemptedQuestions.toString())
            StatCard("Accuracy", "${(state.accuracy * 100).toInt()}%")
            StatCard("Saved", "${state.bookmarked} · ${state.difficult} hard")
        }
        ElevatedCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Weak areas", style = MaterialTheme.typography.headlineSmall)
                if (state.weakAreas.isEmpty()) {
                    Text("Answer a few questions to unlock adaptive weak-area tracking.")
                } else {
                    state.weakAreas.forEach { TopicProgressRow(it) }
                }
            }
        }
    }
}
