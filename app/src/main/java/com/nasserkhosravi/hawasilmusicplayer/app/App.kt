package com.nasserkhosravi.hawasilmusicplayer.app

import android.app.Application
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.nasserkhosravi.appcomponent.AppContext
import com.nasserkhosravi.hawasilmusicplayer.data.model.SongModel
import com.nasserkhosravi.hawasilmusicplayer.data.model.SongModelAdapter
import leakcanary.LeakCanary

class App : Application() {

    val jsonAdapter: Gson by lazy {
        GsonBuilder()
            .registerTypeAdapter(
                SongModel::class.java,
                SongModelAdapter()
            )
            .create()
    }

    override fun onCreate() {
        super.onCreate()
        LeakCanary.config = LeakCanary.config.copy(dumpHeap = false)
        instance = this
        AppContext.build(this)
    }

    companion object {
        private lateinit var instance: App

        fun get(): App {
            return instance
        }
    }

}