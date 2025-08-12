package com.bungaedu.donotbelate.logic

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bungaedu.donotbelate.service.DuranteService

class StopTimerReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        TimerHolder.viewModel?.stopTimer()
        DuranteService.stop(context)
    }
}