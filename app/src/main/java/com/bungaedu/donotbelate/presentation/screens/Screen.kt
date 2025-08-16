package com.bungaedu.donotbelate.presentation.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PunchClock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    open val route: String,
    open val title: String = "",
    open val icon: ImageVector? = null
) {
    // Pantallas de navegación inferior
    object Durante : Screen("durante", "Durante", Icons.Default.Timelapse)
    object Hasta : Screen("hasta", "Hasta", Icons.Default.PunchClock)
    object Profile : Screen("profile", "Perfil", Icons.Default.Person)
    object Settings : Screen("settings", "Ajustes", Icons.Default.Settings)

    // Pantalla fullscreen sin icono ni título
    object DuranteRunning : Screen("durante_running_screen/{avisarCada}/{duranteMin}") {
        fun createRoute(avisarCada: Int, duranteMin: Int): String =
            "durante_running_screen/$avisarCada/$duranteMin"

        const val routeWithArgs = "durante_running_screen/{avisarCada}/{duranteMin}"
        override val title: String = "Durante (full)"
        override val icon: ImageVector? = null
    }
}
