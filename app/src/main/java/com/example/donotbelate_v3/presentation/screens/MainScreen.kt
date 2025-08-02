package com.example.donotbelate_v3.presentation.screens

import BottomNavigationBar
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.donotbelate_v2.presentation.components.TopBar
import com.example.donotbelate_v2.presentation.screens.DuranteScreen
import com.example.donotbelate_v2.presentation.screens.HastaScreen

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showTopBar = currentRoute !in listOf(Screen.Settings.route)
    val showBottomBar = currentRoute in listOf(
        Screen.Durante.route,
        Screen.Hasta.route,
        Screen.Profile.route
    )

    Scaffold(
        topBar = {
            if (showTopBar) {
                TopBar()
            }
        },
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            //startDestination = Screen.Durante.route,
            //startDestination = Screen.Profile.route,
            startDestination = Screen.Durante.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Durante.route) { DuranteScreen(navController) }
            composable(Screen.Hasta.route) { HastaScreen(navController) }
            composable(Screen.Profile.route) { ProfileScreen(navController) }
            composable(Screen.Settings.route) { SettingsScreen(navController) }
        }
    }
}