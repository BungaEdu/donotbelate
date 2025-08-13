package com.bungaedu.donotbelate.presentation.viewmodel

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.ViewModel
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bungaedu.donotbelate.logic.NotificationHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

private const val TAG = "*DuranteViewModel"

class DuranteViewModel : ViewModel() {

    private val _tiempoRestante = MutableStateFlow(0) // En minutos
    private var timerJob: Job? = null
    val tiempoRestante: StateFlow<Int> = _tiempoRestante

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

    /**
     * Se suscribe al broadcast local para recibir actualizaciones del tiempo restante.
     */
    fun startListening(context: Context) {
        val filter = IntentFilter("TIEMPO_RESTANTE_UPDATE")
        LocalBroadcastManager.getInstance(context).registerReceiver(tiempoReceiver, filter)
    }

    /**
     * Cancela la suscripción al broadcast local.
     */
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