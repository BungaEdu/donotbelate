package com.example.donotbelate_v3.logic

import android.content.Context

object TimerStopper {
    private var stopCallback: (() -> Unit)? = null

    fun registerStopCallback(callback: () -> Unit) {
        stopCallback = callback
    }

    fun stopTimerExternally(context: Context) {
        stopCallback?.invoke()
        NotificationHelper.cancelAll(context)
    }

    fun reset() {
        stopCallback = null
    }
}
