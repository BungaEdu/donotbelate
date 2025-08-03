package com.example.donotbelate_v3.presentation.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.donotbelate_v3.logic.NotificationHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DuranteViewModel : ViewModel() {
    private val TAG = "*DuranteViewModel"

    private val _tiempoRestante = MutableStateFlow(0) // en minutos
    val tiempoRestante: StateFlow<Int> = _tiempoRestante

    private var timerJob: Job? = null

    fun startTimer(context: Context, duranteMin: Int, avisarCadaMin: Int) {
        stopTimer()

        timerJob = viewModelScope.launch {
            for (i in duranteMin downTo 0) {
                _tiempoRestante.value = i

                if (i > 0 && i % avisarCadaMin == 0) {
                    Log.d(TAG, "Quedan $i minutos")
                    NotificationHelper.sendNotification(
                        context = context,
                        title = "Avisar cada $avisarCadaMin minutos durante $duranteMin minutos",
                        content = "Te quedan $i minutos",
                    )
                }

                delay(60_000) // espera 1 minuto
            }

            Log.d(TAG, "Se ha terminado el tiempo, no llegues tarde")
            NotificationHelper.sendNotification(
                context = context,
                title = "Temporizador finalizado",
                content = "Se acabó el tiempo, no llegues tarde",
            )
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}
