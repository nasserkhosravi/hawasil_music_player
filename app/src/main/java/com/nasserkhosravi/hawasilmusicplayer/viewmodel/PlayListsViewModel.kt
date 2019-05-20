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

class PlayListsViewModel : ViewModel() {
    private val playLists: MutableLiveData<List<PlayListModel>> by lazy {
        MutableLiveData<List<PlayListModel>>().also {
            fetchPlayListAsync(it)
        }
    }

    val getPlayLists: LiveData<List<PlayListModel>>
        get() = playLists

    @SuppressLint("CheckResult")
    private fun fetchPlayListAsync(result: MutableLiveData<List<PlayListModel>>) {
        Single.just("").subscribeOn(Schedulers.io())
            .map { MediaProvider.getPlayLists() }
            .observeOn(AndroidSchedulers.mainThread()).subscribe({
                result.value = it
            }, {})
    }
}