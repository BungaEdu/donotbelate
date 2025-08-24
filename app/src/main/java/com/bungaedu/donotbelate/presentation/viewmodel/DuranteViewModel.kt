package com.bungaedu.donotbelate.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bungaedu.donotbelate.data.repository.TimerStateRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val TAG = "*DuranteViewModel"

class DuranteViewModel(
    private val repo: TimerStateRepository
) : ViewModel() {
    private val range = 1..59

    // ✅ Siempre devolvemos un valor no nulo con defaults
    val minutosRestantes: StateFlow<Int> = repo.minutosRestantesFlow()
        .map { it ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val avisarCadaMin: StateFlow<Int> = repo.avisarDuranteFlow()
        .map { it ?: 1 } // default 1 min
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)

    val duranteMin: StateFlow<Int> = repo.duranteFlow()
        .map { it ?: 1 } // default 1 min
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)

    val isRunningServiceDurante: StateFlow<Boolean> = repo.isRunningServiceDuranteFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // 🔄 Cambio de valores (UI -> Repo)
    fun onAvisarChange(v: Int) {
        val s = v.coerceIn(range)
        viewModelScope.launch {
            try {
                repo.setAvisarDurante(s)
            } catch (e: Exception) {
                Log.e(TAG, "Error saving avisarCadaMin: $s", e)
            }
        }
    }

    fun onDuranteChange(v: Int) {
        val s = v.coerceIn(range)
        viewModelScope.launch {
            try {
                repo.setDurante(s)
            } catch (e: Exception) {
                Log.e(TAG, "Error saving duranteMin: $s", e)
            }
        }
    }
}