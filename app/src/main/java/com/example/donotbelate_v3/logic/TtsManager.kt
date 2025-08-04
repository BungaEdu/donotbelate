package com.example.donotbelate_v3.logic

import android.content.Context
import android.content.SharedPreferences
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.widget.Toast
import java.util.*
import kotlin.apply
import kotlin.let

class TtsManager() {
    companion object {
        private const val TAG = "*TtsManager"
        const val KEY_MUTE_DURING_CALL = "config_iniciales_mute_duringcall"
        const val KEY_OUTPUT_ALWAYS_PHONE = "config_iniciales_output_always_phone"
        val params: Bundle = Bundle().apply {
            putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1f)
        }
    }
    fun isReady(): Boolean = textToSpeech != null
    private var textToSpeech: TextToSpeech? = null
    private var prefs: SharedPreferences? = null
    private var muteDuringCall: Boolean = false
    private var outputAlwaysPhone: Boolean = false
    private var audioManager: AudioManager? = null
    private var focusRequest: Int = 0
    private var audioFocusRequest: AudioFocusRequest? = null
    private val utteranceId = "id_activateTTS"

    fun init(context: Context, onReady: (() -> Unit)? = null) {
        Log.w(TAG, "Creo un TtsManager")
        // Si ya está inicializado, no volver a instanciar
        if (textToSpeech != null) {
            onReady?.invoke()
            return
        }

        Log.i(TAG, "Inicializando TTS...")

        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        textToSpeech = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                configureLanguage(context)
                textToSpeech?.setSpeechRate(0.9f)
                textToSpeech?.setPitch(0.9f)

                textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String) {}

                    override fun onDone(utteranceId: String) {
                        abandonAudioFocus()
                    }

                    override fun onError(utteranceId: String) {
                        reportCrash(TAG, "Error en TTS")
                    }
                })

                onReady?.invoke()

            } else {
                reportCrash(TAG, "ERROR al inicializar el TTS")
            }
        }

        createFocusRequest()
    }

    private fun configureLanguage(context: Context) {
        try {
            val locale = getLocaleForLanguage(Locale.getDefault().language)
            val result = textToSpeech?.setLanguage(locale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(context, "Lenguaje no soportado", Toast.LENGTH_LONG).show()
            }
        } catch (e: NullPointerException) {
            reportCrash(TAG, "No se ha podido recoger el idioma correctamente")
        } catch (e: Exception) {
            reportCrash(TAG, "Error inesperado al intentar coger el lenguaje del dispositivo")
        }
    }

    private fun createFocusRequest() {
        audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
            .setAudioAttributes(AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                .build())
            .setAcceptsDelayedFocusGain(true)
            .setOnAudioFocusChangeListener {
                // No hacemos nada cuando perdemos el enfoque
            }
            .build()
    }

    fun speak(textToRead: String) {
        muteDuringCall = prefs?.getBoolean(KEY_MUTE_DURING_CALL, false) ?: false
        outputAlwaysPhone = prefs?.getBoolean(KEY_OUTPUT_ALWAYS_PHONE, false) ?: false

        focusRequest = audioManager?.requestAudioFocus(audioFocusRequest ?: return) ?: 0

        val builder = AudioAttributes.Builder()
        if (outputAlwaysPhone) {
            Log.i("VER", "Estoy en outputAlwaysPhone TRUE")
            builder.setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                .setLegacyStreamType(AudioManager.STREAM_ALARM)
        } else {
            Log.i("VER", "Estoy en outputAlwaysPhone FALSE")
            builder.setLegacyStreamType(AudioManager.STREAM_MUSIC)
        }

        textToSpeech?.setAudioAttributes(builder.build())

        if (muteDuringCall) {
            if (focusRequest == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                Log.i(TAG, "Audio focus A: $focusRequest")
                val result = textToSpeech?.speak(textToRead, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
                handleTextToSpeechResult(result ?: TextToSpeech.ERROR, textToRead)
            }
        } else {
            if (focusRequest == AudioManager.AUDIOFOCUS_REQUEST_GRANTED || focusRequest == AudioManager.AUDIOFOCUS_REQUEST_DELAYED || focusRequest == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
                Log.i(TAG, "Audio focus B: $focusRequest")
                val result = textToSpeech?.speak("¡ATENCIÓN! $textToRead", TextToSpeech.QUEUE_FLUSH, params, utteranceId)
                handleTextToSpeechResult(result ?: TextToSpeech.ERROR, textToRead)
            }
        }
    }

    private fun handleTextToSpeechResult(result: Int, text: String) {
        if (result == TextToSpeech.ERROR) {
            reportCrash(TAG, "Error al intentar usar TextToSpeech con el texto: $text")
        } else {
            Log.i(TAG, "Reproduzco: $text")
        }
    }

    private fun abandonAudioFocus() {
        if (audioFocusRequest != null) {
            audioManager?.abandonAudioFocusRequest(audioFocusRequest!!)
        } else if (audioManager != null) {
            audioManager?.abandonAudioFocus(null)
        }
        Log.i(TAG, "Audio focus abandonado")
    }

    fun off() {
        abandonAudioFocus()
        textToSpeech?.let {
            try {
                it.stop()
                it.shutdown()
                Log.i(TAG, "Tts manager off")
            } catch (e: Exception) {
                reportCrash(TAG, "Error occurred while shutting down TextToSpeech")
            }
        }
        textToSpeech = null
    }

    private fun getLocaleForLanguage(language: String): Locale {
        // Implementación del método para obtener el Locale según el idioma
        return Locale.getDefault() // Ejemplo básico, deberías ajustarlo a tu lógica
    }

    private fun reportCrash(tag: String, message: String) {
        // Implementación del método para reportar errores
        Log.e(tag, message)
    }

    interface TtsCompletionListener {
        fun onTtsComplete()
    }
}