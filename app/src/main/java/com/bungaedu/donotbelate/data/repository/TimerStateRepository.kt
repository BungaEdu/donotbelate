package com.bungaedu.donotbelate.data.repository

import kotlinx.coroutines.flow.Flow

interface TimerStateRepository {
    //////////////////////////////// AVISAR ////////////////////////////////
    suspend fun getAvisar(): Int?
    suspend fun setAvisar(v: Int)
    fun avisarFlow(): Flow<Int?>

    //////////////////////////////// DURANTE ////////////////////////////////
    suspend fun getDurante(): Int?
    suspend fun setDurante(v: Int)
    fun duranteFlow(): Flow<Int?>

    //////////////////////////////// RunningService ////////////////////////////////
    suspend fun setIsRunning(v: Boolean)
    fun isRunningFlow(): Flow<Boolean>

    //////////////////////////////// MinutosRestantes ////////////////////////////////
    suspend fun setMinutosRestantes(v: Int)
    fun minutosRestantesFlow(): Flow<Int?>
}