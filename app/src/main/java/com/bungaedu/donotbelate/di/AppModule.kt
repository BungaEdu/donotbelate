package com.bungaedu.donotbelate.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.preferencesDataStoreFile
import com.bungaedu.donotbelate.data.repository.TimerConfigRepository
import com.bungaedu.donotbelate.data.repository.TimerPrefsRepository
import com.bungaedu.donotbelate.logic.TtsManager
import com.bungaedu.donotbelate.presentation.viewmodel.DeviceSettingsViewModel
import com.bungaedu.donotbelate.presentation.viewmodel.DuranteViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // ✅ DataStore global única (sin registrar Context como bean)
    single<DataStore<Preferences>> {
        PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
            produceFile = { androidContext().preferencesDataStoreFile("timer_prefs.preferences_pb") }
        )
    }

    // ✅ Repo → implementación con la DataStore inyectada
    single<TimerConfigRepository> { TimerPrefsRepository(get()) }

    // ✅ TTS singleton (se inicializa en el Service)
    single { TtsManager() }

    // ✅ ViewModels (ajusta según tu constructor real)
    viewModel { DuranteViewModel(get()) }        // TimerConfigRepository
    viewModel { DeviceSettingsViewModel(get()) }
}