package com.bungaedu.donotbelate.data.repository

import kotlinx.coroutines.flow.Flow
import java.time.LocalTime

interface TimerStateRepository {
    //////////////////////////////// AVISAR DURANTE ////////////////////////////////
    suspend fun getAvisarDurante(): Int?
    suspend fun setAvisarDurante(v: Int)
    fun avisarDuranteFlow(): Flow<Int?>

    //////////////////////////////// AVISAR HASTA ////////////////////////////////
    suspend fun getAvisarHasta(): Int?
    suspend fun setAvisarHasta(v: Int)
    fun avisarHastaFlow(): Flow<Int?>

    //////////////////////////////// DURANTE ////////////////////////////////
    suspend fun getDurante(): Int?
    suspend fun setDurante(v: Int)
    fun duranteFlow(): Flow<Int?>

    //////////////////////////////// RunningService ////////////////////////////////
    suspend fun setIsRunningServiceDurante(v: Boolean)
    fun isRunningServiceDuranteFlow(): Flow<Boolean>

    //////////////////////////////// RunningService ////////////////////////////////
    suspend fun setIsRunningServiceHasta(v: Boolean)
    fun isRunningServiceHastaFlow(): Flow<Boolean>

    //////////////////////////////// MinutosRestantes ////////////////////////////////
    suspend fun setMinutosRestantes(v: Int)
    fun minutosRestantesFlow(): Flow<Int?>

    //////////////////////////////// HoraObjetivo ////////////////////////////////
    suspend fun getHoraObjetivo(): LocalTime?
    suspend fun setHoraObjetivo(v: LocalTime)
    fun horaObjetivoFlow(): Flow<LocalTime?>
}