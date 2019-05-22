package com.nasserkhosravi.hawasilmusicplayer.data

import android.content.Context
import android.content.SharedPreferences

object UserPref {

    //    private lateinit var editor: SharedPreferences.Editor
    private lateinit var reader: SharedPreferences

    fun build(context: Context, name: String) {
        reader = context.getSharedPreferences(name, Context.MODE_PRIVATE)
    }

    fun hasQueue(): Boolean {
        return reader.getBoolean("hasQueue", false)
    }

    private fun enableHasQueue() {
        reader.edit().putBoolean("hasQueue", true).apply()
    }

    fun saveQueueData(data: QueueData) {
        reader.edit().putString("QueueData", data.toJson()).apply()
        enableHasQueue()
    }

    fun retrieveQueueData(): QueueData? {
        val json = reader.getString("QueueData", null)
        if (json != null) {
            return QueueData.fromJson(json)
        }
        return null
    }

    fun clear() {
        reader.edit().clear().apply()
    }
}