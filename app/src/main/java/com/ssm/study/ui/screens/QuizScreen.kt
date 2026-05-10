package com.ssm.study.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ssm.study.data.QuestionWithFlag
import com.ssm.study.viewmodel.QuizUiState

@Composable
fun QuizScreen(
    state: QuizUiState,
    onAnswer: (Int) -> Unit,
    onNext: () -> Unit,
    onBookmark: (QuestionWithFlag) -> Unit,
    onDifficult: (QuestionWithFlag) -> Unit,
    onAiTutor: () -> Unit,
    onDismissAiTutor: () -> Unit,
    onFinish: () -> Unit
) {
    val item = state.current
    if (item == null) {
        Column(Modifier.fillMaxSize().padding(ScreenPadding), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("No questions loaded", style = MaterialTheme.typography.headlineMedium)
            Text("Pick a topic, run search, or start a simulation from the dashboard.")
        }
        return
    }
    val question = item.question
    Column(Modifier.fillMaxSize().padding(ScreenPadding), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(Modifier.weight(1f)) {
                Text("${state.mode.label} · ${question.topic.displayName} · ${question.subtopic}", color = MaterialTheme.colorScheme.primary)
                Text("Question ${state.currentIndex + 1}/${state.questions.size} · SSM ${question.yearStyle} style", style = MaterialTheme.typography.titleMedium)
                if (state.timedMode) Text("Time left: ${state.secondsRemaining / 60}:${(state.secondsRemaining % 60).toString().padStart(2, '0')}")
            }
            Row {
                IconButton(onClick = { onBookmark(item) }) {
                    Icon(if (item.bookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder, "Bookmark")
                }
                IconButton(onClick = { onDifficult(item) }) {
                    Icon(Icons.Outlined.Flag, "Difficult")
                }
            }
        }
        LinearProgressIndicator(
            progress = { (state.currentIndex + 1).toFloat() / state.questions.size.coerceAtLeast(1) },
            modifier = Modifier.fillMaxWidth()
        )
        ElevatedCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(question.stem, style = MaterialTheme.typography.headlineSmall)
                Text("${question.difficulty.uppercase()} · ${question.tags.joinToString(" · ")}", style = MaterialTheme.typography.labelMedium)
            }
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
            itemsIndexed(question.options) { index, option ->
                val selected = state.selectedIndex == index
                val correct = question.correctIndex == index
                val answered = state.selectedIndex != null
                val colors = when {
                    answered && correct -> CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    answered && selected -> CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    else -> CardDefaults.outlinedCardColors()
                }
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth().clickable(enabled = !answered) { onAnswer(index) },
                    border = if (selected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else CardDefaults.outlinedCardBorder(),
                    colors = colors
                ) {
                    Text("${'A' + index}. $option", Modifier.padding(18.dp), style = MaterialTheme.typography.titleMedium)
                }
            }
        }
        AnimatedVisibility(visible = state.selectedIndex != null) {
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(if (state.selectedIndex == question.correctIndex) "Correct" else "Review", style = MaterialTheme.typography.titleLarge)
                    Text(question.explanation)
                    Text("High-yield: ${question.takeaway}", color = MaterialTheme.colorScheme.primary)
                    AnimatedVisibility(visible = state.aiTutor.visible) {
                        Text(state.aiTutor.response, style = MaterialTheme.typography.bodyMedium)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(onClick = if (state.aiTutor.visible) onDismissAiTutor else onAiTutor) {
                            Text(if (state.aiTutor.visible) "Hide AI Tutor" else "AI Tutor")
                        }
                        Button(onClick = if (state.currentIndex == state.questions.lastIndex) onFinish else onNext) {
                            Text(if (state.currentIndex == state.questions.lastIndex) "See results" else "Next")
                        }
                    }
                }
            }
        }
    }
}
