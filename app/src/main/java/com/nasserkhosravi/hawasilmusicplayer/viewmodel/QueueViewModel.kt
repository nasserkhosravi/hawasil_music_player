package com.nasserkhosravi.hawasilmusicplayer.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nasserkhosravi.hawasilmusicplayer.data.MediaProvider
import com.nasserkhosravi.hawasilmusicplayer.data.QueueBrain
import com.nasserkhosravi.hawasilmusicplayer.data.model.QueueType
import com.nasserkhosravi.hawasilmusicplayer.data.model.SongModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

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
        Observable.just(queueId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val songs = fetchSongsFromDB()
                songs.sortBy { it.title }
                result.value = songs
            }
    }

    fun onSongClick(position: Int) {
        QueueBrain.processRequest(songs.value!!, position, queueId)
    }

    fun tag(): String {
        return QueueViewModel::class.java.simpleName
    }

}