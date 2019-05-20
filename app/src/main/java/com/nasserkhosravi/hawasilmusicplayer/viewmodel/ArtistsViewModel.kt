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
        //todo: it will be good to convert single to completable
        Single.just("").subscribeOn(Schedulers.io())
            .map { MediaProvider.getArtists() }
            .observeOn(AndroidSchedulers.mainThread()).subscribe({
                result.value = it
            }, {})
    }

}