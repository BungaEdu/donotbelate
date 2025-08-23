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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.bungaedu.donotbelate.data.repository.TimerStateRepository
import com.bungaedu.donotbelate.navigation.Screen
import com.bungaedu.donotbelate.service.DuranteService
import com.bungaedu.donotbelate.presentation.theme.GalanoGrotesque
import com.bungaedu.donotbelate.presentation.viewmodel.DuranteViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get

private const val TAG = "*DuranteRunningScreen"

@Composable
fun DuranteRunningScreen(
    navController: NavController,
    duranteViewModel: DuranteViewModel = viewModel(),
    repo: TimerStateRepository = get()
) {
    // 👀 Los Flow son Int? → hay que poner initial = null
    val minutosRestantes by duranteViewModel.minutosRestantes.collectAsState(initial = null)
    val avisarCadaMin by duranteViewModel.avisarCadaMin.collectAsState(initial = null)
    val duranteMin by duranteViewModel.duranteMin.collectAsState(initial = null)

    val context = LocalContext.current
    val scopeButtonClose = rememberCoroutineScope()

    // ⬅️ Si se pulsa atrás, se detiene el timer y se cierra la pantalla
    BackHandler {
        DuranteService.stop(context)
        navController.popBackStack()
    }

    // 🎨 UI de la pantalla
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF4D59BE)) // Azul de fondo
    ) {
        // ❌ Botón para cerrar (top-right)
        IconButton(
            onClick = {
                Log.d(TAG, "Presiono cerrar")
                scopeButtonClose.launch {
                    repo.setIsRunning(false)
                    DuranteService.stop(context)
                    //TODO solucionar el popBackStack, esto es un parche
                    navController.navigate(Screen.Durante.route)
                    //navController.popBackStack()
                }
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Cerrar",
                tint = Color.White
            )
        }

        // 🕒 Texto superior: "Quedan"
        Text(
            text = "Quedan",
            style = MaterialTheme.typography.headlineLarge,
            color = Color(0xFF181C3B), // Azul oscuro
            fontFamily = FontFamily.Default,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 144.dp)
        )

        // 🔢 Cuenta regresiva gigante en minutos
        Text(
            text = minutosRestantes.toString(),
            fontFamily = GalanoGrotesque,
            fontSize = 280.sp,
            color = Color(0xFFEAB916), // Amarillo
            modifier = Modifier.align(Alignment.Center),
            lineHeight = 280.sp
        )

        Log.d(TAG, "tiempoRestante: $minutosRestantes")

        // 📝 Texto inferior con info del aviso
        Text(
            text = "Te avisaré cada $avisarCadaMin minutos durante $duranteMin minutos",
            color = Color(0xFF181C3B),
            fontFamily = FontFamily.Default,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 144.dp)
        )
    }
}
