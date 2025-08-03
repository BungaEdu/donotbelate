package com.example.donotbelate_v3.logic

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.donotbelate_v3.MainActivity
import com.example.donotbelate_v3.R

@SuppressLint("MissingPermission")//TODO hayq ue pedir permiso notificaciones
object NotificationHelper {

    private const val CHANNEL_ID = "durante_timer_channel"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Durante Timer"
            val descriptionText = "Avisos periódicos del temporizador"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendNotification(
        context: Context,
        title: String,
        content: String,
        isPersistent: Boolean = true, // ⛔ por defecto no deslizable
        id: Int = 100
    ) {
        // Intent para abrir la app (MainActivity)
        val openIntent = Intent(context, MainActivity::class.java)
        val openPendingIntent = PendingIntent.getActivity(
            context,
            0,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Intent para detener el timer desde la notificación (❌)
        val stopIntent = Intent(context, StopTimerReceiver::class.java)
        val stopPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(isPersistent) // ← ¿se puede deslizar?
            .setAutoCancel(!isPersistent) // ← si no es persistente, que se autocancele
            .setContentIntent(openPendingIntent)
            .addAction(R.drawable.ic_launcher_foreground, "Cancelar", stopPendingIntent)

        with(NotificationManagerCompat.from(context)) {
            notify(id, builder.build())
        }
    }



    /*fun sendFinalNotification(context: Context, id: Int = 999) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // o tu icono
            .setContentTitle("Temporizador finalizado")
            .setContentText("Se ha acabado el tiempo, no llegues tarde")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        with(NotificationManagerCompat.from(context)) {
            notify(id, builder.build())
        }
    }*/

    fun cancelAll(context: Context) {
        NotificationManagerCompat.from(context).cancelAll()
    }

}
