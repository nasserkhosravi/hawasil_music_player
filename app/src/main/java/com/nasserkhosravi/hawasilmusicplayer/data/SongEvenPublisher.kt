package com.nasserkhosravi.hawasilmusicplayer.data

import com.nasserkhosravi.hawasilmusicplayer.data.model.SongModel
import com.nasserkhosravi.hawasilmusicplayer.data.model.SongStatus
import io.reactivex.subjects.PublishSubject

object SongEventPublisher {

    val newSongPlay = PublishSubject.create<SongModel>()
    val songComplete = PublishSubject.create<Any>()
    val songStatusChange = PublishSubject.create<SongStatus>()
    val shuffleModeChange = PublishSubject.create<Boolean>()
    val songPassedChange = PublishSubject.create<Long>()

    init {
        newSongPlay.publish().autoConnect()
        songComplete.publish().autoConnect()
        songStatusChange.publish().autoConnect()
        songPassedChange.publish().autoConnect()
    }
}