package com.example.donotbelate_v3.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun SettingsScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // TopBar personalizada con flecha atrás
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            IconButton(onClick = {
                navController.popBackStack(Screen.Profile.route, false)
            }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
            }
            Text(
                text = "Ajustes",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Divider()

        Spacer(modifier = Modifier.height(16.dp))

        // Opciones de ajustes
        Text("Notificaciones", style = MaterialTheme.typography.bodyLarge)
        Switch(checked = true, onCheckedChange = { /* TODO: manejar */ })

        Spacer(modifier = Modifier.height(16.dp))

        Text("Tema oscuro", style = MaterialTheme.typography.bodyLarge)
        Switch(checked = false, onCheckedChange = { /* TODO: manejar */ })

        // Aquí puedes seguir añadiendo más ajustes...
    }
}