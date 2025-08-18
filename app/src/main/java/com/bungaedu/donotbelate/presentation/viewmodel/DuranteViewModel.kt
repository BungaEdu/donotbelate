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
    val minutosRestantes: Flow<Int?> = repo.minutosRestantesFlow()
    val avisarCadaMin: Flow<Int?> = repo.avisarFlow()
    val duranteMin: Flow<Int?> = repo.duranteFlow()
}