package com.ssm.study.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Topic
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ssm.study.data.Topic
import com.ssm.study.ui.screens.BookmarksScreen
import com.ssm.study.ui.screens.DashboardScreen
import com.ssm.study.ui.screens.QuizScreen
import com.ssm.study.ui.screens.ResultsScreen
import com.ssm.study.ui.screens.SettingsScreen
import com.ssm.study.ui.screens.TopicsScreen
import com.ssm.study.ui.screens.WeakAreasScreen
import com.ssm.study.viewmodel.SsmViewModel

private data class NavItem(val route: String, val label: String, val icon: ImageVector)

private val navItems = listOf(
    NavItem("dashboard", "Dashboard", Icons.Outlined.Dashboard),
    NavItem("topics", "Topics", Icons.Outlined.Topic),
    NavItem("bookmarks", "Saved", Icons.Outlined.BookmarkBorder),
    NavItem("weak", "Weak", Icons.Outlined.Flag),
    NavItem("settings", "Settings", Icons.Outlined.Settings)
)

@Composable
fun SsmApp(viewModel: SsmViewModel) {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    Row(Modifier.fillMaxSize()) {
        NavigationRail(Modifier.width(96.dp)) {
            navItems.forEach { item ->
                NavigationRailItem(
                    selected = backStack?.destination?.route == item.route,
                    onClick = { navController.navigate(item.route) { launchSingleTop = true } },
                    icon = { Icon(item.icon, contentDescription = item.label) },
                    label = { Text(item.label) }
                )
            }
        }
        NavHost(navController = navController, startDestination = "dashboard", modifier = Modifier.fillMaxSize()) {
            composable("dashboard") {
                val state by viewModel.dashboard.collectAsStateWithLifecycle()
                DashboardScreen(
                    state = state,
                    onStartSimulation = {
                        viewModel.startSimulation()
                        navController.navigate("quiz")
                    },
                    onStartMixed = {
                        viewModel.startMixedQuiz()
                        navController.navigate("quiz")
                    },
                    onStartWeak = {
                        viewModel.startWeakTopicQuiz()
                        navController.navigate("quiz")
                    },
                    onStartBookmarks = {
                        viewModel.startBookmarkQuiz()
                        navController.navigate("quiz")
                    },
                    onStartDifficult = {
                        viewModel.startDifficultQuiz()
                        navController.navigate("quiz")
                    },
                    onOpenTopics = { navController.navigate("topics") }
                )
            }
            composable("topics") {
                val topics by viewModel.topics.collectAsStateWithLifecycle()
                val search by viewModel.search.collectAsStateWithLifecycle()
                TopicsScreen(
                    topics = topics,
                    search = search,
                    onSearchChanged = viewModel::updateSearch,
                    onStartSearchQuiz = {
                        viewModel.startSearchQuiz()
                        navController.navigate("quiz")
                    },
                    onTopicClick = { topic: Topic ->
                        viewModel.startTopicQuiz(topic)
                        navController.navigate("quiz")
                    }
                )
            }
            composable("quiz") {
                val quiz by viewModel.quiz.collectAsStateWithLifecycle()
                QuizScreen(
                    state = quiz,
                    onAnswer = viewModel::answer,
                    onNext = viewModel::nextQuestion,
                    onBookmark = viewModel::toggleBookmark,
                    onDifficult = viewModel::toggleDifficult,
                    onAiTutor = viewModel::showAiTutor,
                    onDismissAiTutor = viewModel::hideAiTutor,
                    onFinish = { navController.navigate("results") }
                )
            }
            composable("results") {
                val quiz by viewModel.quiz.collectAsStateWithLifecycle()
                ResultsScreen(quiz) { navController.navigate("dashboard") }
            }
            composable("bookmarks") {
                val items by viewModel.bookmarks.collectAsStateWithLifecycle()
                BookmarksScreen(items, viewModel::toggleBookmark, viewModel::toggleDifficult)
            }
            composable("weak") {
                val topics by viewModel.topics.collectAsStateWithLifecycle()
                WeakAreasScreen(topics.filter { it.attempted > 0 }.sortedBy { it.mastery })
            }
            composable("settings") { SettingsScreen() }
        }
    }
}
