package com.bungaedu.donotbelate.data.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class TimerPrefsRepository(
    private val dataStore: DataStore<Preferences>
) : TimerConfigRepository {

    companion object {
        private const val TAG = "*TimerPrefsRepo"
        private val KEY_AVISAR = intPreferencesKey("avisar_cada")
        private val KEY_DURANTE = intPreferencesKey("durante_min")
        private val KEY_IS_RUNNING = booleanPreferencesKey("is_running")
        private val KEY_MINUTOS_RESTANTES = intPreferencesKey("minutos_restantes")
    }

    override suspend fun getAvisar() =
        dataStore.data.first()[KEY_AVISAR].also { Log.d(TAG, "getAvisar → $it") }

    override suspend fun getDurante() =
        dataStore.data.first()[KEY_DURANTE].also { Log.d(TAG, "getDurante → $it") }

    override fun avisarFlow() =
        dataStore.data.map { prefs ->
            val v = prefs[KEY_AVISAR]
            Log.d(TAG, "avisarFlow emitió $v")
            v
        }

    override fun duranteFlow() =
        dataStore.data.map { prefs ->
            val v = prefs[KEY_DURANTE]
            Log.d(TAG, "duranteFlow emitió $v")
            v
        }

    override suspend fun setAvisar(v: Int) {
        Log.d(TAG, "setAvisar = $v")
        dataStore.edit { it[KEY_AVISAR] = v }
    }

    override suspend fun setDurante(v: Int) {
        Log.d(TAG, "setDurante = $v")
        dataStore.edit { it[KEY_DURANTE] = v }
    }

    override fun isRunningFlow() =
        dataStore.data.map { it[KEY_IS_RUNNING] ?: false }

    override fun minutosRestantesFlow() =
        dataStore.data.map { prefs -> prefs[KEY_MINUTOS_RESTANTES] }

    override suspend fun setIsRunning(v: Boolean) {
        Log.d(TAG, "setIsRunning = $v")
        dataStore.edit { it[KEY_IS_RUNNING] = v }
    }

    override suspend fun setMinutosRestantes(v: Int) {
        Log.d(TAG, "setMinutosRestantes = $v")
        dataStore.edit { it[KEY_MINUTOS_RESTANTES] = v }
    }
}