package com.bungaedu.donotbelate.utils

import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import com.bungaedu.donotbelate.service.DuranteService

fun isServiceRunning(context: Context): Boolean {
    val manager = context.getSystemService(ACTIVITY_SERVICE) as android.app.ActivityManager
    return manager.getRunningServices(Int.MAX_VALUE).any {
        it.service.className == DuranteService::class.java.name
    }
}