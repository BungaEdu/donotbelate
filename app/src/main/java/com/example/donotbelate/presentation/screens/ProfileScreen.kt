package com.example.donotbelate.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ContactSupport
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Output
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ProfileScreen(navController: NavController) {
    val TAG = "*ProfileScreen"
    Column(modifier = Modifier.fillMaxSize()) {
        // Header con avatar y nombre
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile",
                modifier = Modifier.size(96.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
            Text("Edu López", style = MaterialTheme.typography.headlineSmall)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Lista de ítems
        Divider()
        ProfileItem(
            title = "Name", value = "Edu López",
            icon = Icons.Default.Person,
            onClick = { /* Editar */ })
        ProfileItem(
            title = "Email",
            icon = Icons.Default.Mail,
            value = "eduardofloreseconomia@gmail.com",
            onClick = { /* Editar */ })
        ProfileItem(
            title = "Preferences",
            value = "Notifications, theme...",
            icon = Icons.Default.Settings,
            onClick = {
                navController.navigate(Screen.Settings.route) {
                    launchSingleTop = true
                }
            }
        )
        ProfileItem(
            title = "Desconectar",
            value = "Cerrar sesión",
            icon = Icons.Default.Output,
            onClick = { /* Editar */ })
        ProfileItem(
            title = "Ayuda",
            icon = Icons.AutoMirrored.Filled.ContactSupport,
            value = "Contacta con nosotros",
            onClick = { /* Editar */ })
    }
}

@Composable
fun ProfileItem(
    title: String,
    value: String,
    icon: ImageVector? = null,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(value) },
        trailingContent = {
            icon?.let {
                Icon(it, contentDescription = null)
            } ?: Icon(Icons.Default.ArrowForwardIos, contentDescription = null)
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 8.dp)
    )
    Divider()
}