package com.bungaedu.donotbelate.di

import com.bungaedu.donotbelate.data.repository.TimerConfigRepository
import com.bungaedu.donotbelate.data.repository.TimerPrefsRepository
import com.bungaedu.donotbelate.logic.TtsManager
import com.bungaedu.donotbelate.presentation.viewmodel.DeviceSettingsViewModel
import com.bungaedu.donotbelate.presentation.viewmodel.DuranteViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Singleton de TTS
    single { TtsManager() }

    // Repo: interface → implementación
    single<TimerConfigRepository> { TimerPrefsRepository(androidContext()) }

    // ViewModels
    viewModel { DuranteViewModel(get()) }       // inyecta TimerConfigRepository
    viewModel { DeviceSettingsViewModel(get()) }
}