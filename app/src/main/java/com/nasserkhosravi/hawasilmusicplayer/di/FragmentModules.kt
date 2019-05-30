package com.nasserkhosravi.hawasilmusicplayer.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.nasserkhosravi.hawasilmusicplayer.view.adapter.*
import com.nasserkhosravi.hawasilmusicplayer.viewmodel.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [RecycleItemListenerModule::class])
class FoldersFragmentModule(val fragment: Fragment) {
    @Singleton
    @Provides
    fun provideAdapter() = FolderAdapter()

    @Singleton
    @Provides
    fun provideViewModel() = ViewModelProviders.of(fragment).get(FoldersViewModel::class.java)
}

@Module(includes = [RecycleItemListenerModule::class])
class AlbumsFragmentModule(val fragment: Fragment) {
    @Singleton
    @Provides
    fun provideAdapter() = AlbumAdapter()

    @Singleton
    @Provides
    fun provideViewModel() = ViewModelProviders.of(fragment).get(AlbumsViewModel::class.java)
}

@Module(includes = [RecycleItemListenerModule::class])
class ArtistFragmentModule(val fragment: Fragment) {

    @Singleton
    @Provides
    fun provideAdapter() = ArtistAdapter()

    @Singleton
    @Provides
    fun provideViewModel() = ViewModelProviders.of(fragment).get(ArtistsViewModel::class.java)
}

@Module(includes = [RecycleItemListenerModule::class])
class PlayListsFragmentModule(val fragment: Fragment) {

    @Singleton
    @Provides
    fun provideAdapter() = PlayListAdapter()

    @Singleton
    @Provides
    fun provideViewModel() = ViewModelProviders.of(fragment).get(PlayListsViewModel::class.java)
}

@Module(includes = [RecycleItemListenerModule::class])
class QueueFragmentModule(val fragment: Fragment) {
    @Provides
    fun provideAdapter() = QueueAdapter()

    @Singleton
    @Provides
    fun provideViewModel() = ViewModelProviders.of(fragment).get(QueueViewModel::class.java)
}

@Module(includes = [RecycleItemListenerModule::class, CompositeDisposableModule::class])
class SongPlayerModule(val fragment: Fragment) {

    @Singleton
    @Provides
    fun provideViewModel() = ViewModelProviders.of(fragment).get(SongPlayerViewModel::class.java)
}

@Module(includes = [RecycleItemListenerModule::class, CompositeDisposableModule::class])
class MiniPlayerFragmentModule(val fragment: Fragment) {

    @Singleton
    @Provides
    fun provideViewModel() = ViewModelProviders.of(fragment).get(MiniPlayerViewModel::class.java)
}