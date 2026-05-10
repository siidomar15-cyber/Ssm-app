package com.ssm.study.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ssm.study.viewmodel.QuizUiState

@Composable
fun ResultsScreen(state: QuizUiState, onDone: () -> Unit) {
    val accuracy = if (state.answered == 0) 0 else state.correct * 100 / state.answered
    Column(Modifier.fillMaxSize().padding(ScreenPadding), verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Text("Results", style = MaterialTheme.typography.displaySmall)
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard("Score", "${state.correct}/${state.answered}")
            StatCard("Accuracy", "$accuracy%")
            StatCard("Mode", state.mode.label)
        }
        Text("Keep marked difficult questions in rotation and revisit weak topics from the dashboard.")
        Button(onClick = onDone) { Text("Back to dashboard") }
    }
}
