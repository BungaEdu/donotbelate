package com.bungaedu.donotbelate.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.bungaedu.donotbelate.presentation.screens.*

private const val TAG = "*SetupNavGraph"
@Composable
fun SetupNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Durante.route,
        modifier = modifier
    ) {
        composable(Screen.Durante.route) {
            Log.d(TAG, "Navigated to DuranteScreen")
            DuranteScreen(navController)
        }

        composable(Screen.Hasta.route) {
            Log.d(TAG, "Navigated to HastaScreen")
            HastaScreen(navController)
        }

        composable(Screen.Profile.route) {
            Log.d(TAG, "Navigated to ProfileScreen")
            ProfileScreen(navController)
        }

        composable(Screen.Settings.route) {
            Log.d(TAG, "Navigated to SettingsScreen")
            SettingsScreen(navController)
        }

        composable(route = Screen.DuranteRunning.route) {
            DuranteRunningScreen(navController)
        }
    }
}
