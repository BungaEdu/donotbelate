// TimerPrefs.kt
package com.bungaedu.donotbelate.data

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore("timer_prefs")

object TimerPrefs {
    private const val TAG = "*TimerPrefs"

    private val KEY_AVISAR = intPreferencesKey("avisar_cada")
    private val KEY_DURANTE = intPreferencesKey("durante_min")

    fun avisarFlow(context: Context) =
        context.dataStore.data.map { prefs ->
            val value = prefs[KEY_AVISAR]
            Log.d(TAG, "avisarFlow emitió valor: $value")
            value
        }

    fun duranteFlow(context: Context) =
        context.dataStore.data.map { prefs ->
            val value = prefs[KEY_DURANTE]
            Log.d(TAG, "duranteFlow emitió valor: $value")
            value
        }

    suspend fun setAvisar(context: Context, v: Int) {
        Log.d(TAG, "Guardando avisar_cada = $v")
        context.dataStore.edit {
            it[KEY_AVISAR] = v
        }
        Log.d(TAG, "Avisar guardado correctamente")
    }

    suspend fun setDurante(context: Context, v: Int) {
        Log.d(TAG, "Guardando durante_min = $v")
        context.dataStore.edit {
            it[KEY_DURANTE] = v
        }
        Log.d(TAG, "Durante guardado correctamente")
    }

    // (Opcional) lecturas puntuales
    suspend fun getAvisarOnce(context: Context): Int? {
        val value = context.dataStore.data.first()[KEY_AVISAR]
        Log.d(TAG, "getAvisarOnce → $value")
        return value
    }

    suspend fun getDuranteOnce(context: Context): Int? {
        val value = context.dataStore.data.first()[KEY_DURANTE]
        Log.d(TAG, "getDuranteOnce → $value")
        return value
    }
}