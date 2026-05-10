package com.ssm.study.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ssm.study.data.TopicProgress

@Composable
fun WeakAreasScreen(topics: List<TopicProgress>) {
    Column(Modifier.fillMaxSize().padding(ScreenPadding), verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text("Weak areas", style = MaterialTheme.typography.displaySmall)
        if (topics.isEmpty()) {
            Text("Weak-area analytics appear once you answer questions across topics.")
        } else {
            topics.forEach { progress ->
                ElevatedCard(Modifier.fillMaxWidth()) {
                    TopicProgressRow(progress, Modifier.padding(18.dp))
                }
            }
        }
    }
}
