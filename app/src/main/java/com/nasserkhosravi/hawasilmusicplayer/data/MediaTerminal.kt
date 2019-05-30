package com.nasserkhosravi.hawasilmusicplayer.data

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.AudioManager
import android.os.IBinder
import com.nasserkhosravi.appcomponent.AppContext
import com.nasserkhosravi.hawasilmusicplayer.FormatUtils.toSecond
import com.nasserkhosravi.hawasilmusicplayer.data.audio.AudioFocusHelper
import com.nasserkhosravi.hawasilmusicplayer.data.audio.AudioFocusRequestCompat
import com.nasserkhosravi.hawasilmusicplayer.data.model.QueueModel
import com.nasserkhosravi.hawasilmusicplayer.data.model.SongModel
import com.nasserkhosravi.hawasilmusicplayer.data.model.SongStatus
import com.nasserkhosravi.hawasilmusicplayer.di.DaggerMediaTerminalComponent
import com.nasserkhosravi.hawasilmusicplayer.di.MediaTerminalModule
import io.reactivex.disposables.Disposable
import javax.inject.Inject

object MediaTerminal : AudioManager.OnAudioFocusChangeListener {
    private var isServiceBound = false
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            playerService = (service as MediaPlayerService.LocalBinder).service
            isServiceBound = true
            DaggerMediaTerminalComponent.builder()
                .mediaTerminalModule(MediaTerminalModule(AppContext.get(), this@MediaTerminal))
                .build()
                .inject(this@MediaTerminal)
            audioFocusHelper.requestAudioFocus(audioFocusRequest)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            audioFocusHelper.abandonAudioFocus(audioFocusRequest)
            isServiceBound = false
        }
    }
    private var finishObserver: Disposable? = null
    private var playerService: MediaPlayerService? = null

    @set:Inject
    lateinit var audioFocusHelper: AudioFocusHelper
    @set:Inject
    lateinit var audioFocusRequest: AudioFocusRequestCompat
    lateinit var queue: QueueModel

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                resume()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT, AudioManager.AUDIOFOCUS_LOSS -> {
                pause()
            }
        }
    }

    fun processRequest(items: List<SongModel>, position: Int, queueId: String) {
        if (queue.isNewQueue(queueId)) {
            queue.reset()
            queue.items.addAll(items)
            queue.queueId = queueId
            resetReplacePlayPublish(position)
        } else {
            if (queue.isNewSong(items[position].id)) {
                finishObserver?.dispose()
                resetReplacePlayPublish(position)
            }
        }
    }

    fun playNext() {
        if (queue.isOnLastItem()) {
            resetReplacePlayPublish(0)
        } else {
            resetReplacePlayPublish(queue.selectedIndex + 1)
        }
    }

    fun playPrevious() {
        if (queue.isOnFirstItem()) {
            resetReplacePlayPublish(queue.items.lastIndex)
        } else {
            resetReplacePlayPublish(queue.selectedIndex - 1)
        }
    }

    fun resume() {
        if (audioFocusHelper.requestAudioFocus(audioFocusRequest)) {
            registerFinishListener()
            queue.selected!!.status = SongStatus.PLAYING
            if (queue.isSongRestored) {
                playerService!!.resetMediaPlayer()
                playerService!!.prepareSongAndPlay(queue.selected!!.path)
                queue.isSongRestored = false
            } else {
                playerService!!.playFromLastPosition()
            }
            SongEventPublisher.songStatusChange.onNext(SongStatus.PLAYING)
        }
    }

    fun pause() {
        finishObserver?.dispose()
        playerService?.pause()
        queue.selected?.status = SongStatus.PAUSE
        SongEventPublisher.songStatusChange.onNext(SongStatus.PAUSE)
    }

    fun toggleRepeat() {
        queue.toggleRepeat()
    }

    fun togglePlay() {
        if (queue.selected!!.isPlaying()) {
            pause()
        } else {
            resume()
        }
    }

    fun toggleShuffle() {
        queue.toggleShuffle()
        SongEventPublisher.shuffleModeChange.onNext(queue.isShuffle)
    }

    fun seekTo(progress: Int) {
        playerService!!.seekTo(progress)
        queue.selected!!.passedDuration = progress.toLong()
    }

    fun startAndBindService() {
        val context = AppContext.get()
        val serviceIntent = Intent(context, MediaPlayerService::class.java)
        if (!MediaPlayerService.isActive) {
            context.startService(serviceIntent)
        }
        if (!isServiceBound) {
            context.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun resetReplacePlayPublish(position: Int) {
        if (queue.selectedIndex > -1) {
            queue.deActiveSelected()
        }
        queue.active(position)
        playSelectedSong()
        SongEventPublisher.newSongPlay.onNext(queue.selected!!)
        registerFinishListener()
    }

    private fun playSelectedSong() {
        playerService!!.resetMediaPlayer()
        playerService!!.prepareSongAndPlay(queue.selected!!.path)
    }

    private fun registerFinishListener() {
        finishObserver = playerService!!.progressPublisher.observable.subscribe {
            queue.selected!!.passedDuration = playerService!!.getCurrentPosition().toLong()
            SongEventPublisher.songPassedChange.onNext(queue.selected!!.passedDuration)
            if (isCompletedCurrent()) {
                onSongCompletedEvent()
                finishObserver?.dispose()
            }
        }
    }

    private fun onSongCompletedEvent() {
        queue.selected?.resetToPassiveState()
        SongEventPublisher.songComplete.onNext(Any())
        if (queue.hasNextItem()) {
            playNext()
        } else {
            if (queue.shouldStartFromFirst()) {
                resetReplacePlayPublish(0)
            }
        }
    }

    private fun isCompletedCurrent(): Boolean {
        return toSecond(playerService!!.getCurrentPosition().toLong()) == toSecond(queue.selected!!.duration)
    }

    fun tag(): String {
        return MediaTerminal::class.java.simpleName
    }
}