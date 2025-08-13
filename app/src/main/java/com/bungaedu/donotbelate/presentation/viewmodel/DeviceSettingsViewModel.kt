package com.bungaedu.donotbelate.presentation.viewmodel

import android.app.Application
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bungaedu.donotbelate.device.NotificationPermissionsMonitor
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DeviceSettingsViewModel(application: Application) : AndroidViewModel(application) {

    sealed interface UiEvent {
        data object ShowNotificationsBottomSheet : UiEvent
        data object RequestRuntimePermission : UiEvent
        data object OpenAppSettings : UiEvent
    }

    private val _events = MutableSharedFlow<UiEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<UiEvent> = _events.asSharedFlow()

    private val _notificationsAllowed = MutableStateFlow(false)
    val notificationsAllowed: StateFlow<Boolean> = _notificationsAllowed.asStateFlow()

    private var observeJob: Job? = null

    init {
        startObservingNotifications()
    }

    /**
     * Empieza a observar si las notificaciones están permitidas en el dispositivo.
     */
    private fun startObservingNotifications() {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            NotificationPermissionsMonitor
                .observeNotificationsAllowed(getApplication())
                .distinctUntilChanged()
                .collect { _notificationsAllowed.value = it }
        }
    }

    /**
     * Acción al pulsar "Empezar".
     * Si las notificaciones no están permitidas, lanza el evento para mostrar el bottom sheet.
     */
    fun onStartPressed() {
        if (!_notificationsAllowed.value) {
            _events.tryEmit(UiEvent.ShowNotificationsBottomSheet)
        }
    }

    /**
     * Acción cuando el usuario confirma en el bottom sheet.
     * Decide si pedir permiso en tiempo de ejecución o abrir ajustes.
     */
    fun onPermissionBottomSheetConfirmed() {
        if (shouldRequestRuntimePermission()) {
            _events.tryEmit(UiEvent.RequestRuntimePermission)
        } else {
            _events.tryEmit(UiEvent.OpenAppSettings)
        }
    }

    /**
     * Devuelve true si debemos pedir permiso de notificaciones en tiempo de ejecución.
     */
    fun shouldRequestRuntimePermission(): Boolean =
        Build.VERSION.SDK_INT >= 33

    /**
     * Devuelve true si debemos enviar al usuario a ajustes.
     */
    fun shouldRedirectToSettings(): Boolean =
        !_notificationsAllowed.value

    /**
     * Intent para abrir la configuración de notificaciones de esta app.
     */
    fun buildAppNotificationSettingsIntent(): Intent =
        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, getApplication<Application>().packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

    override fun onCleared() {
        super.onCleared()
        observeJob?.cancel()
    }
}
