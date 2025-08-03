package com.example.donotbelate_v3.presentation.screens

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
import com.example.donotbelate_v3.logic.TimerStopper
import com.example.donotbelate_v3.presentation.viewmodels.DuranteViewModel
import com.example.donotbelate_v3.ui.theme.GalanoGrotesque

@Composable
fun DuranteRunningScreen(
    navController: NavController,
    avisarCadaMin: Int,
    duranteMin: Int,
    viewModel: DuranteViewModel = viewModel()
) {
    val tiempoRestante by viewModel.tiempoRestante.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        TimerStopper.registerStopCallback {
            viewModel.stopTimer()
            navController.popBackStack()
        }

        viewModel.startTimer(context, duranteMin, avisarCadaMin)
    }

    DisposableEffect(Unit) {
        onDispose {
            TimerStopper.reset()
        }
    }

    BackHandler {
        viewModel.stopTimer()
        navController.popBackStack()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF4D59BE))
    ) {
        // Círculo con X arriba
        IconButton(
            onClick = {
                viewModel.stopTimer()
                navController.popBackStack()
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

        Text(
            text = "Quedan", style = MaterialTheme.typography.headlineLarge,
            color = Color(0xFF181C3B),
            fontFamily = FontFamily.Default,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 144.dp),
        )

        // Texto grande de cuenta atrás
        Text(
            text = tiempoRestante.toString(), // muestra minutos restantes
            fontFamily = GalanoGrotesque,
            fontSize = 280.sp,
            color = Color(0xFFEAB916),
            modifier = Modifier.align(Alignment.Center),
            lineHeight = 280.sp
        )

        Text(
            text = "Te avisaré cada $avisarCadaMin minutos durante $duranteMin minutos",
            color = Color(0xFF181C3B),
            fontFamily = FontFamily.Default,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 144.dp),
        )
    }
}
