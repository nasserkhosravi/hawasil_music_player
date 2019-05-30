package com.nasserkhosravi.hawasilmusicplayer.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.nasserkhosravi.hawasilmusicplayer.data.MediaTerminal
import com.nasserkhosravi.hawasilmusicplayer.data.SongEventPublisher
import com.nasserkhosravi.hawasilmusicplayer.data.UserPref

class MiniPlayerViewModel(app: Application) : AndroidViewModel(app) {

    fun getNewSongPlay() = SongEventPublisher.newSongPlay

    fun getSongStatus() = SongEventPublisher.songStatusChange

    fun getSongComplete() = SongEventPublisher.songComplete

    fun getSongPassed() = SongEventPublisher.songPassedChange

    fun reversePlay() = MediaTerminal.togglePlay()

    fun hasQueue() = UserPref.hasQueue()

    fun getLastSong() = MediaTerminal.queue.selected

}