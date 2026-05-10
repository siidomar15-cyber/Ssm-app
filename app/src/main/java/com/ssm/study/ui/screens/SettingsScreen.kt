package com.ssm.study.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen() {
    Column(Modifier.fillMaxSize().padding(ScreenPadding), verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text("Settings", style = MaterialTheme.typography.displaySmall)
        ElevatedCard {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Offline question bank", style = MaterialTheme.typography.titleLarge)
                Text("2,500 JSON questions are imported into Room on first launch. Attempts, bookmarks, difficult flags and streaks stay on-device.")
                Switch(checked = true, onCheckedChange = null)
            }
        }
        ElevatedCard {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Dark mode", style = MaterialTheme.typography.titleLarge)
                Text("The Material 3 theme follows the system appearance and is optimized for long tablet study sessions.")
            }
        }
        ElevatedCard {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Optional AI Tutor", style = MaterialTheme.typography.titleLarge)
                Text("AI Tutor is intentionally optional. The app remains fully usable offline; when a cloud endpoint is configured later, the quiz button can request expanded explanations.")
                Switch(checked = false, onCheckedChange = null)
            }
        }
        ElevatedCard {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Timed simulation", style = MaterialTheme.typography.titleLarge)
                Text("Default full simulation: 140 randomized questions in 140 minutes, matching a fast exam-practice workflow.")
            }
        }
    }
}
