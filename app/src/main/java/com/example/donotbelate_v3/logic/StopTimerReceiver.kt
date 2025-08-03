package com.example.donotbelate_v3.logic

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class StopTimerReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        TimerStopper.stopTimerExternally(context)
        NotificationHelper.cancelAll(context)
    }
}