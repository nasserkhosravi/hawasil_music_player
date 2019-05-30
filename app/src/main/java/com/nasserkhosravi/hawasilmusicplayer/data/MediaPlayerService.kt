package com.nasserkhosravi.hawasilmusicplayer.data

import android.app.Service
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import com.nasserkhosravi.hawasilmusicplayer.data.model.SongStatus
import io.reactivex.android.schedulers.AndroidSchedulers
import java.io.IOException
import java.util.concurrent.TimeUnit

class MediaPlayerService : Service(), MediaPlayer.OnPreparedListener {
    private val iBinder = LocalBinder()
    //inject
    private var mediaPlayer: MediaPlayer? = null
    //inject
    var progressPublisher = SuspendableObservable(10, TimeUnit.MILLISECONDS)
    //inject
    private lateinit var audioNoisyReceiver: AudioNoisyReceiver

    override fun onCreate() {
        super.onCreate()
        isActive = true
        progressPublisher.observable =
            progressPublisher.observable.observeOn(AndroidSchedulers.mainThread()).map { computePassedDuration() }
        initMediaPlayer()

        audioNoisyReceiver = AudioNoisyReceiver()
        registerReceiver(audioNoisyReceiver, AudioNoisyReceiver.createIntentFilter())
    }

    override fun onPrepared(mp: MediaPlayer?) {
        playFromLastPosition()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return iBinder
    }

    private fun initMediaPlayer() {
        if (mediaPlayer == mediaPlayer) {
            mediaPlayer = MediaPlayer()
        }
        mediaPlayer!!.setOnPreparedListener(this)
    }

    fun playFromLastPosition() {
        if (!mediaPlayer!!.isPlaying) {
            //todo:song passed dependency, remove it
            mediaPlayer?.seekTo(MediaTerminal.queue.selected!!.passedDuration.toInt())
            mediaPlayer!!.start()
            progressPublisher.resume()
        }
    }

    fun pause() {
        mediaPlayer?.pause()
        progressPublisher.pause()
    }

    fun resetMediaPlayer() {
        mediaPlayer!!.stop()
        mediaPlayer!!.reset()
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

    fun seekTo(progress: Int) {
        mediaPlayer?.seekTo(progress)
    }

    fun getCurrentPosition(): Int {
        return mediaPlayer!!.currentPosition
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        //if user or system want kill app then force pause song
        MediaTerminal.queue.selected!!.status = SongStatus.PAUSE
        UserPref.saveQueueData(MediaTerminal.queue)
        release()
        super.onTaskRemoved(rootIntent)
    }

    private fun computePassedDuration(): Float {
        val duration = mediaPlayer!!.duration.toFloat()
        val current = mediaPlayer!!.currentPosition.toFloat()
        return (current / duration)
    }

    fun isReadyToComputePassedDuration(): Boolean {
        return mediaPlayer!!.currentPosition > 0
    }

    fun release() {
        mediaPlayer?.release()
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(audioNoisyReceiver)
        isActive = false
    }

    inner class LocalBinder : Binder() {
        val service: MediaPlayerService
            get() = this@MediaPlayerService
    }

    companion object {
        var isActive = false

        fun tag(): String {
            return MediaPlayerService::class.java.simpleName
        }
    }
}
