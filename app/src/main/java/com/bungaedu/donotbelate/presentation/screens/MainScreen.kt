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
import com.bungaedu.donotbelate.presentation.viewmodel.HastaViewModel
import org.koin.androidx.compose.koinViewModel

private const val TAG = "*MainScreen"

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // ViewModels
    val duranteViewModel: DuranteViewModel = koinViewModel()
    val hastaViewModel: HastaViewModel = koinViewModel()

    // Estados
    val isRunningDurante by duranteViewModel.isRunningServiceDurante.collectAsState()
    val isRunningHasta by hastaViewModel.isRunningServiceHasta.collectAsState()

    val showTopBar = currentRoute !in listOf(
        Screen.Settings.route,
    )

    val showBottomBar = currentRoute in listOf(
        Screen.Durante.route,
        Screen.Hasta.route,
        Screen.Profile.route
    )

    LaunchedEffect(isRunningDurante) {
        Log.i(TAG, "Servicio Durante corriendo=$isRunningDurante")
        if (isRunningDurante && currentRoute != Screen.DuranteRunning.route) {
            Log.i(TAG, "entro1")
            navController.navigate(Screen.DuranteRunning.route) {
                Log.i(TAG, "entro2")
                popUpTo(navController.graph.startDestinationId) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    LaunchedEffect(isRunningHasta) {
        Log.i(TAG, "Servicio Hasta corriendo=$isRunningHasta")
        if (isRunningHasta && currentRoute != Screen.HastaRunning.route) {
            Log.i(TAG, "entro1")
            navController.navigate(Screen.HastaRunning.route) {
                Log.i(TAG, "entro2")
                popUpTo(navController.graph.startDestinationId) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    /*// 🔄 Navegación automática si algún servicio está corriendo
    LaunchedEffect(isRunningDurante, isRunningHasta) {
        Log.i(TAG, "isRunningDurante=$isRunningDurante, isRunningHasta=$isRunningHasta")

        when {
            isRunningDurante && currentRoute != Screen.DuranteRunning.route -> {
                navController.navigate(Screen.DuranteRunning.route) {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }

            isRunningHasta && currentRoute != Screen.HastaRunning.route -> {
                navController.navigate(Screen.HastaRunning.route) {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    }*/

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