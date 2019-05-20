package com.nasserkhosravi.hawasilmusicplayer.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nasserkhosravi.hawasilmusicplayer.data.FolderCache
import com.nasserkhosravi.hawasilmusicplayer.data.MediaProvider
import com.nasserkhosravi.hawasilmusicplayer.data.model.FlatFolderModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class FoldersViewModel : ViewModel() {

    private val folders: MutableLiveData<List<FlatFolderModel>> by lazy {
        MutableLiveData<List<FlatFolderModel>>().also {
            fetchFoldersAsync(it)
        }
    }

    val getFolders: LiveData<List<FlatFolderModel>>
        get() = folders

    @SuppressLint("CheckResult")
    private fun fetchFoldersAsync(result: MutableLiveData<List<FlatFolderModel>>) {
        Single.just("").subscribeOn(Schedulers.io())
            .map {
                MediaProvider.checkCacheFoldersContainSong()
                FolderCache.flats
            }
            .observeOn(AndroidSchedulers.mainThread()).subscribe({
                result.value = it
            }, {})
    }

}