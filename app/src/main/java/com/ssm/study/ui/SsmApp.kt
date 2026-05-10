package com.ssm.study.ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.ListAlt
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
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
    NavItem("topics", "Topics", Icons.Outlined.ListAlt),
    NavItem("bookmarks", "Saved", Icons.Outlined.BookmarkBorder),
    NavItem("weak", "Weak", Icons.Outlined.Flag),
    NavItem("settings", "Settings", Icons.Outlined.Settings)
)

@Composable
fun SsmApp(viewModel: SsmViewModel) {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val selectedRoute = backStack?.destination?.route

    BoxWithConstraints(Modifier.fillMaxSize()) {
        when {
            maxWidth >= 1_100.dp -> PermanentNavigationDrawer(
                drawerContent = {
                    NavigationRail(Modifier.width(112.dp)) {
                        NavigationItems(selectedRoute, navController)
                    }
                }
            ) {
                AppNavHost(viewModel, navController, Modifier.fillMaxSize())
            }

            maxWidth >= 700.dp -> Row(Modifier.fillMaxSize()) {
                NavigationRail(Modifier.width(96.dp)) {
                    NavigationItems(selectedRoute, navController)
                }
                AppNavHost(viewModel, navController, Modifier.fillMaxSize())
            }

            else -> Column(Modifier.fillMaxSize()) {
                AppNavHost(viewModel, navController, Modifier.weight(1f))
                NavigationBar {
                    navItems.forEach { item ->
                        NavigationBarItem(
                            selected = selectedRoute == item.route,
                            onClick = { navController.navigateTopLevel(item.route) },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NavigationItems(selectedRoute: String?, navController: NavHostController) {
    navItems.forEach { item ->
        NavigationRailItem(
            selected = selectedRoute == item.route,
            onClick = { navController.navigateTopLevel(item.route) },
            icon = { Icon(item.icon, contentDescription = item.label) },
            label = { Text(item.label) }
        )
    }
}

@Composable
private fun AppNavHost(viewModel: SsmViewModel, navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = "dashboard", modifier = modifier) {
        composable("dashboard") {
            val state by viewModel.dashboard.collectAsStateWithLifecycle()
            DashboardScreen(
                state = state,
                onStartSimulation = {
                    viewModel.startSimulation()
                    navController.navigate("quiz")
                },
                onOpenTopics = { navController.navigateTopLevel("topics") }
            )
        }
        composable("topics") {
            val topics by viewModel.topics.collectAsStateWithLifecycle()
            TopicsScreen(topics) { topic: Topic ->
                viewModel.startTopicQuiz(topic)
                navController.navigate("quiz")
            }
        }
        composable("quiz") {
            val quiz by viewModel.quiz.collectAsStateWithLifecycle()
            QuizScreen(
                state = quiz,
                onAnswer = viewModel::answer,
                onNext = viewModel::nextQuestion,
                onBookmark = viewModel::toggleBookmark,
                onDifficult = viewModel::toggleDifficult,
                onFinish = { navController.navigate("results") }
            )
        }
        composable("results") {
            val quiz by viewModel.quiz.collectAsStateWithLifecycle()
            ResultsScreen(quiz) { navController.navigateTopLevel("dashboard") }
        }
        composable("bookmarks") {
            val items by viewModel.bookmarks.collectAsStateWithLifecycle()
            BookmarksScreen(items, viewModel::toggleBookmark, viewModel::toggleDifficult)
        }
        composable("weak") {
            val topics by viewModel.topics.collectAsStateWithLifecycle()
            WeakAreasScreen(topics.filter { it.attempted > 0 }.sortedBy { it.accuracy })
        }
        composable("settings") { SettingsScreen() }
    }
}

private fun NavHostController.navigateTopLevel(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
