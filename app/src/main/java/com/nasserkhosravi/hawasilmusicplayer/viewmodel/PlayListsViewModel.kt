package com.nasserkhosravi.hawasilmusicplayer.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nasserkhosravi.hawasilmusicplayer.data.MediaProvider
import com.nasserkhosravi.hawasilmusicplayer.data.model.PlayListModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Callable

class PlayListsViewModel : ViewModel() {
    private val playLists: MutableLiveData<ArrayList<PlayListModel>> by lazy {
        MutableLiveData<ArrayList<PlayListModel>>().also {
            fetchPlayListAsync(it)
        }
    }

    val getPlayLists: LiveData<ArrayList<PlayListModel>>
        get() = playLists

    @SuppressLint("CheckResult")
    private fun fetchPlayListAsync(result: MutableLiveData<ArrayList<PlayListModel>>) {
        val runnable = Callable<ArrayList<PlayListModel>> {
            MediaProvider.getPlayLists()
        }
        Single.fromCallable(runnable)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                result.value = it
            }, {}
            )
    }

    fun removePlaylist(playlistId: Long): Boolean {
        return MediaProvider.removePlaylist(playlistId) > -1
    }

    fun createPlaylist(playlistName: String): Long {
        return MediaProvider.createPlaylist(playlistName)
    }

}