package com.nasserkhosravi.hawasilmusicplayer.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nasserkhosravi.hawasilmusicplayer.data.MediaProvider
import com.nasserkhosravi.hawasilmusicplayer.data.model.ArtistModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Callable

class ArtistsViewModel : ViewModel() {

    private val artists: MutableLiveData<List<ArtistModel>> by lazy {
        MutableLiveData<List<ArtistModel>>().also {
            fetchArtistAsync(it)
        }
    }

    val getArtists: LiveData<List<ArtistModel>>
        get() = artists

    @SuppressLint("CheckResult")
    private fun fetchArtistAsync(result: MutableLiveData<List<ArtistModel>>) {
        val runnable = Callable<List<ArtistModel>> {
            MediaProvider.getArtists()
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