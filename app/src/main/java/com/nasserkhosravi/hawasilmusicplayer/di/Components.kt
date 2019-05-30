package com.nasserkhosravi.hawasilmusicplayer.di

import com.nasserkhosravi.hawasilmusicplayer.app.App
import com.nasserkhosravi.hawasilmusicplayer.data.MediaPlayerService
import com.nasserkhosravi.hawasilmusicplayer.data.MediaTerminal
import com.nasserkhosravi.hawasilmusicplayer.view.fragment.*
import dagger.Component
import javax.inject.Singleton

@Component(modules = [AppModule::class])
@Singleton
interface AppComponent {
    fun inject(app: App)
}

@Component(modules = [ArtistFragmentModule::class])
@Singleton
interface ArtistsFragmentComponent {
    fun inject(fragment: ArtistsFragment)
}

@Component(modules = [AlbumsFragmentModule::class])
@Singleton
interface AlbumsFragmentComponent {
    fun inject(fragment: AlbumsFragment)
}

@Component(modules = [PlayListsFragmentModule::class])
@Singleton
interface PlayListsFragmentComponent {
    fun inject(fragment: PlayListsFragment)
}

@Component(modules = [QueueFragmentModule::class])
@Singleton
interface QueueFragmentComponent {
    fun inject(fragment: QueueFragment)
}

@Component(modules = [FoldersFragmentModule::class])
@Singleton
interface FoldersFragmentComponent {
    fun inject(fragment: FoldersFragment)
}

@Component(modules = [MediaPlayerServiceModule::class])
@Singleton
interface MediaPlayerServiceComponent {
    fun inject(to: MediaPlayerService)
}

@Component(modules = [MediaTerminalModule::class])
@Singleton
interface MediaTerminalComponent {
    fun inject(to: MediaTerminal)
}

@Component(modules = [SongPlayerModule::class])
@Singleton
interface SongPlayerFragmentComponent {
    fun inject(to: SongPlayerFragment)
}

@Component(modules = [MiniPlayerFragmentModule::class])
@Singleton
interface MiniPlayerFragmentComponent {
    fun inject(to: MiniPlayerFragment)
}

