package com.example.donotbelate.presentation.viewmodels

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.ViewModel
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.donotbelate.logic.NotificationHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DuranteViewModel : ViewModel() {
    private val TAG = "*DuranteViewModel"
    private val _tiempoRestante = MutableStateFlow(0) // En minutos
    val tiempoRestante: StateFlow<Int> = _tiempoRestante
    private var timerJob: Job? = null

    /**
     * Detiene el temporizador y cancela la notificación.
     */
    fun stopTimer(context: Context? = null) {
        timerJob?.cancel()
        timerJob = null

        context?.let {
            NotificationHelper.cancelAll(it)
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }

    fun startListening(context: Context) {
        val filter = IntentFilter("TIEMPO_RESTANTE_UPDATE")
        LocalBroadcastManager.getInstance(context).registerReceiver(tiempoReceiver, filter)
    }

    fun stopListening(context: Context) {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(tiempoReceiver)
    }

    private val tiempoReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val minutos = intent?.getIntExtra("minutosRestantes", 0) ?: 0
            _tiempoRestante.value = minutos
        }
    }

}