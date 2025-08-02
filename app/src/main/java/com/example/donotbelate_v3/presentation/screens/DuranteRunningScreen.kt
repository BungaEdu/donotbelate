package com.example.donotbelate_v3.presentation.screens

import android.app.Activity
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.donotbelate_v3.presentation.viewmodels.DuranteViewModel

@Composable
fun DuranteRunningScreen(
    navController: NavController,
    avisarCada: Int,
    durante: Int,
    viewModel: DuranteViewModel = viewModel()
) {
    val tiempoRestante by viewModel.tiempoRestante.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.startTimer(durante, avisarCada)
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

        // Texto grande de cuenta atrás
        Text(
            text = tiempoRestante.toString(),
            style = MaterialTheme.typography.displayLarge,
            color = Color(0xFFEAB916),
            modifier = Modifier.align(Alignment.Center)
        )

        Text(
            text = "Te avisaré cada $avisarCada segundos durante $durante segundos",
            color = Color(0xFF181C3B),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 144.dp)
        )
    }
}
