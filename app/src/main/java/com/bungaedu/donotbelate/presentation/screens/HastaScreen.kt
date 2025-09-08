package com.bungaedu.donotbelate.presentation.screens

import android.Manifest
import android.util.Log
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
import com.bungaedu.donotbelate.presentation.components.NotificationPermissionBottomSheet
import com.bungaedu.donotbelate.presentation.components.NumberPickerComposable
import com.bungaedu.donotbelate.presentation.viewmodel.DeviceSettingsViewModel
import com.bungaedu.donotbelate.presentation.viewmodel.HastaViewModel
import com.bungaedu.donotbelate.service.HastaService
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.time.LocalTime

private const val TAG = "*HastaScreen"

@Composable
fun HastaScreen(
    navController: NavController,
    deviceSettingsViewModel: DeviceSettingsViewModel = koinViewModel(),
    hastaViewModel: HastaViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val scopeStartService = rememberCoroutineScope()
    val range = 1..59
    val now = LocalTime.now()

    val avisarCadaMin by hastaViewModel.avisarCadaMin.collectAsState()
    val isRunningService by hastaViewModel.isRunningServiceHasta.collectAsState()

    var horaSeleccionada by remember { mutableIntStateOf(now.hour) }
    var minutoSeleccionado by remember { mutableIntStateOf(now.minute) }
    var isStartingService by remember { mutableStateOf(false) }

    val notificationsAllowed by deviceSettingsViewModel.notificationsAllowed.collectAsState()
    var showBottomSheet by remember { mutableStateOf(false) }

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
                onValueChange = hastaViewModel::onAvisarChange,
                range = range
            )
            Text("min", style = MaterialTheme.typography.headlineMedium)
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Hasta las", style = MaterialTheme.typography.headlineMedium)
            NumberPickerComposable(
                value = horaSeleccionada,
                onValueChange = { horaSeleccionada = it },
                range = 0..23
            )
            Text(":")
            NumberPickerComposable(
                value = minutoSeleccionado,
                onValueChange = { minutoSeleccionado = it },
                range = 0..59
            )
        }

        Button(
            onClick = {
                Log.d(TAG, "notis: $notificationsAllowed")
                if (notificationsAllowed) {
                    isStartingService = true
                    scopeStartService.launch {
                        hastaViewModel.onHoraObjetivoChange(horaSeleccionada, minutoSeleccionado)
                        HastaService.start(context)
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
                    navController.navigate(Screen.HastaRunning.route)
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