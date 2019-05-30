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
import java.util.concurrent.Callable

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
        val runnable = Callable<List<FlatFolderModel>> {
            MediaProvider.checkCacheFoldersContainSong()
            FolderCache.flatFolders
        }
        Single.fromCallable(runnable)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                result.value = it
            }, {}
            )
    }

}