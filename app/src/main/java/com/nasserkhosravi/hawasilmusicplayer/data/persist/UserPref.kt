package com.nasserkhosravi.hawasilmusicplayer.data.persist

import android.content.Context
import android.content.SharedPreferences
import com.nasserkhosravi.hawasilmusicplayer.data.model.QueueModel

object UserPref {

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

    fun saveQueueData(data: QueueModel) {
        reader.edit().putString("QueueModel", data.toJson()).apply()
        enableHasQueue()
    }

    fun retrieveQueueData(): QueueModel? {
        val json = reader.getString("QueueModel", null)
        if (json != null) {
            return QueueModel.fromJson(json)
        }
        return null
    }

    fun clear() {
        reader.edit().clear().apply()
    }
}