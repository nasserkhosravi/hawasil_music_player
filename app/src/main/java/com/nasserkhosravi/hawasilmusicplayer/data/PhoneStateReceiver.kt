package com.nasserkhosravi.hawasilmusicplayer.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.telephony.TelephonyManager
import android.util.Log

class PhoneStateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent!!.action == "android.intent.action.PHONE_STATE") {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            when (state) {
                TelephonyManager.EXTRA_STATE_RINGING -> {
                    Log.d(tag(), "incoming call: ")
                }
                TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                    Log.d(tag(), "Outgoing call : ")
                }
                TelephonyManager.EXTRA_STATE_IDLE -> {
                    Log.d(tag(), "Call ended: ")
                }
            }
        }
    }


    companion object {

        fun tag(): String {
            return PhoneStateReceiver::class.java.simpleName
        }

        fun createIntentFilter(): IntentFilter {
            val intentFilter = IntentFilter()
            intentFilter.addAction("android.intent.action.PHONE_STATE")
            intentFilter.addAction("android.intent.action.NEW_OUTGOING_CALL")
            return intentFilter
        }
    }
}