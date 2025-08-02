package com.example.donotbelate_v3.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DuranteViewModel : ViewModel() {

    private val _tiempoRestante = MutableStateFlow(0)
    val tiempoRestante: StateFlow<Int> = _tiempoRestante

    private var timerJob: Job? = null

    fun startTimer(duracionSegundos: Int, avisarCada: Int) {
        stopTimer() // Por si había uno previo

        timerJob = viewModelScope.launch {
            for (i in duracionSegundos downTo 0) {
                _tiempoRestante.value = i

                if (i > 0 && i % avisarCada == 0) {
                    Log.d("DuranteTimer", "Quedan $i segundos")
                }

                delay(1_000)
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