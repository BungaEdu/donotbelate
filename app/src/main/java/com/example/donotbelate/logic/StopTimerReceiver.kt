package com.example.donotbelate.logic

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.donotbelate.service.DuranteService

class StopTimerReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        TimerHolder.viewModel?.stopTimer()
        DuranteService.stop(context)
    }
}