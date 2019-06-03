package com.nasserkhosravi.hawasilmusicplayer.data

import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.nasserkhosravi.hawasilmusicplayer.data.model.SongModel

class MediaSessionCallBack(private val manager: QueueManager) : MediaSessionCompat.Callback() {
    override fun onPlay() {
        super.onPlay()
        manager.resume()
    }

    override fun onPause() {
        super.onPause()
        manager.pause()
    }

    override fun onSeekTo(pos: Long) {
        super.onSeekTo(pos)
        manager.seekTo(pos)
    }

    override fun onSkipToPrevious() {
        super.onSkipToPrevious()
        manager.playPrevious()
    }

    override fun onSkipToNext() {
        super.onSkipToNext()
        manager.playNext()
    }

    override fun onCommand(command: String?, extras: Bundle?, cb: ResultReceiver?) {
        super.onCommand(command, extras, cb)
        //WTF: always extras is null
        when (command!!) {
            MediaPlayerCommands.PROCESS_QUEUE -> {
                val bundle = manager.requestedQueueBundle
                val position = bundle!!.getInt(Constants.POSITION)
                val queueId = bundle[Constants.QUEUE_ID] as String
                val items = bundle.getParcelableArrayList<SongModel>(Constants.ITEMS)!!
                manager.processRequest(items, position, queueId)
                manager.requestedQueueBundle = null
            }
        }
    }

    override fun onSetShuffleMode(shuffleMode: Int) {
        super.onSetShuffleMode(shuffleMode)
        when (shuffleMode) {
            PlaybackStateCompat.SHUFFLE_MODE_ALL -> {
                manager.setShuffle(true)
            }
            PlaybackStateCompat.SHUFFLE_MODE_NONE -> {
                manager.setShuffle(false)
            }
            else -> {
                //log un supported situation
            }
        }
    }

    override fun onSetRepeatMode(repeatMode: Int) {
        super.onSetRepeatMode(repeatMode)
        when (repeatMode) {
            PlaybackStateCompat.REPEAT_MODE_ALL -> {
                manager.setRepeatMode(true)
            }
            PlaybackStateCompat.REPEAT_MODE_NONE -> {
                manager.setRepeatMode(false)
            }
            else -> {
                //log un supported situation
            }
        }
    }
}