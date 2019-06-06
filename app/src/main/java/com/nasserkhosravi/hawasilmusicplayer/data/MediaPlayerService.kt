package com.nasserkhosravi.hawasilmusicplayer.data

import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.text.TextUtils
import androidx.media.MediaBrowserServiceCompat
import androidx.media.session.MediaButtonReceiver
import com.nasserkhosravi.appcomponent.AppContext
import com.nasserkhosravi.hawasilmusicplayer.R
import com.nasserkhosravi.hawasilmusicplayer.app.App
import com.nasserkhosravi.hawasilmusicplayer.data.audio.AudioFocusHelper
import com.nasserkhosravi.hawasilmusicplayer.data.audio.AudioFocusRequestCompat
import com.nasserkhosravi.hawasilmusicplayer.data.model.SongStatus
import com.nasserkhosravi.hawasilmusicplayer.data.persist.UserPref
import com.nasserkhosravi.hawasilmusicplayer.di.*
import com.nasserkhosravi.hawasilmusicplayer.getMediaMetaData
import io.reactivex.disposables.CompositeDisposable
import java.io.IOException
import javax.inject.Inject

class MediaPlayerService : MediaBrowserServiceCompat(), MediaPlayer.OnPreparedListener,
    AudioManager.OnAudioFocusChangeListener {

    @Inject
    @JvmField
    var mediaPlayer: MediaPlayer? = null
    @Inject
    lateinit var stateBuilder: PlaybackStateCompat.Builder
    @Inject
    lateinit var progressPublisher: SuspendableObservable
    @Inject
    lateinit var audioNoisyReceiver: AudioNoisyReceiver
    @Inject
    lateinit var mediaSession: MediaSessionCompat
    @set:Inject
    lateinit var audioFocusHelper: AudioFocusHelper
    @set:Inject
    lateinit var audioFocusRequest: AudioFocusRequestCompat
    @Inject
    lateinit var mediaController: MediaControllerCompat
    @Inject
    lateinit var mediaControllerCallback: MediaControllerCallBack
    @Inject
    lateinit var disposables: CompositeDisposable

    private lateinit var manager: QueueManager

    override fun onAudioFocusChange(focusChange: Int) {
        manager.onAudioFocusChange(focusChange)
    }

    override fun onCreate() {
        super.onCreate()
        isActive = true
        DaggerMediaPlayerServiceComponent.builder()
            .mediaPlayerServiceModule(MediaPlayerServiceModule(this))
            .audioFocusModule(AudioFocusModule(AppContext.get(), this))
            .mediaSessionModule(MediaSessionModule(this, tag()))
            .notificationModule(NotificationModule(this))
            .build()
            .inject(this)
        QueueManager.get().setArgs(this, audioFocusHelper, audioFocusRequest)
        manager = QueueManager.get()
        audioFocusHelper.requestAudioFocus(audioFocusRequest)

        mediaPlayer!!.setOnPreparedListener(this)
        registerReceiver(audioNoisyReceiver, AudioNoisyReceiver.createIntentFilter())
        mediaSession.setPlaybackState(stateBuilder.build())
        sessionToken = mediaSession.sessionToken
        mediaController.registerCallback(mediaControllerCallback)

        val newSongPlayDisposable = QueueEvents.newSongPlay.subscribe {
            mediaSession.setMetadata(manager.queue!!.selected!!.getMediaMetaData(this))
            updateMediaSessionPlaybackState(stateBuilder, PlaybackStateCompat.STATE_PLAYING)
        }
        val queueCompletedDisposable = QueueEvents.queueCompleted.subscribe {
            updateMediaSessionPlaybackState(stateBuilder, PlaybackStateCompat.STATE_PAUSED)
        }

        val songStatusDisposable = QueueEvents.songStatus.subscribe {
            mediaSession.setMetadata(manager.queue!!.selected!!.getMediaMetaData(this))
            if (it.isPlay()) {
                updateMediaSessionPlaybackState(stateBuilder, PlaybackStateCompat.STATE_PLAYING)
            } else {
                updateMediaSessionPlaybackState(stateBuilder, PlaybackStateCompat.STATE_PAUSED)
            }
        }
        disposables.add(songStatusDisposable)
        disposables.add(newSongPlayDisposable)
        disposables.add(queueCompletedDisposable)

    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot? {
        if (TextUtils.equals(clientPackageName, packageName)) {
            return BrowserRoot(getString(R.string.app_name), null)
        }
        return null
    }

    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        result.sendResult(null)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        MediaButtonReceiver.handleIntent(mediaSession, intent)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onPrepared(mp: MediaPlayer?) {
        playFromLastPosition()
    }

    fun playFromLastPosition() {
        if (!mediaPlayer?.isPlaying!!) {
            //todo:song passed dependency, remove it
            mediaPlayer!!.seekTo(manager.queue!!.selected!!.passedDuration.toInt())
            mediaPlayer!!.start()
            progressPublisher.resume()
        }
    }

    fun pause() {
        mediaPlayer?.pause()
        progressPublisher.pause()
    }

    fun resetMediaPlayer() {
        mediaPlayer?.stop()
        mediaPlayer?.reset()
    }

    fun prepareSongAndPlay(path: String) {
        mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
        try {
            mediaPlayer!!.setDataSource(path)
        } catch (e: IOException) {
            e.printStackTrace()
            stopSelf()
        }
        mediaPlayer!!.prepareAsync()
    }

    fun getCurrentPosition(): Int {
        if (mediaPlayer != null) {
            return mediaPlayer!!.currentPosition
        }
        return 0
    }

    fun computePassedDuration(): Float {
        if (mediaPlayer != null) {
            val duration = mediaPlayer!!.duration.toFloat()
            val current = mediaPlayer!!.currentPosition.toFloat()
            return (current / duration)
        }
        return 0f
    }

    /**
     * User removed task from task list
     * so stop service and onDestroy will be called
     */
    override fun onTaskRemoved(rootIntent: Intent?) {
        stopSelf()
        super.onTaskRemoved(rootIntent)
    }

    /**
     * Service terminated
     */
    override fun onDestroy() {
        super.onDestroy()
        releaseResource()
    }

    private fun releaseResource() {
        disposables.clear()
        mediaSession.setCallback(null)
        mediaController.unregisterCallback(mediaControllerCallback)
        mediaControllerCallback.removeNowPlayingNotification()
        if (App.get().isNormalIntent) {
            saveCurrentSong()
        }
        mediaPlayer!!.release()

        QueueManager.get().finishObserver?.dispose()
        unregisterReceiver(audioNoisyReceiver)
        isActive = false
    }

    private fun saveCurrentSong() {
        //if user or system want kill app then force pause song
        manager.queue?.selected?.let { song ->
            song.status = SongStatus.PAUSE
            UserPref.saveQueueData(manager.queue!!)
        }
    }

    private fun updateMediaSessionPlaybackState(stateBuilder: PlaybackStateCompat.Builder, state: Int) {
        fun createPlayBackAction(state: Int): Long {
            return if (state == PlaybackStateCompat.STATE_PLAYING) {
                PlaybackStateCompat.ACTION_PLAY_PAUSE or PlaybackStateCompat.ACTION_PAUSE or PlaybackStateCompat.ACTION_SKIP_TO_NEXT or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
            } else {
                PlaybackStateCompat.ACTION_PLAY_PAUSE or PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_SKIP_TO_NEXT or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
            }
        }
        stateBuilder.setActions(createPlayBackAction(state))
        stateBuilder.setState(state, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 0f)
        mediaSession.setPlaybackState(stateBuilder.build())
    }

    companion object {
        var isActive = false

        fun tag(): String {
            return MediaPlayerService::class.java.simpleName
        }
    }
}
