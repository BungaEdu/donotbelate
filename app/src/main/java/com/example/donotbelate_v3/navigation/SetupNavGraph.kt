package com.example.donotbelate_v3.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.donotbelate_v3.presentation.screens.*

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
        composable(Screen.Durante.route) { DuranteScreen(navController) }
        composable(Screen.Hasta.route) { HastaScreen(navController) }
        composable(Screen.Profile.route) { ProfileScreen(navController) }
        composable(Screen.Settings.route) { SettingsScreen(navController) }

        composable(
            route = Screen.DuranteRunning.route,
            arguments = listOf(
                navArgument("avisarCada") { type = NavType.IntType },
                navArgument("durante") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val avisarCada = backStackEntry.arguments?.getInt("avisarCada") ?: 5
            val durante = backStackEntry.arguments?.getInt("durante") ?: 30

            DuranteRunningScreen(
                navController = navController,
                avisarCadaMin = avisarCada,
                duranteMin = durante
            )
        }
    }
}
