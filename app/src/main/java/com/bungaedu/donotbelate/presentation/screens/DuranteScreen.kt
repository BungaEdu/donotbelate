package com.bungaedu.donotbelate.presentation.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bungaedu.donotbelate.presentation.components.NumberPickerComposable
import com.bungaedu.donotbelate.presentation.components.NotificationPermissionBottomSheet
import com.bungaedu.donotbelate.presentation.viewmodel.DeviceSettingsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun DuranteScreen(
    navController: NavController,
    deviceSettingsViewModel: DeviceSettingsViewModel = koinViewModel()
) {
    // Navigation arguments
    val avisoInitial = navController.currentBackStackEntry
        ?.arguments?.getString("avisar")?.toIntOrNull() ?: 0
    val duranteInitial = navController.currentBackStackEntry
        ?.arguments?.getString("minuto")?.toIntOrNull() ?: 0

    var avisarCada by remember { mutableIntStateOf(avisoInitial) }
    var duranteMin by remember { mutableIntStateOf(duranteInitial) }
    var showBottomSheet by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val notificationsAllowed by deviceSettingsViewModel.notificationsAllowed.collectAsState()

    // Permission launcher for Android 13+
    val requestNotificationsPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted || notificationsAllowed) {
            showBottomSheet = false
            navController.navigate("durante_running_screen/$avisarCada/$duranteMin")
        } else {
            // Keep bottom sheet open to offer Settings option
            showBottomSheet = true
        }
    }

    // Handle ViewModel events
    LaunchedEffect(Unit) {
        deviceSettingsViewModel.events.collect { event ->
            when (event) {
                DeviceSettingsViewModel.UiEvent.ShowNotificationsBottomSheet -> {
                    showBottomSheet = true
                }

                DeviceSettingsViewModel.UiEvent.RequestRuntimePermission -> {
                    requestNotificationsPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                }

                DeviceSettingsViewModel.UiEvent.OpenAppSettings -> {
                    val intent = deviceSettingsViewModel.buildAppNotificationSettingsIntent()
                    context.startActivity(intent)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Avisar cada", style = MaterialTheme.typography.headlineMedium)
            NumberPickerComposable(
                value = avisarCada,
                onValueChange = { avisarCada = it },
                range = 1..59
            )
            Text("min", style = MaterialTheme.typography.headlineMedium)
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Durante", style = MaterialTheme.typography.headlineMedium)
            NumberPickerComposable(
                value = duranteMin,
                onValueChange = { duranteMin = it },
                range = 1..59
            )
            Text("min", style = MaterialTheme.typography.headlineMedium)
        }

        Button(
            onClick = {
                if (notificationsAllowed) {
                    navController.navigate("durante_running_screen/$avisarCada/$duranteMin")
                } else {
                    deviceSettingsViewModel.onStartPressed()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Empezar")
        }
    }

    NotificationPermissionBottomSheet(
        isVisible = showBottomSheet,
        shouldRedirectToSettings = deviceSettingsViewModel.shouldRedirectToSettings(),
        onRequestPermission = {
            deviceSettingsViewModel.onPermissionBottomSheetConfirmed()
        },
        onDismiss = { showBottomSheet = false }
    )
}