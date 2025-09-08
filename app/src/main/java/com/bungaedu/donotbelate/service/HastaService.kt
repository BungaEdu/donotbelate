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
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

private const val TAG = "*HastaService"

class HastaService : Service() {
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var timerJob: Job? = null

    private val ttsManager: TtsManager by inject()
    private val repo: TimerStateRepository by inject()

    companion object {
        fun start(context: Context) {
            Log.d(TAG, "Start recibido")
            ContextCompat.startForegroundService(
                context,
                Intent(context, HastaService::class.java)
            )
        }

        fun stop(context: Context) {
            Log.d(TAG, "Stop recibido")
            context.stopService(Intent(context, HastaService::class.java))
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
        startForeground(200, initial)

        serviceScope.launch {
            val avisar = repo.getAvisarHasta() ?: 1
            val horaObjetivo = repo.getHoraObjetivo() ?: LocalTime.now().plusMinutes(1)

            Log.d(TAG, "Config desde DataStore → avisar=$avisar, horaObjetivo=$horaObjetivo")

            // Estado inicial en DataStore
            repo.setIsRunningServiceHasta(true)

            // Actualiza notificación real
            val horaStr = horaObjetivo.format(DateTimeFormatter.ofPattern("HH:mm"))
            /*val minutosRestantes =
                ChronoUnit.MINUTES.between(LocalTime.now(), horaObjetivo).toInt()*/

            NotificationHelper.updateNotification(
                context = this@HastaService,
                title = "Avisar cada $avisar min hasta las $horaStr",
                content = "",
                isOngoing = true
            )

            startTimer(avisar, horaObjetivo)
        }

        return START_NOT_STICKY
    }

    private fun startTimer(avisarCadaMin: Int, horaObjetivo: LocalTime) {
        timerJob?.cancel()
        timerJob = serviceScope.launch {

            // Mensaje inicial
            if (ttsManager.isReady()) {
                ttsManager.speak("Te avisaré cada $avisarCadaMin minutos hasta las $horaObjetivo")
            } else {
                Log.w(TAG, "TTS no inicializado todavía (mensaje inicial)")
            }

            // Calcular la próxima hora de aviso (hora actual + avisarCadaMin)
            var proximoAviso = LocalTime.now().plusMinutes(avisarCadaMin.toLong())

            while (LocalTime.now().isBefore(horaObjetivo)) {
                val minutosRestantes = ChronoUnit.MINUTES.between(LocalTime.now(), horaObjetivo).toInt()
                repo.setMinutosRestantes(minutosRestantes)

                NotificationHelper.updateNotification(
                    this@HastaService,
                    "Avisar cada $avisarCadaMin min hasta las $horaObjetivo",
                    "Te quedan $minutosRestantes minutos",
                    isOngoing = true
                )

                // Verificar si hemos llegado a la hora de aviso
                val ahora = LocalTime.now()
                if (!ahora.isBefore(proximoAviso) && ahora.isBefore(horaObjetivo)) {
                    if (ttsManager.isReady()) {
                        ttsManager.speak("Son las ${ahora.hour.toString().padStart(2, '0')}:${ahora.minute.toString().padStart(2, '0')}")
                    } else {
                        Log.w(TAG, "TTS no inicializado todavía (aviso horario)")
                    }

                    // Calcular el siguiente aviso
                    proximoAviso = proximoAviso.plusMinutes(avisarCadaMin.toLong())
                }

                delay(60_000) // Esperar 1 minuto
            }

            // Cuando se alcanza la hora objetivo
            if (!LocalTime.now().isBefore(horaObjetivo)) {
                NotificationHelper.updateNotification(
                    this@HastaService,
                    "Hora alcanzada",
                    "Ya son las $horaObjetivo, no llegues tarde",
                    isOngoing = false
                )

                if (ttsManager.isReady()) {
                    ttsManager.speak("Ya son las $horaObjetivo, no llegues tarde")
                } else {
                    Log.w(TAG, "TTS no inicializado todavía (mensaje final)")
                }

                stopSelf()
            }
        }
    }

    private fun cleanup() {
        // 1. Cancelar el timer job
        timerJob?.cancel()
        timerJob = null

        // 2. Cancelar todas las notificaciones
        NotificationHelper.cancelAll(this)

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