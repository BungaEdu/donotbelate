package com.bungaedu.donotbelate.logic

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bungaedu.donotbelate.R

@SuppressLint("MissingPermission") // Recuerda pedir el permiso de notificaciones en Android 13+
object NotificationHelper {
    private const val TAG = "*NotificationHelper"
    private const val CHANNEL_ID = "durante_timer_channel"
    private const val CHANNEL_NAME = "Durante Timer"
    private const val CHANNEL_DESCRIPTION = "Avisos periódicos del temporizador"
    private const val TIMER_NOTIFICATION_ID = 100

    fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = CHANNEL_DESCRIPTION
        }

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
        Log.d(TAG, "Notificación creada")
    }

    /**
     * Crea el canal de notificaciones si hace falta (Android 8+).
     * Se puede llamar tantas veces como quieras; es idempotente.
     */
    fun ensureChannel(context: Context) {
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
            description = "CHANNEL_DESC"
            enableLights(false)
            enableVibration(false)
            setShowBadge(false)
            // Si quieres que no haga ruido:
            setSound(null, null)
            lightColor = Color.BLUE
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(channel)
    }

    /**
     * Construye una notificación completa con botón para cancelar (detener el temporizador).
     */
    private fun buildNotification(
        context: Context,
        title: String,
        content: String,
        isOngoing: Boolean
    ): Notification {
        // Intent para traer la app tal cual esté (si hay task) o abrirla normal (si no)
        val launchIntent = context.packageManager
            .getLaunchIntentForPackage(context.packageName)
            ?.apply {
                addFlags(
                    Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or // trae al frente si existe
                            Intent.FLAG_ACTIVITY_SINGLE_TOP or       // evita recrear si ya está arriba
                            Intent.FLAG_ACTIVITY_CLEAR_TOP           // limpia duplicados si los hubiera
                )
            }

        val contentPendingIntent = PendingIntent.getActivity(
            context,
            0,
            launchIntent, // puede ser null solo en casos raros; en la práctica no lo es
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOnlyAlertOnce(true)
            .setOngoing(isOngoing)
            .setAutoCancel(!isOngoing)
            .setContentIntent(contentPendingIntent)
            .build()
    }

    /**
     * Construye la notificación que se usará en segundo plano (para startForeground).
     */
    fun buildForegroundNotification(context: Context, title: String, content: String) =
        buildNotification(context, title, content, true)

    /**
     * Actualiza la notificación en cualquier momento (por ejemplo, "quedan X minutos").
     */
    fun updateNotification(
        context: Context, title: String, content: String,
        isOngoing: Boolean
    ) {
        val n = buildNotification(context, title, content, isOngoing)
        NotificationManagerCompat.from(context).notify(TIMER_NOTIFICATION_ID, n)
    }

    /**
     * Cancela todas las notificaciones activas (útil al detener el temporizador).
     */
    fun cancelAll(context: Context) {
        NotificationManagerCompat.from(context).cancelAll()
    }
}