package com.example.donotbelate

import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.donotbelate.logic.NotificationHelper
import com.example.donotbelate.logic.TtsManager
import com.example.donotbelate.presentation.screens.MainScreen
import com.example.donotbelate.ui.theme.MyAppTheme
import org.koin.android.ext.android.inject
import com.google.android.gms.tasks.Task
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE
import com.google.android.play.core.install.model.UpdateAvailability

class MainActivity : ComponentActivity() {
    private val TAG = "*MainActivity"
    private val ttsManager: TtsManager by inject()
    private val updateManager by lazy { AppUpdateManagerFactory.create(this) }
    private val REQUEST_CODE_UPDATE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "onCreate")

        // Crear canal de notificación y inicializar TTS
        NotificationHelper.createNotificationChannel(this)
        ttsManager.init(applicationContext)

        // Cargar la UI Compose
        setContent {
            MyAppTheme {
                MainScreen()
            }
        }
        checkForUpdates()
    }

    private fun checkForUpdates() {
        val appUpdateInfoTask: Task<AppUpdateInfo> = updateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                appUpdateInfo.isUpdateTypeAllowed(IMMEDIATE)
            ) {
                try {
                    updateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        IMMEDIATE,
                        this,
                        REQUEST_CODE_UPDATE
                    )
                } catch (e: IntentSender.SendIntentException) {
                    e.printStackTrace()
                }
            }
        }
    }
}