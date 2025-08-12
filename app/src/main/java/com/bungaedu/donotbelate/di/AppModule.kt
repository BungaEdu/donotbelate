package com.bungaedu.donotbelate.di

import com.bungaedu.donotbelate.logic.TtsManager
import org.koin.dsl.module

val appModule = module {
    single { TtsManager() }
//TODO hay que meter bien con DI los viewmodels
//viewModel { DuranteViewModel() }
}