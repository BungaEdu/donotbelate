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
import com.bungaedu.donotbelate.navigation.Screen
import com.bungaedu.donotbelate.presentation.components.NumberPickerComposable
import com.bungaedu.donotbelate.presentation.components.NotificationPermissionBottomSheet
import com.bungaedu.donotbelate.presentation.viewmodel.DeviceSettingsViewModel
import com.bungaedu.donotbelate.presentation.viewmodel.DuranteViewModel
import com.bungaedu.donotbelate.service.DuranteService
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

private const val TAG = "*DuranteScreen"

@Composable
fun DuranteScreen(
    navController: NavController,
    deviceSettingsViewModel: DeviceSettingsViewModel = koinViewModel(),
    duranteViewModel: DuranteViewModel = koinViewModel()
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val range = 1..59
    val context = LocalContext.current

    val notificationsAllowed by deviceSettingsViewModel.notificationsAllowed.collectAsState()
    val avisarCadaMin by duranteViewModel.avisarCadaMin.collectAsState()
    val duranteMin by duranteViewModel.duranteMin.collectAsState()
    val isRunningService by duranteViewModel.isRunningService.collectAsState()

    var isStartingService by remember { mutableStateOf(false) }

    // Permission launcher for Android 13+
    val requestNotificationsPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        showBottomSheet = !(granted || notificationsAllowed)
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
                value = avisarCadaMin,
                onValueChange = duranteViewModel::onAvisarChange,
                range = range
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
                onValueChange = duranteViewModel::onDuranteChange,
                range = range
            )
            Text("min", style = MaterialTheme.typography.headlineMedium)
        }

        Button(
            onClick = {
                if (notificationsAllowed) {
                    isStartingService = true
                    scope.launch {
                        DuranteService.start(context)
                    }
                } else {
                    deviceSettingsViewModel.onStartPressed()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isStartingService && !isRunningService
        ) {
            when {
                isStartingService -> {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Iniciando")
                        Spacer(modifier = Modifier.width(8.dp))
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                isRunningService -> {
                    isStartingService = false
                    navController.navigate(Screen.DuranteRunning.route)
                }

                else -> {
                    Text("Empezar")
                }
            }
        }

        NotificationPermissionBottomSheet(
            isVisible = showBottomSheet,
            shouldRedirectToSettings = deviceSettingsViewModel.shouldRedirectToSettings(),
            onRequestPermission = { deviceSettingsViewModel.onPermissionBottomSheetConfirmed() },
            onDismiss = { showBottomSheet = false }
        )
    }
}