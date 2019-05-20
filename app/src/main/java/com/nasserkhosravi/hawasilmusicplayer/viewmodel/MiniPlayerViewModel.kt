package com.nasserkhosravi.hawasilmusicplayer.viewmodel

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nasserkhosravi.hawasilmusicplayer.app.App
import com.nasserkhosravi.hawasilmusicplayer.data.MediaPlayerService
import com.nasserkhosravi.hawasilmusicplayer.data.QueueBrain
import com.nasserkhosravi.hawasilmusicplayer.data.UserPref
import com.nasserkhosravi.hawasilmusicplayer.data.model.SongModel
import io.reactivex.disposables.Disposable

class MiniPlayerViewModel(app: Application) : AndroidViewModel(app) {
    private var player: MediaPlayerService? = null
    private var serviceConnection: ServiceConnection
    private var progressObserver: Disposable? = null
    private val progress = MutableLiveData<Float>()

    init {
        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                player = (service as MediaPlayerService.LocalBinder).service
                registerProgressReporting()
            }

            override fun onServiceDisconnected(name: ComponentName) {}
        }
        val serviceIntent = Intent(app, MediaPlayerService::class.java)
        app.startService(serviceIntent)
        app.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    val getProgress: LiveData<Float>
        get() = progress

    fun reversePlay() {
        QueueBrain.reversePlay()
    }

    fun hasQueue(): Boolean {
        return UserPref.hasQueue()
    }

    fun getLastSong(): SongModel? {
        return QueueBrain.getSelected()
    }

    fun onStop() {
        progressObserver?.dispose()
    }

    fun registerProgressReporting() {
        progressObserver?.dispose()
        progressObserver = player!!.progressPublisher.observable.subscribe {
            if (player!!.isReadyToComputePassedDuration()) {
                progress.value = it.toFloat()
            }
        }
    }

    fun unRegisterProgressReporting() {
        progressObserver?.dispose()
    }

    fun onStart() {
        if (player != null) {
            registerProgressReporting()
        }
    }

    fun onDestroyView() {
        if (player != null) {
            getApplication<App>().unbindService(serviceConnection)
        }
        progressObserver?.dispose()
    }

}