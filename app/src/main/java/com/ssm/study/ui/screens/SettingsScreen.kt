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
                Text("Room keeps questions, attempts, bookmarks and difficult flags on-device for fast offline sessions.")
                Switch(checked = true, onCheckedChange = null)
            }
        }
        ElevatedCard {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Timed simulation", style = MaterialTheme.typography.titleLarge)
                Text("Default mock exam: 120 questions in 120 minutes. Adjust this in the ViewModel when adding real exam profiles.")
            }
        }
    }
}
