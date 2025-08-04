package com.example.donotbelate_v3

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.donotbelate_v3.logic.NotificationHelper
import com.example.donotbelate_v3.presentation.screens.MainScreen
import com.example.donotbelate_v3.ui.theme.MyAppTheme

class MainActivity : ComponentActivity() {
    private val TAG = "*MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        NotificationHelper.createNotificationChannel(this)
        setContent {
            MyAppTheme {
                MainScreen()
            }
        }
    }
}