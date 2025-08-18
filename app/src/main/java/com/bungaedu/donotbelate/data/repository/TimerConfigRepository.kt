package com.bungaedu.donotbelate.data.repository

import kotlinx.coroutines.flow.Flow

interface TimerConfigRepository {
    suspend fun getAvisar(): Int?
    suspend fun getDurante(): Int?
    fun avisarFlow(): Flow<Int?>
    fun duranteFlow(): Flow<Int?>
    suspend fun setAvisar(v: Int)
    suspend fun setDurante(v: Int)

    fun isRunningFlow(): Flow<Boolean>
    fun minutosRestantesFlow(): Flow<Int?>
    suspend fun setIsRunning(v: Boolean)
    suspend fun setMinutosRestantes(v: Int)
}