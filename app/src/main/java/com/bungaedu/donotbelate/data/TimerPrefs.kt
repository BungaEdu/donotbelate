// TimerPrefs.kt
package com.bungaedu.donotbelate.data

import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore("timer_prefs")

object TimerPrefs {
    private val KEY_AVISAR = intPreferencesKey("avisar_cada")
    private val KEY_DURANTE = intPreferencesKey("durante_min")

    fun avisarFlow(context: Context) =
        context.dataStore.data.map { it[KEY_AVISAR] }

    fun duranteFlow(context: Context) =
        context.dataStore.data.map { it[KEY_DURANTE] }

    suspend fun setAvisar(context: Context, v: Int) {
        context.dataStore.edit { it[KEY_AVISAR] = v }
    }

    suspend fun setDurante(context: Context, v: Int) {
        context.dataStore.edit { it[KEY_DURANTE] = v }
    }

    // (Opcional) lecturas puntuales
    suspend fun getAvisarOnce(context: Context) =
        context.dataStore.data.first()[KEY_AVISAR]

    suspend fun getDuranteOnce(context: Context) =
        context.dataStore.data.first()[KEY_DURANTE]
}
