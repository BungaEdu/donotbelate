// VIEWMODEL SIMPLIFICADO - Ya no necesita BroadcastReceiver
package com.bungaedu.donotbelate.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.bungaedu.donotbelate.logic.NotificationHelper
import com.bungaedu.donotbelate.service.DuranteService
import kotlinx.coroutines.flow.StateFlow

private const val TAG = "*DuranteViewModel"

class DuranteViewModel : ViewModel() {

    // 🎉 Directamente del servicio - Sin BroadcastReceiver!
    val tiempoRestante: StateFlow<Int> = DuranteService.minutosRestantes
    val serviceRunning: StateFlow<Boolean> = DuranteService.isRunning
    val avisarCadaMin: StateFlow<Int> = DuranteService.avisarCadaMin
    val duranteMin: StateFlow<Int> = DuranteService.duranteMin


    /**
     * Detiene el temporizador y cancela la notificación.
     */
    fun stopTimer(context: Context? = null) {
        context?.let {
            DuranteService.stop(it)
            NotificationHelper.cancelAll(it)
        }
    }

    // 🗑️ Ya no necesitamos startListening() ni stopListening()
    // 🗑️ Ya no necesitamos BroadcastReceiver
    // 🗑️ Ya no necesitamos Job para el timer
}