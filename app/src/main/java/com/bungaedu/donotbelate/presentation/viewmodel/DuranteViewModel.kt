package com.bungaedu.donotbelate.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.bungaedu.donotbelate.data.repository.TimerStateRepository
import kotlinx.coroutines.flow.Flow

private const val TAG = "*DuranteViewModel"

class DuranteViewModel(
    repo: TimerStateRepository
) : ViewModel() {

    // 👀 Observamos directamente DataStore
    val minutosRestantes: Flow<Int?> = repo.minutosRestantesFlow()
    val avisarCadaMin: Flow<Int?> = repo.avisarFlow()
    val duranteMin: Flow<Int?> = repo.duranteFlow()
}