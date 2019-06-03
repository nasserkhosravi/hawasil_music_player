package com.nasserkhosravi.hawasilmusicplayer.data

import com.nasserkhosravi.hawasilmusicplayer.data.model.SongModel
import com.nasserkhosravi.hawasilmusicplayer.data.model.SongStatus
import io.reactivex.subjects.PublishSubject

object QueueEvents {

    val newSongPlay = PublishSubject.create<SongModel>()
    val songComplete = PublishSubject.create<Any>()
    val songStatus = PublishSubject.create<SongStatus>()
    val shuffleMode = PublishSubject.create<Boolean>()
    val songPassed = PublishSubject.create<Long>()
    val queueCompleted = PublishSubject.create<Any>()

    init {
        newSongPlay.publish().autoConnect()
        songComplete.publish().autoConnect()
        songStatus.publish().autoConnect()
        songPassed.publish().autoConnect()
        queueCompleted.publish().autoConnect()
    }
}