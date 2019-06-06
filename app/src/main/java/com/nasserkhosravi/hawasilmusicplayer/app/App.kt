package com.nasserkhosravi.hawasilmusicplayer.app

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.gson.Gson
import com.nasserkhosravi.appcomponent.AppContext
import com.nasserkhosravi.hawasilmusicplayer.R
import com.nasserkhosravi.hawasilmusicplayer.data.MediaProvider
import com.nasserkhosravi.hawasilmusicplayer.data.persist.MusicPlayerDatabase
import com.nasserkhosravi.hawasilmusicplayer.data.persist.UserPref
import com.nasserkhosravi.hawasilmusicplayer.di.AppModule
import com.nasserkhosravi.hawasilmusicplayer.di.DaggerAppComponent
import leakcanary.LeakCanary
import javax.inject.Inject

open class App : Application() {

    @Inject
    lateinit var jsonAdapter: Gson
    val defaultArt: Bitmap by lazy {
        BitmapFactory.decodeResource(resources, R.drawable.art_default)
    }
    val appIcon: Bitmap by lazy {
        BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
    }

    @Inject
    lateinit var database: MusicPlayerDatabase

    var isNormalIntent = true

    override fun onCreate() {
        super.onCreate()
        val component = DaggerAppComponent.builder().appModule(AppModule(this)).build()
        component.inject(this)

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