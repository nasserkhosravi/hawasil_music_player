package com.nasserkhosravi.hawasilmusicplayer.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nasserkhosravi.appcomponent.AppContext
import com.nasserkhosravi.hawasilmusicplayer.data.MediaProvider
import com.nasserkhosravi.hawasilmusicplayer.data.QueueBrain
import com.nasserkhosravi.hawasilmusicplayer.data.model.SongModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SongsViewModel : ViewModel() {

    private val songs: MutableLiveData<List<SongModel>> by lazy {
        MutableLiveData<List<SongModel>>().also {
            fetchSongsAsync(it)
        }
    }

    val getSongs: LiveData<List<SongModel>>
        get() = songs

    @SuppressLint("CheckResult")
    private fun fetchSongsAsync(result: MutableLiveData<List<SongModel>>) {
        //todo: it will be good to convert single to completable
        Single.just("").subscribeOn(Schedulers.io())
            .map { MediaProvider.getSongs() }
            .observeOn(AndroidSchedulers.mainThread()).subscribe({
                result.value = it
            }, {})
    }


    fun onSongClick(position: Int) {
        QueueBrain.checkNewQueueRequest(songs.value!!, position, "${AppContext.get().packageName}.songList")
    }
}