package com.bungaedu.donotbelate.di

import com.bungaedu.donotbelate.logic.TtsManager
import com.bungaedu.donotbelate.presentation.viewmodel.DeviceSettingsViewModel
import com.bungaedu.donotbelate.presentation.viewmodel.DuranteViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { TtsManager() }
    viewModel { DuranteViewModel() }
    viewModel {
        DeviceSettingsViewModel(get())
    }
}