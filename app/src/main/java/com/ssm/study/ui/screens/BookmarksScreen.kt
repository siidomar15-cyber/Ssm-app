package com.ssm.study.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ssm.study.data.QuestionWithFlag

@Composable
fun BookmarksScreen(
    questions: List<QuestionWithFlag>,
    onBookmark: (QuestionWithFlag) -> Unit,
    onDifficult: (QuestionWithFlag) -> Unit
) {
    Column(Modifier.fillMaxSize().padding(ScreenPadding), verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text("Bookmarks & difficult", style = MaterialTheme.typography.displaySmall)
        if (questions.isEmpty()) {
            Text("Use the bookmark and flag buttons during quizzes to build a focused review queue.")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(questions) { item ->
                    ElevatedCard(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("${item.question.topic.displayName} · ${item.question.year}", color = MaterialTheme.colorScheme.primary)
                            Text(item.question.stem, style = MaterialTheme.typography.titleMedium)
                            Text(item.question.takeaway)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                AssistChip(onClick = { onBookmark(item) }, label = { Text(if (item.bookmarked) "Remove bookmark" else "Bookmark") })
                                AssistChip(onClick = { onDifficult(item) }, label = { Text(if (item.difficult) "Mark easy" else "Mark hard") })
                            }
                        }
                    }
                }
            }
        }
    }
}
