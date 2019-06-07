package com.nasserkhosravi.hawasilmusicplayer.data

import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import com.nasserkhosravi.hawasilmusicplayer.FormatUtils
import com.nasserkhosravi.hawasilmusicplayer.data.audio.AudioFocusHelper
import com.nasserkhosravi.hawasilmusicplayer.data.audio.AudioFocusRequestCompat
import com.nasserkhosravi.hawasilmusicplayer.data.model.QueueModel
import com.nasserkhosravi.hawasilmusicplayer.data.model.SongModel
import com.nasserkhosravi.hawasilmusicplayer.data.model.SongStatus
import io.reactivex.disposables.Disposable

class QueueManager private constructor() {
    var finishObserver: Disposable? = null
    var queue: QueueModel? = null
    private lateinit var playerService: MediaPlayerService
    private lateinit var audioFocusHelper: AudioFocusHelper
    private lateinit var audioFocusRequest: AudioFocusRequestCompat
    // temporary information for processing request, it will be null after processing
    var requestedQueueBundle: Bundle? = null

    fun setArgs(
        mediaPlayerService: MediaPlayerService,
        audioFocusHelper: AudioFocusHelper,
        audioFocusRequest: AudioFocusRequestCompat
    ) {
        this.playerService = mediaPlayerService
        this.audioFocusHelper = audioFocusHelper
        this.audioFocusRequest = audioFocusRequest
    }

    fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                playerService.mediaPlayer!!.setVolume(1f, 1f)
                resume()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                playerService.mediaPlayer!!.setVolume(0.2f, 0.2f)
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT, AudioManager.AUDIOFOCUS_LOSS -> {
                playerService.mediaPlayer!!.setVolume(1f, 1f)
                pause()
            }
        }
    }

    fun processRequest(items: List<SongModel>, position: Int, queueId: String): Int {
        if (queue!!.isNewQueue(queueId)) {
            queue!!.reset()
            queue!!.items.addAll(items)
            queue!!.queueId = queueId
            resetReplacePlayPublish(position)
            return 1
        } else {
            if (queue!!.isNewSong(items[position].id)) {
                finishObserver?.dispose()
                resetReplacePlayPublish(position)
                return 0
            }
            return -1
        }
    }

    fun playByPosition(position: Int) {
        if (position == queue!!.selectedIndex) {
            return
        }
        if (position > -1 && position <= queue!!.items.size) {
            resetReplacePlayPublish(position)
        }
    }

    fun playNext() {
        if (queue!!.isOnLastItem()) {
            resetReplacePlayPublish(0)
        } else {
            resetReplacePlayPublish(queue!!.selectedIndex + 1)
        }
    }

    fun playPrevious() {
        if (queue!!.isOnFirstItem()) {
            resetReplacePlayPublish(queue!!.items.lastIndex)
        } else {
            resetReplacePlayPublish(queue!!.selectedIndex - 1)
        }
    }

    fun resume(): Boolean {
        if (audioFocusHelper.requestAudioFocus(audioFocusRequest)) {
            registerFinishListener()
            queue!!.selected!!.status = SongStatus.PLAYING
            if (queue!!.shouldLoad) {
                playerService.resetMediaPlayer()
                playerService.prepareSongAndPlay(queue!!.selected!!.path)
                queue!!.shouldLoad = false
            } else {
                playerService.playFromLastPosition()
            }
            QueueEvents.songStatus.onNext(SongStatus.PLAYING)
            return true
        }
        return false
    }

    fun pause() {
        finishObserver?.dispose()
        playerService.pause()
        queue?.selected?.status = SongStatus.PAUSE
        QueueEvents.songStatus.onNext(SongStatus.PAUSE)
    }

    fun setRepeatMode(isEnable: Boolean) {
        if (queue!!.isEnableRepeat != isEnable) {
            queue!!.setRepeat(isEnable)
        }
    }

    fun setShuffle(isEnable: Boolean) {
        if (queue!!.isShuffled != isEnable) {
            queue!!.setShuffle(isEnable)
            QueueEvents.shuffleMode.onNext(queue!!.isShuffled)
        }
    }

    fun seekTo(progress: Long) {
        playerService.mediaPlayer!!.seekTo(progress.toInt())
        queue!!.selected!!.passedDuration = progress
    }

    private fun resetReplacePlayPublish(position: Int) {
        if (queue!!.selectedIndex > -1) {
            queue!!.deActiveSelected()
        }
        queue!!.active(position)
        playSelectedSong()
        QueueEvents.newSongPlay.onNext(queue!!.selected!!)
        finishObserver?.dispose()
        registerFinishListener()
    }

    private fun playSelectedSong() {
        playerService.resetMediaPlayer()
        playerService.prepareSongAndPlay(queue!!.selected!!.path)
    }

    private fun registerFinishListener() {
        finishObserver = playerService.progressPublisher.observable.subscribe({
            queue!!.selected!!.passedDuration = playerService.getCurrentPosition().toLong()
            QueueEvents.songPassed.onNext(queue!!.selected!!.passedDuration)
            if (isCompletedCurrent()) {
                onSongCompletedEvent()
                finishObserver?.dispose()
            }
        }, {
            Log.d(tag(), "registerFinishListener: error")
        })
    }

    private fun onSongCompletedEvent() {
        queue!!.selected?.resetToPassiveState()
        QueueEvents.songComplete.onNext(Any())
        if (queue!!.hasNextItem()) {
            playNext()
        } else {
            if (queue!!.shouldStartFromFirst()) {
                resetReplacePlayPublish(0)
            } else {
                QueueEvents.queueCompleted.onNext(Any())
            }
        }
    }

    private fun isCompletedCurrent(): Boolean {
        return FormatUtils.toSecond(playerService.getCurrentPosition().toLong()) == FormatUtils.toSecond(queue!!.selected!!.duration)
    }

    companion object {
        fun tag(): String {
            return QueueManager::class.java.simpleName
        }

        private val instance = QueueManager()
        fun get() = instance
    }
}