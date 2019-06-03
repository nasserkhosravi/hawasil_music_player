package com.nasserkhosravi.hawasilmusicplayer.di

import android.app.Application
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media.AudioAttributesCompat
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.nasserkhosravi.appcomponent.view.adapter.BaseComponentAdapter
import com.nasserkhosravi.hawasilmusicplayer.MediaStyleNotificationBuilder
import com.nasserkhosravi.hawasilmusicplayer.data.*
import com.nasserkhosravi.hawasilmusicplayer.data.audio.AudioFocusHelper
import com.nasserkhosravi.hawasilmusicplayer.data.audio.AudioFocusRequestCompat
import com.nasserkhosravi.hawasilmusicplayer.data.model.SongModel
import com.nasserkhosravi.hawasilmusicplayer.data.model.SongModelAdapter
import com.nasserkhosravi.hawasilmusicplayer.view.adapter.RecycleItemListener
import dagger.Module
import dagger.Provides
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application
    }

    @Singleton
    @Provides
    fun provideGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(
                SongModel::class.java,
                SongModelAdapter()
            )
            .setPrettyPrinting()
            .create()
    }
}

@Module
class AudioFocusModule(
    private val context: Context,
    private val audioFocusChangeListener: AudioManager.OnAudioFocusChangeListener
) {
    @Singleton
    @Provides
    fun provideAudioFocus() = AudioFocusHelper(context)

    @Singleton
    @Provides
    fun provideAudioFocusRequest(): AudioFocusRequestCompat {
        val audioAttributesCompat = AudioAttributesCompat.Builder()
            .setUsage(AudioAttributesCompat.USAGE_MEDIA)
            .setContentType(AudioAttributesCompat.CONTENT_TYPE_MUSIC)
            .build()

        return AudioFocusRequestCompat.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setOnAudioFocusChangeListener(audioFocusChangeListener)
            .setAudioAttributes(audioAttributesCompat)
            .build()
    }
}

@Module
class MediaPlayerServiceModule(private val service: MediaPlayerService) {

    @Singleton
    @Provides
    fun provideAudioNoiseReceiver() = AudioNoisyReceiver()

    @Singleton
    @Provides
    fun provideProgressPublisher(): SuspendableObservable {
        val progressPublisher = SuspendableObservable(10, TimeUnit.MILLISECONDS)
        progressPublisher.observable =
            progressPublisher.observable.observeOn(AndroidSchedulers.mainThread()).map { service.computePassedDuration() }
        return progressPublisher
    }

    @Singleton
    @Provides
    fun provideMediaPlayer() = MediaPlayer()
}

@Module
class MediaSessionModule(
    private val service: Service,
    private val mediaSessionTag: String
) {
    @Singleton
    @Provides
    fun provideMediaSessionCompat(mediaSessionCallBack: MediaSessionCallBack): MediaSessionCompat {
        val sessionActivityPendingIntent =
            service.packageManager?.getLaunchIntentForPackage(service.packageName)?.let { sessionIntent ->
                PendingIntent.getActivity(service, 0, sessionIntent, 0)
            }
        return MediaSessionCompat(service, mediaSessionTag)
            .apply {
                setSessionActivity(sessionActivityPendingIntent)
                isActive = true
                setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
                setCallback(mediaSessionCallBack)
                if (QueueManager.get().queue.isShuffled) {
                    setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_ALL)
                }
                if (QueueManager.get().queue.isEnableRepeat) {
                    setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ALL)
                }
            }
    }

    @Singleton
    @Provides
    fun provideMediaSessionCallBack() = MediaSessionCallBack(QueueManager.get())

    @Singleton
    @Provides
    fun providePlayBackState(): PlaybackStateCompat.Builder {
        return PlaybackStateCompat.Builder().setActions(
            PlaybackStateCompat.ACTION_PLAY_PAUSE
                    or PlaybackStateCompat.ACTION_PLAY
                    or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                    or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
        )
    }

    @Singleton
    @Provides
    fun provideMediaController(mediaSession: MediaSessionCompat) = MediaControllerCompat(service, mediaSession)

    @Singleton
    @Provides
    fun provideMediaControllerCallBack(
        mediaSession: MediaSessionCompat,
        mediaController: MediaControllerCompat,
        notificationBuilder: MediaStyleNotificationBuilder,
        notificationManager: NotificationManagerCompat
    ): MediaControllerCallBack {
        return MediaControllerCallBack(service, mediaSession, mediaController, notificationBuilder, notificationManager)
    }

}

@Module
class NotificationModule(val context: Context) {

    @Singleton
    @Provides
    fun provideBuilder() = MediaStyleNotificationBuilder(context)

    @Singleton
    @Provides
    fun provideManager() = NotificationManagerCompat.from(context)
}

@Module
class RecycleItemListenerModule(private val context: Context, private val listener: BaseComponentAdapter.ItemClickListener) {

    @Singleton
    @Provides
    fun providerRecycleItemListener() = RecycleItemListener(context, listener)

}

@Module
class CompositeDisposableModule {
    @Provides
    fun provide() = CompositeDisposable()
}