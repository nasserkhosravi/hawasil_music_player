package com.nasserkhosravi.hawasilmusicplayer.data

import android.app.Service
import android.content.Intent
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.nasserkhosravi.hawasilmusicplayer.MediaStyleNotificationBuilder

class MediaControllerCallBack(
    private val service: Service,
    private val mediaSession: MediaSessionCompat,
    private val mediaController: MediaControllerCompat,
    private val notificationBuilder: MediaStyleNotificationBuilder,
    private val notificationManager: NotificationManagerCompat
) : MediaControllerCompat.Callback() {
    private var isForegroundService = false

    override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
        mediaController.playbackState?.let { updateNotification(it) }
    }

    override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
        state?.let { updateNotification(it) }
    }

    private fun updateNotification(state: PlaybackStateCompat) {
        val updatedState = state.state
        // Skip building a notification when state is "none" and metadata is null.
        val notification = if (mediaController.metadata != null
            && updatedState != PlaybackStateCompat.STATE_NONE
        ) {
            notificationBuilder.buildNotification(mediaSession.sessionToken)
        } else {
            null
        }
        when (updatedState) {
            PlaybackStateCompat.STATE_BUFFERING,
            PlaybackStateCompat.STATE_PLAYING -> {
                if (notification != null) {
                    notificationManager.notify(NOW_PLAYING_NOTIFICATION, notification)
                    if (!isForegroundService) {
                        ContextCompat.startForegroundService(
                            service.applicationContext,
                            Intent(service.applicationContext, service.javaClass)
                        )
                        service.startForeground(NOW_PLAYING_NOTIFICATION, notification)
                        isForegroundService = true
                    }
                }
            }
            else -> {
                if (isForegroundService) {
                    service.stopForeground(false)
                    isForegroundService = false
                    if (updatedState == PlaybackStateCompat.STATE_NONE) {
                        service.stopSelf()
                    }
                    if (notification != null) {
                        notificationManager.notify(NOW_PLAYING_NOTIFICATION, notification)
                    } else {
                        removeNowPlayingNotification()
                    }
                }
            }
        }
    }

    fun removeNowPlayingNotification() {
        service.stopForeground(true)
    }

}