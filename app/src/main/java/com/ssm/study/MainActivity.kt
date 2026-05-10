package com.ssm.study

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.ssm.study.ui.SsmApp
import com.ssm.study.ui.theme.SsmTheme
import com.ssm.study.viewmodel.AppViewModelFactory
import com.ssm.study.viewmodel.SsmViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: SsmViewModel by viewModels {
        AppViewModelFactory((application as SsmStudyApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SsmTheme {
                SsmApp(viewModel)
            }
        }
    }
}
