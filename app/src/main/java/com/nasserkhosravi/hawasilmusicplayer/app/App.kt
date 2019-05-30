package com.nasserkhosravi.hawasilmusicplayer.app

import android.app.Application
import com.google.gson.Gson
import com.nasserkhosravi.appcomponent.AppContext
import com.nasserkhosravi.hawasilmusicplayer.data.MediaProvider
import com.nasserkhosravi.hawasilmusicplayer.data.UserPref
import com.nasserkhosravi.hawasilmusicplayer.di.DaggerAppComponent
import leakcanary.LeakCanary
import javax.inject.Inject

open class App : Application() {

    @Inject
    lateinit var jsonAdapter: Gson

    override fun onCreate() {
        super.onCreate()
        DaggerAppComponent.create().inject(this)

        LeakCanary.config = LeakCanary.config.copy(dumpHeap = false)
        instance = this
        AppContext.build(this)
        UserPref.build(this, "user_pref")
        MediaProvider.setContentResolver(contentResolver)
    }

    companion object {
        private lateinit var instance: App

        fun get(): App {
            return instance
        }
    }

}