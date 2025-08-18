package com.bungaedu.donotbelate.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.bungaedu.donotbelate.data.repository.TimerConfigRepository
import com.bungaedu.donotbelate.logic.NotificationHelper
import com.bungaedu.donotbelate.service.DuranteService
import kotlinx.coroutines.flow.Flow

private const val TAG = "*DuranteViewModel"

class DuranteViewModel(
    repo: TimerConfigRepository
) : ViewModel() {

    // 👀 Observamos directamente DataStore
    val tiempoRestante: Flow<Int?> = repo.minutosRestantesFlow()
    val avisarCadaMin: Flow<Int?> = repo.avisarFlow()
    val duranteMin: Flow<Int?> = repo.duranteFlow()

    /**
     * Detiene el temporizador y cancela la notificación.
     */
    fun stopTimer(context: Context? = null) {
        context?.let {
            DuranteService.stop(it)
            NotificationHelper.cancelAll(it)
        }
    }
}