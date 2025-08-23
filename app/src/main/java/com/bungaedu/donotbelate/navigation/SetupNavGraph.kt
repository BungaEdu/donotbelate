package com.bungaedu.donotbelate.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bungaedu.donotbelate.presentation.screens.*
import com.bungaedu.donotbelate.presentation.viewmodel.DuranteViewModel
import org.koin.androidx.compose.koinViewModel

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
            Log.w(TAG, "Navigated to DuranteScreen")
            DuranteScreen(navController)
        }

        composable(Screen.Hasta.route) {
            Log.w(TAG, "Navigated to HastaScreen")
            HastaScreen(navController)
        }

        composable(Screen.Profile.route) {
            Log.w(TAG, "Navigated to ProfileScreen")
            ProfileScreen(navController)
        }

        composable(Screen.Settings.route) {
            Log.w(TAG, "Navigated to SettingsScreen")
            SettingsScreen(navController)
        }

        composable(route = Screen.DuranteRunning.route) {
            Log.w(TAG, "Navigated to DuranteRunningScreen")
            val duranteViewModel: DuranteViewModel = koinViewModel()
            DuranteRunningScreen(navController, duranteViewModel)
        }
    }
}
