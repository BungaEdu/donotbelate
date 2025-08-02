package com.example.donotbelate_v2.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.donotbelate_v3.presentation.components.NumberPickerComposable

@Composable
fun DuranteScreen(navController: NavController) {
    // ViewModel (comentado)
    // val viewModel: DuranteViewModel = koinViewModel()
    // val tiempoRestante by viewModel.tiempoRestante.collectAsState()

    val avisar = navController.currentBackStackEntry
        ?.arguments?.getString("avisar")?.toIntOrNull() ?: 3
    val minuto = navController.currentBackStackEntry
        ?.arguments?.getString("minuto")?.toIntOrNull() ?: 30

    // States internos (puedes moverlos al ViewModel luego)
    var minutoSeleccionado by remember { mutableIntStateOf(minuto) }
    var avisarCada by remember { mutableIntStateOf(avisar) }

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
            Text("seg", style = MaterialTheme.typography.headlineMedium)
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Durante", style = MaterialTheme.typography.headlineMedium)
            NumberPickerComposable(
                value = minutoSeleccionado,
                onValueChange = { minutoSeleccionado = it },
                range = 0..59
            )
            Text("min", style = MaterialTheme.typography.headlineMedium)
        }

        // tiempoRestante podría ir aquí como Text dinámico

        Button(
            onClick = {
                navController.navigate(
                    "durante_running_screen/$avisarCada/${minutoSeleccionado * 60}"
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Empezar")
        }
    }
}
