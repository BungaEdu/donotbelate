package com.bungaedu.donotbelate.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bungaedu.donotbelate.data.repository.TimerStateRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalTime

private const val TAG = "*HastaViewModel"

class HastaViewModel(
    private val repo: TimerStateRepository
) : ViewModel() {
    private val range = 1..59

    val avisarCadaMin: StateFlow<Int> = repo.avisarHastaFlow()
        .map { it ?: 1 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)

    val horaObjetivo: StateFlow<LocalTime?> = repo.horaObjetivoFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val minutosRestantes: StateFlow<Int> = repo.minutosRestantesFlow()
        .map { it ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val isRunningServiceHasta: StateFlow<Boolean> = repo.isRunningServiceHastaFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun onAvisarChange(v: Int) {
        val s = v.coerceIn(range)
        viewModelScope.launch {
            try {
                repo.setAvisarHasta(s)
            } catch (e: Exception) {
                Log.e(TAG, "Error saving avisarHasta: $s", e)
            }
        }
    }

    fun onHoraObjetivoChange(hora: Int, minuto: Int) {
        val nuevaHora = LocalTime.of(hora, minuto)
        viewModelScope.launch {
            try {
                repo.setHoraObjetivo(nuevaHora)
                Log.d(TAG, "Hora objetivo guardada: $nuevaHora")
            } catch (e: Exception) {
                Log.e(TAG, "Error guardando hora objetivo: $nuevaHora", e)
            }
        }
    }
}