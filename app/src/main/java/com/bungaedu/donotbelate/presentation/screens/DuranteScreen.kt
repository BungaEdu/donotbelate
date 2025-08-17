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
import com.bungaedu.donotbelate.data.TimerPrefs
import com.bungaedu.donotbelate.service.DuranteService
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun DuranteScreen(
    navController: NavController,
    deviceSettingsViewModel: DeviceSettingsViewModel = koinViewModel()
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val range = 1..59
    val context = LocalContext.current
    val notificationsAllowed by deviceSettingsViewModel.notificationsAllowed.collectAsState()

    // 1) Leemos DataStore (pueden ser null la 1ª vez)
    val savedAvisar by TimerPrefs.avisarFlow(context).collectAsState(initial = null)
    val savedDurante by TimerPrefs.duranteFlow(context).collectAsState(initial = null)

    // 2) Estado local (source of truth en UI)
    var avisarCadaMin by remember { mutableIntStateOf(range.first) }
    var duranteMin by remember { mutableIntStateOf(range.first) }

    var isStartingService by remember { mutableStateOf(false) }

    // Para no sobreescribir lo que el usuario ya tocó
    var initialized by remember { mutableStateOf(false) }

    // 3) Inicializamos desde Prefs una única vez cuando lleguen
    LaunchedEffect(savedAvisar, savedDurante) {
        if (!initialized) {
            avisarCadaMin = (savedAvisar ?: range.first).coerceIn(range)
            duranteMin = (savedDurante ?: range.first).coerceIn(range)
            initialized = true
        }
    }

    // 4) Guardar en DataStore cuando cambien
    fun onAvisarChange(v: Int) {
        val safe = v.coerceIn(range)
        avisarCadaMin = safe
        scope.launch { TimerPrefs.setAvisar(context, safe) }
    }

    fun onDuranteChange(v: Int) {
        val safe = v.coerceIn(range)
        duranteMin = safe
        scope.launch { TimerPrefs.setDurante(context, safe) }
    }

    // Permission launcher for Android 13+
    val requestNotificationsPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted || notificationsAllowed) {
            showBottomSheet = false
            //TODO quitar las variables y se quedará todo en el datastore
            navController.navigate("durante_running_screen/$avisarCadaMin/$duranteMin")
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

    // ----- UI -----
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
                onValueChange = ::onAvisarChange,
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
                onValueChange = ::onDuranteChange,
                range = range
            )
            Text("min", style = MaterialTheme.typography.headlineMedium)
        }

        Button(
            onClick = {
                if (notificationsAllowed) {
                    isStartingService = true
                    //TODO hacer lo de datastore
                    DuranteService.start(context, avisarCadaMin, duranteMin)
                } else {
                    deviceSettingsViewModel.onStartPressed()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isStartingService
        ) {
            if (isStartingService) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text("Iniciando…")
                }
            } else {
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