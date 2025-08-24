package com.bungaedu.donotbelate.presentation.screens

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bungaedu.donotbelate.navigation.Screen
import com.bungaedu.donotbelate.presentation.components.TopBar
import com.bungaedu.donotbelate.navigation.SetupNavGraph
import com.bungaedu.donotbelate.presentation.components.BottomNavigationBar
import com.bungaedu.donotbelate.presentation.viewmodel.DuranteViewModel
import org.koin.androidx.compose.koinViewModel

private const val TAG = "*MainScreen"

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val duranteViewModel: DuranteViewModel = koinViewModel()
    val isRunningService by duranteViewModel.isRunningService.collectAsState()

    val showTopBar = currentRoute !in listOf(
        Screen.Settings.route,
    )

    val showBottomBar = currentRoute in listOf(
        Screen.Durante.route,
        Screen.Hasta.route,
        Screen.Profile.route
    )

    LaunchedEffect(isRunningService) {
        Log.i(TAG, "serviceRunning=$isRunningService")
        if (isRunningService && currentRoute != Screen.DuranteRunning.route) {
            Log.i(TAG, "entro1")
            navController.navigate(Screen.DuranteRunning.route) {
                Log.i(TAG, "entro2")
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