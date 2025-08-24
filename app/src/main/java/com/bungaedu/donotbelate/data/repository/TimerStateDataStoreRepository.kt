package com.bungaedu.donotbelate.data.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalTime

class TimerStateDataStoreRepository(
    private val dataStore: DataStore<Preferences>
) : TimerStateRepository {

    companion object {
        private const val TAG = "*TimerPrefsRepo"

        // AVISAR
        private val KEY_AVISAR_DURANTE = intPreferencesKey("avisar_durante")
        private val KEY_AVISAR_HASTA = intPreferencesKey("avisar_hasta")

        // DURANTE
        private val KEY_DURANTE = intPreferencesKey("durante_min")

        // RUNNING FLAGS
        private val KEY_IS_RUNNING_DURANTE = booleanPreferencesKey("is_running_durante")
        private val KEY_IS_RUNNING_HASTA = booleanPreferencesKey("is_running_hasta")

        // RESTO
        private val KEY_MINUTOS_RESTANTES = intPreferencesKey("minutos_restantes")
        private val KEY_HORA_OBJETIVO = intPreferencesKey("hora_objetivo") // minutos desde medianoche
    }

    //////////////////////////////// AVISAR DURANTE ////////////////////////////////
    override suspend fun getAvisarDurante() =
        dataStore.data.first()[KEY_AVISAR_DURANTE].also { Log.d(TAG, "getAvisarDurante → $it") }

    override suspend fun setAvisarDurante(v: Int) {
        Log.d(TAG, "setAvisarDurante = $v")
        dataStore.edit { it[KEY_AVISAR_DURANTE] = v }
    }

    override fun avisarDuranteFlow() =
        dataStore.data.map { prefs ->
            val v = prefs[KEY_AVISAR_DURANTE]
            Log.d(TAG, "avisarDuranteFlow emitió $v")
            v
        }

    //////////////////////////////// AVISAR HASTA ////////////////////////////////
    override suspend fun getAvisarHasta() =
        dataStore.data.first()[KEY_AVISAR_HASTA].also { Log.d(TAG, "getAvisarHasta → $it") }

    override suspend fun setAvisarHasta(v: Int) {
        Log.d(TAG, "setAvisarHasta = $v")
        dataStore.edit { it[KEY_AVISAR_HASTA] = v }
    }

    override fun avisarHastaFlow() =
        dataStore.data.map { prefs ->
            val v = prefs[KEY_AVISAR_HASTA]
            Log.d(TAG, "avisarHastaFlow emitió $v")
            v
        }

    //////////////////////////////// DURANTE ////////////////////////////////
    override suspend fun getDurante() =
        dataStore.data.first()[KEY_DURANTE].also { Log.d(TAG, "getDurante → $it") }

    override suspend fun setDurante(v: Int) {
        Log.d(TAG, "setDurante = $v")
        dataStore.edit { it[KEY_DURANTE] = v }
    }

    override fun duranteFlow() =
        dataStore.data.map { prefs ->
            val v = prefs[KEY_DURANTE]
            Log.d(TAG, "duranteFlow emitió $v")
            v
        }

    //////////////////////////////// Running Durante ////////////////////////////////
    override suspend fun setIsRunningServiceDurante(v: Boolean) {
        Log.d(TAG, "setIsRunningServiceDurante = $v")
        dataStore.edit { it[KEY_IS_RUNNING_DURANTE] = v }
    }

    override fun isRunningServiceDuranteFlow() =
        dataStore.data.map { prefs ->
            val value = prefs[KEY_IS_RUNNING_DURANTE] ?: false
            Log.d(TAG, "isRunningServiceDuranteFlow emitió: $value")
            value
        }

    //////////////////////////////// Running Hasta ////////////////////////////////
    override suspend fun setIsRunningServiceHasta(v: Boolean) {
        Log.d(TAG, "setIsRunningServiceHasta = $v")
        dataStore.edit { it[KEY_IS_RUNNING_HASTA] = v }
    }

    override fun isRunningServiceHastaFlow() =
        dataStore.data.map { prefs ->
            val value = prefs[KEY_IS_RUNNING_HASTA] ?: false
            Log.d(TAG, "isRunningServiceHastaFlow emitió: $value")
            value
        }

    //////////////////////////////// MinutosRestantes ////////////////////////////////
    override suspend fun setMinutosRestantes(v: Int) {
        Log.d(TAG, "setMinutosRestantes = $v")
        dataStore.edit { it[KEY_MINUTOS_RESTANTES] = v }
    }

    override fun minutosRestantesFlow() =
        dataStore.data.map { prefs ->
            val value = prefs[KEY_MINUTOS_RESTANTES]
            Log.d(TAG, "minutosRestantesFlow emitió: $value")
            value
        }

    //////////////////////////////// HoraObjetivo ////////////////////////////////
    override suspend fun getHoraObjetivo(): LocalTime? {
        val minutos = dataStore.data.first()[KEY_HORA_OBJETIVO]
        val value = minutos?.let { LocalTime.ofSecondOfDay(it.toLong() * 60) }
        Log.d(TAG, "getHoraObjetivo → $value")
        return value
    }

    override suspend fun setHoraObjetivo(v: LocalTime) {
        val minutos = v.hour * 60 + v.minute
        Log.d(TAG, "setHoraObjetivo = $v ($minutos minutos desde medianoche)")
        dataStore.edit { it[KEY_HORA_OBJETIVO] = minutos }
    }

    override fun horaObjetivoFlow() =
        dataStore.data.map { prefs ->
            val minutos = prefs[KEY_HORA_OBJETIVO]
            val value = minutos?.let { LocalTime.ofSecondOfDay(it.toLong() * 60) }
            Log.d(TAG, "horaObjetivoFlow emitió: $value")
            value
        }
}