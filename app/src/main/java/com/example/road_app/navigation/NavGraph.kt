package com.example.road_app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.road_app.editor.EditorScreen
import com.example.road_app.scenariolist.ScenarioListScreen

object Routes {
    const val SCENARIO_LIST = "scenario_list"
    const val EDITOR = "editor"
}

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.SCENARIO_LIST
    ) {
        composable(Routes.SCENARIO_LIST) {
            ScenarioListScreen(
                onCreateNewScenario = {
                    navController.navigate(Routes.EDITOR)
                }
            )
        }
        composable(Routes.EDITOR) {
            EditorScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
