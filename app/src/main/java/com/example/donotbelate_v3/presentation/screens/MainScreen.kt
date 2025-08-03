package com.example.donotbelate_v3.presentation.screens

import BottomNavigationBar
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.donotbelate_v3.presentation.components.TopBar
import com.example.donotbelate_v3.navigation.SetupNavGraph

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showTopBar = currentRoute !in listOf(
        Screen.Settings.route,
        Screen.DuranteRunning.routeWithArgs
    )

    val showBottomBar = currentRoute in listOf(
        Screen.Durante.route,
        Screen.Hasta.route,
        Screen.Profile.route
    )

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