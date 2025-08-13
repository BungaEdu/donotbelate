package com.bungaedu.donotbelate.logic

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bungaedu.donotbelate.MainActivity
import com.bungaedu.donotbelate.R

@SuppressLint("MissingPermission") // Recuerda pedir el permiso de notificaciones en Android 13+
object NotificationHelper {

    private const val CHANNEL_ID = "durante_timer_channel"
    private const val CHANNEL_NAME = "Durante Timer"
    private const val CHANNEL_DESCRIPTION = "Avisos periódicos del temporizador"
    private const val TIMER_NOTIFICATION_ID = 100

    fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = CHANNEL_DESCRIPTION
        }

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    /**
     * Construye la notificación que se usará en segundo plano (para startForeground).
     */
    fun buildForegroundNotification(
        context: Context,
        title: String,
        content: String
    ): Notification = buildNotification(
        context = context,
        title = title,
        content = content,
        isOngoing = true
    )

    /**
     * Construye una notificación completa con botón para cancelar (detener el temporizador).
     */
    private fun buildNotification(
        context: Context,
        title: String,
        content: String,
        isOngoing: Boolean
    ): Notification {
        // Abrir la app al tocar la notificación
        val openIntent = Intent(context, MainActivity::class.java)
        val openPendingIntent = PendingIntent.getActivity(
            context, 0, openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Botón "Cancelar"
        val stopIntent = Intent(context, StopTimerReceiver::class.java)
        val stopPendingIntent = PendingIntent.getBroadcast(
            context, 0, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_foreground) // Pon aquí tu icono real
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(isOngoing)         // ← ¿se puede deslizar?
            .setAutoCancel(!isOngoing)     // ← autocancel si no es persistente
            .setContentIntent(openPendingIntent)
            .addAction(R.mipmap.ic_launcher_foreground, "Cancelar", stopPendingIntent)
            .build()
    }

    /**
     * Actualiza la notificación en cualquier momento (por ejemplo, "quedan X minutos").
     */
    fun updateNotification(
        context: Context,
        title: String,
        content: String,
        isOngoing: Boolean = true
    ) {
        val updated = buildNotification(context, title, content, isOngoing)
        NotificationManagerCompat.from(context).notify(TIMER_NOTIFICATION_ID, updated)
    }

    /**
     * Cancela todas las notificaciones activas (útil al detener el temporizador).
     */
    fun cancelAll(context: Context) {
        NotificationManagerCompat.from(context).cancelAll()
    }
}