package com.example.road_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.road_app.navigation.AppNavGraph
import com.example.road_app.ui.theme.RoadScenarioBuilderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RoadScenarioBuilderTheme {
                AppNavGraph()
            }
        }
    }
}
