package com.bungaedu.donotbelate.app

import android.app.Application
import com.bungaedu.donotbelate.di.appModule
import com.google.firebase.FirebaseApp
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class DoNotBeLateApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@DoNotBeLateApplication)
            modules(
                appModule
            )
        }

        FirebaseApp.initializeApp(this)
    }
}
