package com.bungaedu.donotbelate.presentation.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bungaedu.donotbelate.data.repository.TimerConfigRepository
import com.bungaedu.donotbelate.presentation.components.TopBar
import com.bungaedu.donotbelate.navigation.SetupNavGraph
import com.bungaedu.donotbelate.presentation.components.BottomNavigationBar
import org.koin.androidx.compose.get

private const val TAG = "*HastaScreen"

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val repo: TimerConfigRepository = get()
    val serviceRunning by repo.isRunningFlow().collectAsState(initial = false)

    val showTopBar = currentRoute !in listOf(
        Screen.Settings.route,
    )

    val showBottomBar = currentRoute in listOf(
        Screen.Durante.route,
        Screen.Hasta.route,
        Screen.Profile.route
    )

    LaunchedEffect(serviceRunning) {
        if (serviceRunning && currentRoute != Screen.DuranteRunning.route) {
            navController.navigate(Screen.DuranteRunning.route) {
                popUpTo(navController.graph.startDestinationId) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    Scaffold(
        topBar = {
            if (showTopBar) TopBar()
        },
        bottomBar = {
            if (showBottomBar) BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        SetupNavGraph(navController = navController, modifier = Modifier.padding(innerPadding))
    }
}