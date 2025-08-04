package com.example.donotbelate_v3.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.donotbelate_v3.logic.NotificationHelper
import kotlinx.coroutines.*

class DuranteService : Service() {
    private val TAG = "*DuranteService"
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var timerJob: Job? = null

    @SuppressLint("ForegroundServiceType") //TODO revisar si necesito algo para quitar el supress
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val duranteMin = intent?.getIntExtra("duranteMin", 0) ?: 0
        val avisarCadaMin = intent?.getIntExtra("avisarCadaMin", 1) ?: 1

        val initialNotification = NotificationHelper.buildForegroundNotification(
            context = this,
            title = "Avisar cada $avisarCadaMin minutos durante $duranteMin minutos",
            content = "Te quedan $duranteMin minutos"
        )

        // Mostrar la notificación como foreground
        startForeground(100, initialNotification)

        // Iniciar temporizador
        startTimer(duranteMin, avisarCadaMin)

        return START_NOT_STICKY
    }

    private fun startTimer(duranteMin: Int, avisarCadaMin: Int) {
        timerJob?.cancel()
        timerJob = serviceScope.launch {
            for (minutos in duranteMin downTo 0) {
                // 🔊 Emitir evento con minutos restantes
                val intent = Intent("TIEMPO_RESTANTE_UPDATE").apply {
                    putExtra("minutosRestantes", minutos)
                }
                LocalBroadcastManager.getInstance(this@DuranteService).sendBroadcast(intent)

                if (minutos > 0 && minutos % avisarCadaMin == 0) {
                    NotificationHelper.updateNotification(
                        context = this@DuranteService,
                        title = "Avisar cada $avisarCadaMin min durante $duranteMin min",
                        content = "Te quedan $minutos minutos",
                        isOngoing = true
                    )
                }

                delay(60_000)
            }

            NotificationHelper.updateNotification(
                context = this@DuranteService,
                title = "Temporizador finalizado",
                content = "Se ha acabado el tiempo, no llegues tarde",
                isOngoing = false
            )

            stopSelf()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timerJob?.cancel()
        NotificationHelper.cancelAll(this)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        fun start(context: android.content.Context, duranteMin: Int, avisarCadaMin: Int) {
            val intent = Intent(context, DuranteService::class.java).apply {
                putExtra("duranteMin", duranteMin)
                putExtra("avisarCadaMin", avisarCadaMin)
            }
            context.startForegroundService(intent)
        }

        fun stop(context: android.content.Context) {
            context.stopService(Intent(context, DuranteService::class.java))
        }
    }
}