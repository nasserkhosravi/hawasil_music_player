package com.nasserkhosravi.hawasilmusicplayer.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nasserkhosravi.hawasilmusicplayer.app.App
import com.nasserkhosravi.hawasilmusicplayer.data.FavoriteManager
import com.nasserkhosravi.hawasilmusicplayer.data.QueueEvents
import com.nasserkhosravi.hawasilmusicplayer.data.QueueManager
import com.nasserkhosravi.hawasilmusicplayer.data.UIMediaCommand
import com.nasserkhosravi.hawasilmusicplayer.data.model.SongModel
import java.io.FileNotFoundException

class SongPlayerViewModel : ViewModel() {
    private var defaultArt: Bitmap? = null

    private val repeat = MutableLiveData<Boolean>()
    private val shuffle = MutableLiveData<Boolean>()
    private var favorite = MutableLiveData<Boolean>()

    init {
        val data = QueueManager.get().queue!!
        repeat.value = data.isEnableRepeat
        shuffle.value = data.isShuffled
        favorite.value = FavoriteManager.isFavorite(data.selected!!.id)
    }

    val getRepeat: LiveData<Boolean>
        get() = repeat

    val getShuffle: LiveData<Boolean>
        get() = shuffle

    val getFavorite: LiveData<Boolean>
        get() = favorite

    fun getShuffleEvent() = QueueEvents.shuffleMode

    fun getNewSongEvent() = QueueEvents.newSongPlay

    fun getSongStatusEvent() = QueueEvents.songStatus

    fun getSongCompleteEvent() = QueueEvents.songComplete

    fun getSongChangeEvent() = QueueEvents.songPassed

    fun seekTo(progress: Int) {
        UIMediaCommand.seekTo(progress)
    }

    fun reversePlay() {
        UIMediaCommand.togglePlay()
    }

    fun playPrevious() {
        UIMediaCommand.playPrevious()
    }

    fun playNext() {
        UIMediaCommand.playNext()
    }

    fun toggleRepeat() {
        UIMediaCommand.nextRepeatMode()
        repeat.value = !repeat.value!!
    }

    fun toggleShuffle() {
        UIMediaCommand.toggleShuffle()
        shuffle.value = !shuffle.value!!
    }

    fun getArt(context: Context): Bitmap {
        val model = getCurrentSong()
        return if (model.artUri != null) {
            try {
                MediaStore.Images.Media.getBitmap(context.contentResolver, model.artUri!!)
            } catch (e: FileNotFoundException) {
                App.get().defaultArt
            }
        } else {
            App.get().defaultArt
        }
    }

    fun toggleFavorite() {
        if (favorite.value!!) {
            if (FavoriteManager.remove(getCurrentSong().id)) {
                favorite.value = false
            }
        } else {
            if (FavoriteManager.add(getCurrentSong().id)) {
                favorite.value = true
            }
        }
    }

    fun getCurrentSong(): SongModel {
        return QueueManager.get().queue!!.selected!!
    }

}