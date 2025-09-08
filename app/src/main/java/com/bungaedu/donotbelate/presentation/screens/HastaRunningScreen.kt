package com.bungaedu.donotbelate.presentation.screens

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bungaedu.donotbelate.data.repository.TimerStateRepository
import com.bungaedu.donotbelate.navigation.Screen
import com.bungaedu.donotbelate.presentation.theme.GalanoGrotesque
import com.bungaedu.donotbelate.presentation.viewmodel.HastaViewModel
import com.bungaedu.donotbelate.service.HastaService
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel
import java.time.format.DateTimeFormatter

private const val TAG = "*HastaRunningScreen"

@Composable
fun HastaRunningScreen(
    navController: NavController,
    hastaViewModel: HastaViewModel = koinViewModel(),
    repo: TimerStateRepository = get()
) {
    val context = LocalContext.current
    val scopeButtonClose = rememberCoroutineScope()

    val avisarCadaMin by hastaViewModel.avisarCadaMin.collectAsState()
    val minutosRestantes by hastaViewModel.minutosRestantes.collectAsState()
    val horaObjetivo by hastaViewModel.horaObjetivo.collectAsState()

    BackHandler {
        HastaService.stop(context)
        navController.popBackStack()
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFF4D59BE))
    ) {
        IconButton(
            onClick = {
                Log.d(TAG, "Cerrar pulsado")
                scopeButtonClose.launch {
                    repo.setIsRunningServiceHasta(false)
                    HastaService.stop(context)
                    navController.navigate(Screen.Hasta.route) {
                        popUpTo(Screen.HastaRunning.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            },
            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
        ) {
            Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
        }

        // 📝 Texto superior con info del aviso
        horaObjetivo?.let {
            Text(
                text = "Avisar cada $avisarCadaMin min hasta las",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 144.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 🔢 Hora objetivo gigante en el centro
            Text(
                text = it.format(DateTimeFormatter.ofPattern("HH:mm")),
                fontFamily = GalanoGrotesque,
                fontSize = 100.sp,
                color = Color(0xFFEAB916), // Amarillo
                modifier = Modifier.align(Alignment.Center),
                lineHeight = 200.sp
            )
        }
    }
}