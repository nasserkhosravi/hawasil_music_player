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
import com.nasserkhosravi.hawasilmusicplayer.data.MediaTerminal
import com.nasserkhosravi.hawasilmusicplayer.data.SongEventPublisher
import com.nasserkhosravi.hawasilmusicplayer.data.model.SongModel
import java.io.FileNotFoundException

class SongPlayerViewModel : ViewModel() {
    private var defaultArt: Bitmap? = null

    private val repeat = MutableLiveData<Boolean>()
    private val shuffle = MutableLiveData<Boolean>()
    private var favorite = MutableLiveData<Boolean>()

    init {
        val data = MediaTerminal.queue
        repeat.value = data.isEnableRepeat
        shuffle.value = data.isShuffle
        favorite.value = FavoriteManager.isFavorite(data.selected!!.id)
    }

    val getRepeat: LiveData<Boolean>
        get() = repeat

    val getFavorite: LiveData<Boolean>
        get() = favorite

    fun getShuffleEvent() = SongEventPublisher.shuffleModeChange

    fun getNewSongEvent() = SongEventPublisher.newSongPlay

    fun getSongStatusEvent() = SongEventPublisher.songStatusChange

    fun getSongCompleteEvent() = SongEventPublisher.songComplete

    fun getSongChangeEvent() = SongEventPublisher.songPassedChange

    fun seekTo(progress: Int) {
        MediaTerminal.seekTo(progress)
    }

    fun reversePlay() {
        MediaTerminal.togglePlay()
    }

    fun playPrevious() {
        MediaTerminal.playPrevious()
    }

    fun playNext() {
        MediaTerminal.playNext()
    }

    fun toggleRepeat() {
        MediaTerminal.toggleRepeat()
        repeat.value = !repeat.value!!
    }

    fun toggleShuffle() {
        MediaTerminal.toggleShuffle()
        shuffle.value = !shuffle.value!!
    }

    fun getArt(context: Context): Bitmap {
        val model = MediaTerminal.queue.selected!!
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
            if (FavoriteManager.remove(MediaTerminal.queue.selected!!.id)) {
                favorite.value = false
            }
        } else {
            if (FavoriteManager.add(MediaTerminal.queue.selected!!.id)) {
                favorite.value = true
            }
        }
    }

    private fun getDefaultArt(context: Context): Bitmap {
        if (defaultArt == null) {
            defaultArt = BitmapFactory.decodeResource(context.resources, R.drawable.art_default)
        }
        return defaultArt!!
    }

    fun getCurrentSong(): SongModel {
        return MediaTerminal.queue.selected!!
    }

}