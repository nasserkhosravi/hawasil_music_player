package com.nasserkhosravi.hawasilmusicplayer.data

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.google.gson.annotations.Expose
import com.nasserkhosravi.appcomponent.AppContext
import com.nasserkhosravi.hawasilmusicplayer.FormatUtils.toSecond
import com.nasserkhosravi.hawasilmusicplayer.app.App
import com.nasserkhosravi.hawasilmusicplayer.data.model.SongModel
import com.nasserkhosravi.hawasilmusicplayer.data.model.SongStatus
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject

object QueueBrain {
    var data = QueueData()
        private set

    private var isServiceBound = false
    private var finishObserver: Disposable? = null
    var playerService: MediaPlayerService? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            playerService = (service as MediaPlayerService.LocalBinder).service
            isServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            isServiceBound = false
        }
    }

    fun processRequest(items: List<SongModel>, position: Int, queueId: String) {
        if (isNewQueue(queueId)) {
            data.reset()
            data.queue.addAll(items)
            data.queueId = queueId
            replaceAndPlay(position)
        } else {
            //old queue, just check new song
            //we ignore if song is equal to current song
            val newSong = items[position]
            if (data.selected?.id != newSong.id) {
                finishObserver?.dispose()
                replaceAndPlay(position)
            }
        }
    }

    private fun replaceAndPlay(position: Int) {
        if (data.selected != null) {
            if (data.selectedIndex > -1) {
                data.queue[data.selectedIndex].reset()
            }
        }
        //replace
        data.selected = data.queue[position]
        data.selected!!.status = SongStatus.PLAYING

        ///play
        data.selectedIndex = position
        playerService!!.resetMediaPlayer()
        playerService!!.prepareSongAndPlay(data.selected!!.path)
        SongEventPublisher.newSongPlay.onNext(data.selected!!)
        registerFinishListener()
    }

    private fun registerFinishListener() {
        finishObserver = playerService!!.progressPublisher.observable.subscribe {
            data.selected!!.songPassed = playerService!!.getCurrentPosition().toLong()
            if (isCompletedCurrent()) {
                onSongCompletedEvent()
                finishObserver?.dispose()
            }
        }
    }

    fun playNext() {
        if (isLastSong()) {
            replaceAndPlay(0)
        } else {
            replaceAndPlay(data.selectedIndex + 1)
        }
    }

    fun playPrevious() {
        if (isFirstSong()) {
            replaceAndPlay(data.queue.lastIndex)
        } else {
            replaceAndPlay(data.selectedIndex - 1)
        }
    }

    fun resume() {
        registerFinishListener()
        data.selected!!.status = SongStatus.PLAYING
        if (data.isSongRestored) {
            playerService!!.resetMediaPlayer()
            playerService!!.prepareSongAndPlay(data.selected!!.path)
            data.isSongRestored = false
        } else {
            playerService!!.playFromLastPosition()
        }
        SongEventPublisher.songStatusChange.onNext(SongStatus.PLAYING)
    }

    fun pause() {
        finishObserver?.dispose()
        playerService?.pause()
        data.selected?.status = SongStatus.PAUSE
        SongEventPublisher.songStatusChange.onNext(SongStatus.PAUSE)
    }

    fun toggleRepeat() {
        data.isEnableRepeat = !data.isEnableRepeat
    }

    fun togglePlay() {
        if (data.selected!!.isPlaying()) {
            pause()
        } else {
            resume()
        }
    }

    fun toggleShuffle() {
        data.isShuffle = !data.isShuffle
        if (data.isShuffle) {
            makeShuffle()
        } else {
            makeUnShuffle()
        }
        SongEventPublisher.shuffleModeChange.onNext(data.isShuffle)
    }

    private fun makeShuffle() {
        data.queue.shuffle()
    }

    private fun makeUnShuffle() {
        data.queue.sortBy { it.title }
    }

    fun seekTo(progress: Int) {
        playerService!!.seekTo(progress)
        data.selected!!.songPassed = progress.toLong()
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

    private fun onSongCompletedEvent() {
        data.selected?.status = SongStatus.PAUSE
        data.selected?.songPassed = 0
        SongEventPublisher.songComplete.onNext(Any())
        if (hasNextSong()) {
            playNext()
        } else {
            if (shouldStartFromFirst()) {
                replaceAndPlay(0)
            }
        }
    }

    private fun isCompletedCurrent(): Boolean {
        return toSecond(playerService!!.getCurrentPosition().toLong()) == toSecond(data.selected!!.duration)
    }

    private fun isNewQueue(queueId: String): Boolean {
        return data.queueId != queueId
    }

    private fun isFirstSong() = data.selectedIndex == 0

    private fun isSingleSong(): Boolean {
        return data.queue.size == 1
    }

    private fun shouldStartFromFirst(): Boolean {
        if (isSingleSong() || isLastSong()) {
            if (data.isEnableRepeat) {
                return true
            }
        }
        return false
    }

    private fun isLastSong() = data.selectedIndex == data.queue.lastIndex

    private fun hasNextSong() = data.selectedIndex < data.queue.lastIndex

    fun setData(data: QueueData) {
        this.data = data
    }

    fun tag(): String {
        return QueueBrain::class.java.simpleName
    }
}

class QueueData {
    var isSongRestored = false

    @Expose
    var selected: SongModel? = null
    @Expose
    val queue = ArrayList<SongModel>()
    @Expose
    var selectedIndex = -1
    @Expose
    var queueId = ""
    @Expose
    var isEnableRepeat = false
    @Expose
    var isShuffle = false

    fun toJson(): String {
        return App.get().jsonAdapter.toJson(this)
    }

    companion object {
        fun fromJson(json: String): QueueData {
            val fromJson = App.get().jsonAdapter.fromJson(json, QueueData::class.java)
            val model = QueueData()
            model.queue.addAll(fromJson.queue)
            model.selected = fromJson.selected
            model.isShuffle = fromJson.isShuffle
            model.selectedIndex = fromJson.selectedIndex
            model.queueId = fromJson.queueId
            model.isEnableRepeat = fromJson.isEnableRepeat
            return model
        }
    }

    fun reset() {
        isSongRestored = false
        queue.clear()
        selectedIndex = -1
        queueId = ""
        isShuffle = false
        isEnableRepeat = false
        selected = null
    }
}

object SongEventPublisher {

    val newSongPlay = PublishSubject.create<SongModel>()
    val songComplete = PublishSubject.create<Any>()
    val songStatusChange = PublishSubject.create<SongStatus>()
    val shuffleModeChange = PublishSubject.create<Boolean>()

    init {
        newSongPlay.publish().autoConnect()
        songComplete.publish().autoConnect()
        songStatusChange.publish().autoConnect()
    }
}