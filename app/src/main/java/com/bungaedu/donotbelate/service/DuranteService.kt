package com.bungaedu.donotbelate.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import com.bungaedu.donotbelate.data.repository.TimerStateRepository
import com.bungaedu.donotbelate.logic.NotificationHelper
import com.bungaedu.donotbelate.logic.TtsManager
import com.bungaedu.donotbelate.utils.reportCrash
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject

private const val TAG = "*DuranteService"

class DuranteService : Service() {
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var timerJob: Job? = null

    // TODO arreglar con Koin e inyección de dependencias con buenas prácticas
    private val ttsManager: TtsManager by inject()
    private val repo: TimerStateRepository by inject()

    companion object {
        fun start(context: Context) {
            ContextCompat.startForegroundService(
                context,
                Intent(context, DuranteService::class.java)
            )
        }

        fun stop(context: Context) {
            Log.d(TAG, "Envío señal de stop")
            context.stopService(Intent(context, DuranteService::class.java))
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand(flags=$flags, startId=$startId, intent=$intent)")

        // Notificación inicial obligatoria (<5s)
        NotificationHelper.ensureChannel(this)
        val initial = NotificationHelper.buildForegroundNotification(
            context = this,
            title = "Iniciando…",
            content = "Preparando temporizador"
        )

        startForeground(100, initial)

        serviceScope.launch {
            val avisar = repo.getAvisarDurante() ?: 1
            val durante = repo.getDurante() ?: 1

            Log.d(TAG, "Config desde DataStore → avisar=$avisar, durante=$durante")

            // Estado inicial en DataStore
            repo.setIsRunningServiceDurante(true)

            // Actualiza notificación real
            NotificationHelper.updateNotification(
                context = this@DuranteService,
                title = "Avisar cada $avisar min durante $durante min",
                content = "Te quedan $durante minutos",
                isOngoing = true
            )

            startTimer(avisar, durante)
        }

        return START_NOT_STICKY
    }

    private fun startTimer(avisarCadaMin: Int, duranteMin: Int) {
        timerJob?.cancel()
        timerJob = serviceScope.launch {
            val inicio = System.currentTimeMillis()
            val fin = inicio + duranteMin * 60_000L

            // ✅ Aviso inicial y estado inicial
            repo.setMinutosRestantes(duranteMin)
            if (ttsManager.isReady()) {
                ttsManager.speak("Te quedan $duranteMin minutos")
            } else {
                reportCrash(TAG, "TTS no inicializado todavía (inicio)")
            }

            for (i in (duranteMin - 1) downTo 0) {
                val proximo = inicio + (duranteMin - i) * 60_000L
                val delayMs = proximo - System.currentTimeMillis()

                if (delayMs > 0) delay(delayMs) // 🔹 Esperar hasta el momento exacto

                // Actualizar minutos restantes
                repo.setMinutosRestantes(i)
                if (i > 0) {
                    NotificationHelper.updateNotification(
                        context = this@DuranteService,
                        title = "Avisar cada $avisarCadaMin min durante $duranteMin min",
                        content = "Te quedan $i minutos",
                        isOngoing = true
                    )
                }

                // Aviso intermedio si toca
                val transcurridos = duranteMin - i
                if (i > 0 && transcurridos % avisarCadaMin == 0) {
                    if (ttsManager.isReady()) {
                        ttsManager.speak("Te quedan $i minutos")
                    } else {
                        reportCrash(TAG, "TTS no inicializado todavía (avisar $i)")
                    }
                }

                // Aviso final
                if (i == 0) {
                    // Quita la notificación foreground
                    stopForeground(true)

                    // Crea una notificación normal que se queda en la bandeja
                    NotificationHelper.showFinalNotification(
                        context = this@DuranteService,
                        title = "Temporizador finalizado",
                        content = "Se ha acabado el tiempo, no llegues tarde"
                    )

                    if (ttsManager.isReady()) {
                        ttsManager.speak("Se ha acabado el tiempo, no llegues tarde")
                    } else {
                        Log.w(TAG, "TTS no inicializado todavía (final)")
                    }
                }

            }

            repo.setIsRunningServiceDurante(false)
            stopSelf()
        }
    }

    private fun cleanup() {
        // 1. Cancelar el timer job
        timerJob?.cancel()
        timerJob = null

        // 2. Cancelar solo la notificación ongoing, no la final
        NotificationHelper.cancelOngoing(this)

        // 3. Limpiar estado en DataStore (ahora con serviceScope)
        serviceScope.launch {
            repo.setIsRunningServiceDurante(false)
            repo.setMinutosRestantes(0)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cleanup()
        serviceScope.cancel() // Cancelar el scope al final
    }

    override fun onBind(intent: Intent?): IBinder? = null
}