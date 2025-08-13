package com.bungaedu.donotbelate.device

import android.Manifest
import android.app.AppOpsManager
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

object NotificationPermissionsMonitor {

    // Constante para compatibilidad si compileSdk < 33
    private const val OPSTR_POST_NOTIFICATION = "android:post_notification"

    fun observeNotificationsAllowed(context: Context): Flow<Boolean> = callbackFlow {
        val appCtx = context.applicationContext

        fun currentAllowed(): Boolean {
            val toggleAllowed = NotificationManagerCompat.from(appCtx).areNotificationsEnabled()
            return if (Build.VERSION.SDK_INT >= 33) {
                val permGranted = ContextCompat.checkSelfPermission(
                    appCtx, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
                toggleAllowed && permGranted
            } else {
                toggleAllowed
            }
        }

        fun emitCurrent() {
            trySend(currentAllowed())
        }

        // 1) Cambios del toggle “Permitir notificaciones” (API 28+)
        val blockStateReceiver: BroadcastReceiver? =
            if (Build.VERSION.SDK_INT >= 28) object : BroadcastReceiver() {
                override fun onReceive(c: Context?, intent: Intent?) {
                    emitCurrent()
                }
            } else null

        val blockStateFilter: IntentFilter? =
            if (Build.VERSION.SDK_INT >= 28)
                IntentFilter(NotificationManager.ACTION_APP_BLOCK_STATE_CHANGED)
            else null

        if (blockStateReceiver != null && blockStateFilter != null) {
            if (Build.VERSION.SDK_INT >= 33) {
                appCtx.registerReceiver(
                    blockStateReceiver,
                    blockStateFilter,
                    Context.RECEIVER_NOT_EXPORTED
                )
            } else {
                @Suppress("DEPRECATION")
                ContextCompat.registerReceiver(
                    appCtx,
                    blockStateReceiver,
                    blockStateFilter,
                    ContextCompat.RECEIVER_EXPORTED
                )
            }
        }

        // 2) Cambios en AppOps para POST_NOTIFICATION
        val appOps = appCtx.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val appOpsListener = AppOpsManager.OnOpChangedListener { op, pkg ->
            if (op == OPSTR_POST_NOTIFICATION && pkg == appCtx.packageName) {
                emitCurrent()
            }
        }
        @Suppress("DEPRECATION")
        appOps.startWatchingMode(
            OPSTR_POST_NOTIFICATION,
            appCtx.packageName,
            appOpsListener
        )

        // Estado inicial
        emitCurrent()

        awaitClose {
            if (blockStateReceiver != null) runCatching {
                appCtx.unregisterReceiver(
                    blockStateReceiver
                )
            }
            runCatching { appOps.stopWatchingMode(appOpsListener) }
        }
    }
}