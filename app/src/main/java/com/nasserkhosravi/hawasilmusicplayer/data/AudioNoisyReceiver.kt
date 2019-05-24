package com.nasserkhosravi.hawasilmusicplayer.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager

class AudioNoisyReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY) {
            QueueBrain.pause()
        }
    }

    companion object {
        fun createIntentFilter(): IntentFilter {
            return IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        }

        fun tag(): String {
            return AudioNoisyReceiver::class.java.simpleName
        }
    }
}