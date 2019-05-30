package com.nasserkhosravi.hawasilmusicplayer.di

import android.app.Application
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import androidx.media.AudioAttributesCompat
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.nasserkhosravi.appcomponent.view.adapter.BaseComponentAdapter
import com.nasserkhosravi.hawasilmusicplayer.data.AudioNoisyReceiver
import com.nasserkhosravi.hawasilmusicplayer.data.MediaPlayerService
import com.nasserkhosravi.hawasilmusicplayer.data.SuspendableObservable
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
class MediaTerminalModule(
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