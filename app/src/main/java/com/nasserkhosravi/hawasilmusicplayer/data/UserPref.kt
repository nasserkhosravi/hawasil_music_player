package com.nasserkhosravi.hawasilmusicplayer.data

import android.content.Context
import android.content.SharedPreferences
import com.nasserkhosravi.appcomponent.AppContext

object UserPref {

    private var editor: SharedPreferences.Editor
    private var reader: SharedPreferences = AppContext.get().getSharedPreferences("user_pref", Context.MODE_PRIVATE)

    init {
        editor = reader.edit()
        editor.apply()
    }

    fun hasQueue(): Boolean {
        return reader.getBoolean("hasQueue", false)
    }

    private fun enableHasQueue() {
        editor.putBoolean("hasQueue", true).apply()
    }

    fun rememberQueueData(data: QueueData) {
        editor.putString("QueueData", data.toJson()).apply()
        enableHasQueue()
    }

    fun restoreQueueData(data: QueueData) {
        data.fromJson(reader.getString("QueueData", null)!!)
    }

    fun clear() {
        editor.clear().apply()
    }
}