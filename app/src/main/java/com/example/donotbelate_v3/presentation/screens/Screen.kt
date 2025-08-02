package com.example.donotbelate_v3.presentation.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PunchClock
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Durante : Screen("durante", "Durante", Icons.Default.Timelapse)
    object Hasta : Screen("hasta", "Hasta", Icons.Default.PunchClock)
    object Profile : Screen("profile", "Perfil", Icons.Default.Person)
    object Settings : Screen("settings", "Settings", Icons.Default.Person)
}