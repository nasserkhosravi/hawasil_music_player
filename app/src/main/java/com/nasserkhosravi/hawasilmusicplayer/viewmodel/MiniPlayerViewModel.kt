package com.nasserkhosravi.hawasilmusicplayer.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.nasserkhosravi.hawasilmusicplayer.data.QueueEvents
import com.nasserkhosravi.hawasilmusicplayer.data.QueueManager
import com.nasserkhosravi.hawasilmusicplayer.data.UIMediaCommand
import com.nasserkhosravi.hawasilmusicplayer.data.UserPref

class MiniPlayerViewModel(app: Application) : AndroidViewModel(app) {

    fun getNewSongPlay() = QueueEvents.newSongPlay

    fun getSongStatus() = QueueEvents.songStatus

    fun getSongComplete() = QueueEvents.songComplete

    fun getSongPassed() = QueueEvents.songPassed

    fun reversePlay() = UIMediaCommand.togglePlay()

    fun hasQueue() = UserPref.hasQueue()

    fun getCurrentSong() = QueueManager.get().queue.selected

}