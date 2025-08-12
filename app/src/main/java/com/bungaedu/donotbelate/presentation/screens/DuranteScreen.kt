package com.bungaedu.donotbelate.presentation.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bungaedu.donotbelate.presentation.components.NumberPickerComposable

private const val TAG = "*DuranteScreen"

@Composable
fun DuranteScreen(navController: NavController) {
    val avisar = navController.currentBackStackEntry
        ?.arguments?.getString("avisar")?.toIntOrNull() ?: 0
    val minuto = navController.currentBackStackEntry
        ?.arguments?.getString("minuto")?.toIntOrNull() ?: 0

    var duranteMin by remember { mutableIntStateOf(minuto) }
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
            //TODO dejo el value estatico para pruebas, luego hay que mejorarlo
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
            Text("Durante", style = MaterialTheme.typography.headlineMedium)
            NumberPickerComposable(
                //TODO dejo el value estatico para pruebas, luego hay que mejorarlo
                value = avisarCada,
                onValueChange = { duranteMin = it },
                range = 1..59
            )
            Text("min", style = MaterialTheme.typography.headlineMedium)
        }

        Button(
            onClick = {
                navController.navigate(
                    "durante_running_screen/$avisarCada/${duranteMin}"
                )
                Log.d(TAG, "From DuranteScreen to DuranteRunningScreen: avisarCada=$avisarCada, durante=$duranteMin")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Empezar")
        }
    }
}
