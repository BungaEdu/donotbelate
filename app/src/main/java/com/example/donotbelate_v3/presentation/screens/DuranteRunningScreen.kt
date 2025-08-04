package com.example.donotbelate_v3.presentation.screens

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
import com.example.donotbelate_v3.service.DuranteService
import com.example.donotbelate_v3.logic.NotificationHelper
import com.example.donotbelate_v3.logic.TimerHolder
import com.example.donotbelate_v3.presentation.viewmodels.DuranteViewModel
import com.example.donotbelate_v3.ui.theme.GalanoGrotesque

@Composable
fun DuranteRunningScreen(
    navController: NavController,
    avisarCadaMin: Int,
    duranteMin: Int,
    viewModel: DuranteViewModel = viewModel()
) {
    val TAG = "*DuranteRunningScreen"
    val tiempoRestante by viewModel.tiempoRestante.collectAsState()
    val context = LocalContext.current

    // 🔁 Al entrar en la pantalla, inicia el timer y el servicio foreground
    LaunchedEffect(Unit) {
        TimerHolder.viewModel = viewModel
        viewModel.startListening(context)
        DuranteService.start(context, duranteMin, avisarCadaMin)
    }

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
                viewModel.stopListening(context)
                DuranteService.stop(context) // Detiene el servicio
                NotificationHelper.cancelAll(context) // Cancela la notificación
                navController.popBackStack() // Cierra pantalla
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
            text = tiempoRestante.toString(),
            fontFamily = GalanoGrotesque,
            fontSize = 280.sp,
            color = Color(0xFFEAB916), // Amarillo
            modifier = Modifier.align(Alignment.Center),
            lineHeight = 280.sp
        )

        Log.d(TAG, "tiempoRestante: $tiempoRestante")

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
