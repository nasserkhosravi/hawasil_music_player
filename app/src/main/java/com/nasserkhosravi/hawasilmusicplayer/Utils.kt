package com.nasserkhosravi.hawasilmusicplayer

import android.Manifest
import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.provider.MediaStore
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.annotation.DimenRes
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.media.session.MediaButtonReceiver
import com.nasserkhosravi.appcomponent.AppContext
import com.nasserkhosravi.hawasilmusicplayer.data.NOW_PLAYING_CHANNEL
import com.nasserkhosravi.hawasilmusicplayer.data.model.SongModel
import java.util.concurrent.TimeUnit

/**
 * Helper class to encapsulate code for building notifications.
 */
class MediaStyleNotificationBuilder(private val context: Context) {
    private val platformNotificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val skipToPreviousAction = NotificationCompat.Action(
        R.drawable.ic_skip_previous_black_24dp,
        context.getString(R.string.notification_skip_to_previous),
        MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
    )

    private val playAction = NotificationCompat.Action(
        R.drawable.ic_play_arrow_black_24dp,
        context.getString(R.string.notification_play),
        MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_PLAY)
    )

    private val pauseAction = NotificationCompat.Action(
        R.drawable.ic_pause_black_24dp,
        context.getString(R.string.notification_pause),
        MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_PAUSE)
    )

    private val skipToNextAction = NotificationCompat.Action(
        R.drawable.ic_skip_next_black_24dp,
        context.getString(R.string.notification_skip_to_next),
        MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_SKIP_TO_NEXT)
    )

    private val stopPendingIntent =
        MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_STOP)

    fun buildNotification(sessionToken: MediaSessionCompat.Token): Notification {
        if (shouldCreateNowPlayingChannel()) {
            createNowPlayingChannel()
        }

        val controller = MediaControllerCompat(context, sessionToken)
        val description = controller.metadata.description
        val playbackState = controller.playbackState

        val builder = NotificationCompat.Builder(context, NOW_PLAYING_CHANNEL)

        builder.addAction(skipToPreviousAction)
        if (playbackState.state == PlaybackStateCompat.STATE_PLAYING) {
            builder.addAction(pauseAction)
        } else if (playbackState.state == PlaybackStateCompat.STATE_PAUSED) {
            builder.addAction(playAction)
        }
        builder.addAction(skipToNextAction)

        val mediaStyle = androidx.media.app.NotificationCompat.MediaStyle()
            .setCancelButtonIntent(stopPendingIntent)
            .setMediaSession(sessionToken)
            .setShowActionsInCompactView(0, 1, 2)
            .setShowCancelButton(true)

        return builder.setContentIntent(controller.sessionActivity)
            .setContentText(description.subtitle)
            .setContentTitle(description.title)
            .setDeleteIntent(stopPendingIntent)
            .setLargeIcon(description.iconBitmap)
            .setOnlyAlertOnce(true)
            .setSmallIcon(R.drawable.default_icon)
            .setStyle(mediaStyle)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    private fun shouldCreateNowPlayingChannel() =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !nowPlayingChannelExists()

    @RequiresApi(Build.VERSION_CODES.O)
    private fun nowPlayingChannelExists() =
        platformNotificationManager.getNotificationChannel(NOW_PLAYING_CHANNEL) != null

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNowPlayingChannel() {
        val notificationChannel = NotificationChannel(
            NOW_PLAYING_CHANNEL,
            context.getString(R.string.notification_channel),
            NotificationManager.IMPORTANCE_LOW
        )
            .apply {
                description = context.getString(R.string.notification_channel_description)
            }

        platformNotificationManager.createNotificationChannel(notificationChannel)
    }
}

object FormatUtils {
    fun milliSeconds(duration: Long): String {
        return String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(duration),
            TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
        )
    }

    fun toSecond(p: Long): Long {
        return TimeUnit.MILLISECONDS.toSeconds(p)
    }
}

object PermissionUtils {

    fun hasPermissions(context: Context, vararg permissions: String): Boolean {
        var result = true
        permissions.forEach {
            if (ActivityCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED) {
                result = false
            }
        }
        return result
    }

    fun hasPermission(context: Context, permission: String): Boolean {
        val res = context.checkCallingOrSelfPermission(permission)
        return res == PackageManager.PERMISSION_GRANTED
    }

    fun on_Has_HasNot(value: Boolean, has: () -> Unit, hasNot: () -> Unit) {
        if (value) {
            has()
        } else {
            hasNot()
        }
    }

    fun requestPermission(activity: Activity, permission: String, requestCode: Int) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(permission),
            requestCode
        )
    }

    fun requestPermission(activity: Activity, requestCode: Int, vararg permission: String) {
        ActivityCompat.requestPermissions(
            activity,
            permission,
            requestCode
        )
    }

    fun requestWritePermission(activity: Activity) {
        requestPermission(
            activity,
            getWritePermissionCode(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    fun requestPhoneState(activity: Activity) {
        requestPermission(activity, Manifest.permission.READ_PHONE_STATE, 10)
    }

    fun requestReadPermission(activity: Activity) {
        requestPermission(
            activity,
            getReadPermissionCode(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }


    fun hasWritePermission(context: Context): Boolean {
        val res = context.checkCallingOrSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return res == PackageManager.PERMISSION_GRANTED
    }

    fun hasReadPermission(context: Context): Boolean {
        val res = context.checkCallingOrSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        return res == PackageManager.PERMISSION_GRANTED
    }

    fun hasCameraPermission(context: Context): Boolean {
        return hasPermission(context, Manifest.permission.CAMERA)
    }

    fun getWritePermissionCode() = 100

    fun getReadPermissionCode() = 101
}

object MyRes {
    fun getDim(@DimenRes dim: Int): Float {
        return AppContext.get().resources.getDimension(dim)
    }
}

class TimeMeasure {
    private var start = 0L
    private var end = 0L

    fun start() {
        if (start == 0L) {
            start = System.currentTimeMillis()
        }
    }

    fun end() {
        if (end == 0L) {
            end = System.currentTimeMillis()
        }
    }

    fun reset() {
        start = 0
        end = 0
    }

    fun getDiff(): Long {
        return end - start
    }

    fun printDiff(tag: String) {
        Log.d(tag, "${getDiff()}")
    }
}

fun SongModel.getMediaMetaData(context: Context): MediaMetadataCompat {
    val metadataBuilder = MediaMetadataCompat.Builder()
        .putBitmap(
            MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON,
            BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher)
        )
        .putBitmap(
            MediaMetadataCompat.METADATA_KEY_ALBUM_ART,
            MediaStore.Images.Media.getBitmap(context.contentResolver, artUri)
        )
    metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
    metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
    return metadataBuilder.build()!!
}