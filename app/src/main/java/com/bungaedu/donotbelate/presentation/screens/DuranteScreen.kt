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

    // 1) Args de navegación (si llegan)
    val avisoArg = navController.currentBackStackEntry
        ?.arguments?.getString("avisar")?.toIntOrNull()
    val duranteArg = navController.currentBackStackEntry
        ?.arguments?.getString("minuto")?.toIntOrNull()

    // 2) Flows de DataStore (pueden ser null la 1ª vez)
    val savedAvisar by remember(context) { TimerPrefs.avisarFlow(context) }
        .collectAsState(initial = null)
    val savedDurante by remember(context) { TimerPrefs.duranteFlow(context) }
        .collectAsState(initial = null)

    // 3) Iniciales: prioridad -> args > DataStore > rango.first
    var avisarCada by remember(savedAvisar, avisoArg) {
        mutableIntStateOf(
            (avisoArg ?: savedAvisar)?.takeIf { it in range } ?: range.first
        )
    }
    var duranteMin by remember(savedDurante, duranteArg) {
        mutableIntStateOf(
            (duranteArg ?: savedDurante)?.takeIf { it in range } ?: range.first
        )
    }

    // 4) Guardar en DataStore cuando cambien
    fun onAvisarChange(v: Int) {
        val safe = v.coerceIn(range)
        avisarCada = safe
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
                value = avisarCada,
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
                val a = avisarCada.coerceIn(range)
                val d = duranteMin.coerceIn(range)
                if (notificationsAllowed) {
                    navController.navigate("durante_running_screen/$a/$d")
                } else {
                    deviceSettingsViewModel.onStartPressed()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Empezar") }
    }

    NotificationPermissionBottomSheet(
        isVisible = showBottomSheet,
        shouldRedirectToSettings = deviceSettingsViewModel.shouldRedirectToSettings(),
        onRequestPermission = { deviceSettingsViewModel.onPermissionBottomSheetConfirmed() },
        onDismiss = { showBottomSheet = false }
    )
}