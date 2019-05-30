package com.nasserkhosravi.hawasilmusicplayer.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nasserkhosravi.hawasilmusicplayer.data.MediaProvider
import com.nasserkhosravi.hawasilmusicplayer.data.model.AlbumModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Callable

class AlbumsViewModel : ViewModel() {

    private val albums: MutableLiveData<List<AlbumModel>> by lazy {
        MutableLiveData<List<AlbumModel>>().also {
            fetchAlbumsAsync(it)
        }
    }

    val getAlbums: LiveData<List<AlbumModel>>
        get() = albums

    @SuppressLint("CheckResult")
    private fun fetchAlbumsAsync(result: MutableLiveData<List<AlbumModel>>) {
        val runnable = Callable<List<AlbumModel>> {
            MediaProvider.getAlbums()
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