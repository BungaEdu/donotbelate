package com.example.donotbelate_v2.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DuranteViewModel : ViewModel() {

    private val _tiempoRestante = MutableStateFlow(0) // en minutos
    val tiempoRestante: StateFlow<Int> = _tiempoRestante

    private var timerJob: Job? = null

    fun startTimer(duracionMinutos: Int, avisarCadaMinutos: Int) {
        stopTimer()

        timerJob = viewModelScope.launch {
            for (i in duracionMinutos downTo 0) {
                _tiempoRestante.value = i

                if (i > 0 && i % avisarCadaMinutos == 0) {
                    Log.d("DuranteTimer", "Quedan $i minutos")
                }

                delay(60_000) // espera 1 minuto
            }

            Log.d("DuranteTimer", "Se ha terminado el tiempo, no llegues tarde")
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
