package com.bungaedu.donotbelate.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bungaedu.donotbelate.logic.NotificationHelper
import com.bungaedu.donotbelate.logic.TtsManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.android.ext.android.inject

private const val TAG = "*DuranteService"

class DuranteService : Service() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var timerJob: Job? = null

    // ✅ Inyección de TtsManager con Koin
    private val ttsManager: TtsManager by inject()

    companion object {
        // StateFlow para el estado del servicio
        private val _isRunning = MutableStateFlow(false)
        val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

        // StateFlow para los minutos restantes
        private val _minutosRestantes = MutableStateFlow(0)
        val minutosRestantes: StateFlow<Int> = _minutosRestantes.asStateFlow()

        // StateFlow para la configuración actual
        private val _avisarCadaMin = MutableStateFlow(0)
        val avisarCadaMin: StateFlow<Int> = _avisarCadaMin.asStateFlow()

        private val _duranteMin = MutableStateFlow(0)
        val duranteMin: StateFlow<Int> = _duranteMin.asStateFlow()

        //TODO Hacer lo del datastore
        fun start(context: Context, avisarCadaMin: Int, duranteMin: Int) {
            val intent = Intent(context, DuranteService::class.java).apply {
                putExtra("avisarCadaMin", avisarCadaMin)
                putExtra("duranteMin", duranteMin)
            }
            context.startForegroundService(intent)
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, DuranteService::class.java))
        }
    }

    @SuppressLint("ForegroundServiceType") //TODO revisar si necesito algo para quitar el supress
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val duranteMin = intent?.getIntExtra("duranteMin", 0) ?: 0
        val avisarCadaMin = intent?.getIntExtra("avisarCadaMin", 1) ?: 1

        // Actualizar StateFlow
        //TODO ver a quien orquesta esta info para cargármela con datastore
        _isRunning.value = true
        _avisarCadaMin.value = avisarCadaMin
        _duranteMin.value = duranteMin
        //_minutosRestantes.value = esto lo modifica el timer

        val initialNotification = NotificationHelper.buildForegroundNotification(
            context = this,
            title = "Avisar cada $avisarCadaMin minutos durante $duranteMin minutos",
            content = "Te quedan $duranteMin minutos"
        )

        // Mostrar la notificación como foreground
        startForeground(100, initialNotification)

        // Iniciar temporizador
        //TODO Aquí pasarle lo de datastore
        startTimer(avisarCadaMin, duranteMin)

        return START_NOT_STICKY
    }

    private fun startTimer(avisarCadaMin: Int, duranteMin: Int) {
        timerJob?.cancel()
        timerJob = serviceScope.launch {
            for (minutos in duranteMin downTo 0) {
                // Actualizar StateFlow con minutos restantes
                _minutosRestantes.value = minutos

                // 🔁 Emitir evento con minutos restantes
                val intent = Intent("TIEMPO_RESTANTE_UPDATE").apply {
                    putExtra("minutosRestantes", minutos)
                }
                LocalBroadcastManager.getInstance(this@DuranteService).sendBroadcast(intent)

                if (minutos > 0 && minutos % avisarCadaMin == 0) {
                    Log.i(TAG, "Entro en condicional para speak")
                    NotificationHelper.updateNotification(
                        context = this@DuranteService,
                        title = "Avisar cada $avisarCadaMin min durante $duranteMin min",
                        content = "Te quedan $minutos minutos",
                        isOngoing = true
                    )

                    if (ttsManager.isReady()) {
                        ttsManager.speak("Te quedan $minutos minutos")
                    } else {
                        Log.w(TAG, "TTS no inicializado todavía (avisar $minutos)")
                    }
                }

                // ✅ Solo cuando llega a 0
                if (minutos == 0) {
                    stopForeground(false)
                    NotificationHelper.updateNotification(
                        context = this@DuranteService,
                        title = "Temporizador finalizado",
                        content = "Se ha acabado el tiempo, no llegues tarde",
                        isOngoing = false
                    )

                    if (ttsManager.isReady()) {
                        ttsManager.speak("Se ha acabado el tiempo, no llegues tarde")
                    } else {
                        Log.w(TAG, "TTS no inicializado todavía (final)")
                    }
                }

                delay(60_000)
            }

            stopSelf()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timerJob?.cancel()
        NotificationHelper.cancelAll(this)
        // Actualizar StateFlow
        _isRunning.value = false
        _avisarCadaMin.value = 0
        _duranteMin.value = 0
        _minutosRestantes.value = 0
    }

    override fun onBind(intent: Intent?): IBinder? = null
}