package com.nasserkhosravi.hawasilmusicplayer.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nasserkhosravi.hawasilmusicplayer.R
import com.nasserkhosravi.hawasilmusicplayer.data.FavoriteManager
import com.nasserkhosravi.hawasilmusicplayer.data.QueueBrain
import com.nasserkhosravi.hawasilmusicplayer.data.SongEventPublisher
import com.nasserkhosravi.hawasilmusicplayer.data.model.SongModel
import com.nasserkhosravi.hawasilmusicplayer.data.model.SongStatus
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import java.io.FileNotFoundException

class SongPlayerViewModel : ViewModel() {
    private var defaultArt: Bitmap? = null

    private val songPassed = MutableLiveData<Long>()
    private val repeat = MutableLiveData<Boolean>()
    private val shuffle = MutableLiveData<Boolean>()
    private var favorite = MutableLiveData<Boolean>()
    private var progressObserver: Disposable? = null

    init {
        val data = QueueBrain.data
        repeat.value = data.isEnableRepeat
        shuffle.value = data.isShuffle
        favorite.value = FavoriteManager.isFavorite(data.selected!!.id)
    }

    val getRepeat: LiveData<Boolean>
        get() = repeat

    val getSongPassed: LiveData<Long>
        get() = songPassed

    val getFavorite: LiveData<Boolean>
        get() = favorite

    fun getNewSongEvent(): PublishSubject<SongModel> {
        return SongEventPublisher.newSongPlay
    }

    fun getSongStatusEvent(): PublishSubject<SongStatus> {
        return SongEventPublisher.songStatusChange
    }

    fun getSongCompleteEvent(): PublishSubject<Any> {
        return SongEventPublisher.songComplete
    }

    fun seekTo(progress: Int) {
        QueueBrain.seekTo(progress)
    }

    fun reversePlay() {
        QueueBrain.togglePlay()
    }

    fun playPrevious() {
        QueueBrain.playPrevious()
    }

    fun playNext() {
        QueueBrain.playNext()
    }

    fun toggleRepeat() {
        QueueBrain.toggleRepeat()
        repeat.value = !repeat.value!!
    }

    fun toggleShuffle() {
        QueueBrain.toggleShuffle()
        shuffle.value = !shuffle.value!!
    }

    fun getArt(context: Context): Bitmap {
        val model = QueueBrain.data.selected!!
        return if (model.artUri != null) {
            try {
                MediaStore.Images.Media.getBitmap(context.contentResolver, model.artUri!!)
            } catch (e: FileNotFoundException) {
                getDefaultArt(context)
            }
        } else {
            getDefaultArt(context)
        }
    }

    fun toggleFavorite() {
        if (favorite.value!!) {
            if (FavoriteManager.remove(QueueBrain.data.selected!!.id)) {
                favorite.value = false
            }
        } else {
            if (FavoriteManager.add(QueueBrain.data.selected!!.id)) {
                favorite.value = true
            }
        }
    }

    fun unRegisterTimeReporting() {
        progressObserver?.dispose()
    }

    fun registerTimeReporting() {
        progressObserver = QueueBrain.playerService!!.progressPublisher.observable.subscribe {
            songPassed.value = getCurrentSong().songPassed
        }
    }

    private fun getDefaultArt(context: Context): Bitmap {
        if (defaultArt == null) {
            defaultArt = BitmapFactory.decodeResource(context.resources, R.drawable.art_default)
        }
        return defaultArt!!
    }

    override fun onCleared() {
        super.onCleared()
        unRegisterTimeReporting()
    }

    fun getCurrentSong(): SongModel {
        return QueueBrain.data.selected!!
    }

}