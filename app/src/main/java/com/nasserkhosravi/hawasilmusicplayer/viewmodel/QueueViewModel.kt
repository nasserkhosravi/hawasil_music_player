package com.nasserkhosravi.hawasilmusicplayer.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nasserkhosravi.hawasilmusicplayer.data.MediaProvider
import com.nasserkhosravi.hawasilmusicplayer.data.MediaTerminal
import com.nasserkhosravi.hawasilmusicplayer.data.model.QueueType
import com.nasserkhosravi.hawasilmusicplayer.data.model.SongModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Callable

class QueueViewModel : ViewModel() {
    private lateinit var queueId: String
    private var typeId = -1
    private val songs: MutableLiveData<List<SongModel>> by lazy {
        MutableLiveData<List<SongModel>>().also {
            fetchSongsFromDBAsync(it)
        }
    }

    fun setArgs(queueId: String, type: Int) {
        this.queueId = queueId
        this.typeId = type
    }

    val getSongs: LiveData<List<SongModel>>
        get() = songs

    private fun fetchSongsFromDB(): ArrayList<SongModel> {
        return when (QueueType.fromId(typeId)) {
            QueueType.FOLDER -> {
                MediaProvider.getSongsByFolder(queueId)
            }
            QueueType.ARTIST -> {
                MediaProvider.getSongsByArtist(queueId.toLong())
            }
            QueueType.ALBUM -> {
                MediaProvider.getSongsByAlbum(queueId.toLong())
            }
            QueueType.PLAYLIST -> {
                MediaProvider.getPlaylistTracks(queueId.toLong())
            }
            QueueType.SONGS -> {
                MediaProvider.getSongs()
            }
            else -> throw IllegalArgumentException()
        }
    }

    @SuppressLint("CheckResult")
    private fun fetchSongsFromDBAsync(result: MutableLiveData<List<SongModel>>) {
        val runnable = Callable<ArrayList<SongModel>> {
            val songs = fetchSongsFromDB()
            songs.sortBy { it.title }
            songs
        }
        Single.fromCallable(runnable)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                result.value = it
            }, {}
            )
    }

    fun onSongClick(position: Int) {
        MediaTerminal.processRequest(songs.value!!, position, queueId)
    }

    fun tag(): String {
        return QueueViewModel::class.java.simpleName
    }

}