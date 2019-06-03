package com.nasserkhosravi.hawasilmusicplayer.data

import android.os.Bundle
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.nasserkhosravi.hawasilmusicplayer.data.model.SongModel

/**
 * responsible to UI command of app
 */
object UIMediaCommand {

    private var controllerCompat: MediaControllerCompat? = null

    fun setController(controller: MediaControllerCompat?) {
        this.controllerCompat = controller
    }

    fun processQueue(items: ArrayList<SongModel>, position: Int, queueId: String) {
        val bundle = Bundle()
        bundle.putParcelableArrayList(Constants.ITEMS, items)
        bundle.putInt(Constants.POSITION, position)
        bundle.putString(Constants.QUEUE_ID, queueId)
        QueueManager.get().requestedQueueBundle = bundle
        controllerCompat!!.sendCommand(MediaPlayerCommands.PROCESS_QUEUE, null, null)
    }

    fun playNext() {
        controllerCompat!!.transportControls.skipToNext()
    }

    fun playPrevious() {
        controllerCompat!!.transportControls.skipToPrevious()
    }

    fun resume() {
        controllerCompat!!.transportControls.play()
    }

    fun pause() {
        controllerCompat!!.transportControls.pause()
    }

    fun nextRepeatMode() {
        val mode = when (controllerCompat!!.repeatMode) {
            PlaybackStateCompat.REPEAT_MODE_NONE -> {
                PlaybackStateCompat.REPEAT_MODE_ALL
            }
            PlaybackStateCompat.REPEAT_MODE_ALL -> {
                PlaybackStateCompat.REPEAT_MODE_NONE
            }
            else -> {
                throw IllegalStateException()
            }
        }
        controllerCompat!!.transportControls.setRepeatMode(mode)

    }

    fun togglePlay() {
        if (controllerCompat!!.playbackState.state == PlaybackStateCompat.STATE_PLAYING) {
            controllerCompat!!.transportControls.pause()
        } else {
            controllerCompat!!.transportControls.play()
        }
    }

    fun toggleShuffle() {
        val mode = when (controllerCompat!!.shuffleMode) {
            PlaybackStateCompat.SHUFFLE_MODE_NONE -> {
                PlaybackStateCompat.SHUFFLE_MODE_ALL
            }
            else -> {
                PlaybackStateCompat.SHUFFLE_MODE_NONE
            }
        }
        controllerCompat!!.transportControls.setShuffleMode(mode)
    }

    fun seekTo(progress: Int) {
        controllerCompat!!.transportControls.seekTo(progress.toLong())
    }
}