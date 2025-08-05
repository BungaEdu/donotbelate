package com.example.donotbelate.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.donotbelate.presentation.components.NumberPickerComposable

@Composable
fun HastaScreen(navController: NavController) {
    val TAG = "*HastaScreen"
    val hora = navController.currentBackStackEntry
        ?.arguments?.getString("hora")?.toIntOrNull() ?: 14
    val minuto = navController.currentBackStackEntry
        ?.arguments?.getString("minuto")?.toIntOrNull() ?: 30
    val avisar = navController.currentBackStackEntry
        ?.arguments?.getString("avisar")?.toIntOrNull() ?: 3

    // States internos (puedes moverlos al ViewModel luego)
    var horaSeleccionada by remember { mutableIntStateOf(hora) }
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
            Text("min", style = MaterialTheme.typography.headlineMedium)
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Hasta las", style = MaterialTheme.typography.headlineMedium)
            NumberPickerComposable(
                //TODO dejo el value estatico para pruebas, luego hay que mejorarlo
                value = 1,
                onValueChange = { horaSeleccionada = it },
                range = 0..23
            )
            Text(":")
            NumberPickerComposable(
                //TODO dejo el value estatico para pruebas, luego hay que mejorarlo
                value = 3,
                onValueChange = { minutoSeleccionado = it },
                range = 0..59
            )
        }

        // tiempoRestante podría ir aquí como Text dinámico

        Button(
            onClick = {
                navController.popBackStack()
                // viewModel.detenerContador()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Empezar")
        }
    }
}
