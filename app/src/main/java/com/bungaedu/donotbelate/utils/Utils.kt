package com.bungaedu.donotbelate.utils

import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.os.Build
import android.util.Log
import com.bungaedu.donotbelate.service.DuranteService
import com.google.firebase.crashlytics.FirebaseCrashlytics

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

/**
 * Méto.do que registra un Log de error y reporta la traza a través de Firebase Crashlytics.
 * Este méto.do se utiliza principalmente en bloques try-catch para mejorar la visibilidad
 * de errores en la aplicación.
 *
 * @param className Nombre de la clase donde ocurre el error.
 * @param errorMessage Mensaje de error o descripción de la excepción.
 */
fun reportCrash(className: String, errorMessage: String) {
    try {
        // Log de error en consola
        Log.e(className, errorMessage)

        // Crear una excepción personalizada con el mensaje
        val exception = Exception("Clase: $className - Error: $errorMessage")

        // Registrar información adicional en Crashlytics
        val crashlytics = FirebaseCrashlytics.getInstance()
        crashlytics.setCustomKey("Clase", className) // Clave personalizada con la clase
        crashlytics.setCustomKey("Mensaje de error", errorMessage) // Clave personalizada con el mensaje

        // Enviar la excepción a Crashlytics
        crashlytics.recordException(exception)
    } catch (e: Exception) {
        // Manejo de errores durante el reporte
        Log.e("reportCrash", "Error al reportar la excepción: ${e.message}")
    }
}