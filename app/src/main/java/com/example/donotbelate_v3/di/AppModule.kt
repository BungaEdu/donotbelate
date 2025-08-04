package com.example.donotbelate_v3.di

import com.example.donotbelate_v3.logic.TtsManager
import com.example.donotbelate_v3.presentation.viewmodels.DuranteViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { TtsManager() }
//TODO hay que meter bien con DI los viewmodels
//viewModel { DuranteViewModel() }
}