package com.bungaedu.donotbelate.utils

import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.os.Build
import com.bungaedu.donotbelate.service.DuranteService

fun isServiceRunning(context: Context): Boolean {
    val manager = context.getSystemService(ACTIVITY_SERVICE) as android.app.ActivityManager
    return manager.getRunningServices(Int.MAX_VALUE).any {
        it.service.className == DuranteService::class.java.name
    }
}

// Función para obtener el versionName desde el contexto
fun getVersionName(context: Context): String {
    return try {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        packageInfo.versionName ?: "N/A"
    } catch (e: Exception) {
        "N/A"
    }
}

/**
 * Obtiene el versionCode de la aplicación.
 *
 * @param context Contexto de la aplicación.
 * @return El versionCode como un número entero o -1 si hay un error.
 */
fun getVersionCode(context: Context): Int {
    return try {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.longVersionCode.toInt() // Convertir longVersionCode a Int
        } else {
            @Suppress("DEPRECATION")
            packageInfo.versionCode
        }
    } catch (e: Exception) {
        -1
    }
}