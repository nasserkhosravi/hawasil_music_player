package com.nasserkhosravi.hawasilmusicplayer.data

import com.nasserkhosravi.hawasilmusicplayer.data.model.SongModel

object FavoriteManager {
    //    todo: take notice user can remove playlist from other app
    private const val NAME_OF_PLAYLIST = "Hawasil favorite"
    //never use this playlist id, this used for cache
    var playlistId = -1L
        private set

    fun isFavorite(songId: Long): Boolean {
        return MediaProvider.isExistInPlaylist(songId, getIdOrCreate())
    }

    fun remove(songId: Long): Boolean {
        return MediaProvider.removeFromPlayList(songId, getIdOrCreate())
    }

    fun add(songId: Long): Boolean {
        return MediaProvider.addToPlayList(songId, getIdOrCreate())
    }

    fun fetchAll(): ArrayList<SongModel> {
        return MediaProvider.getPlaylistTracks(getIdOrCreate())
    }

    private fun getIdOrCreate(): Long {
        if (!isCachedPlaylistId()) {
            val fetchedPlaylistId = MediaProvider.isExistPlayList(NAME_OF_PLAYLIST)
            if (fetchedPlaylistId == -1L) {
                val createdPlaylistId = MediaProvider.createPlaylist(NAME_OF_PLAYLIST)
                if (createdPlaylistId == -1L) {
                    throw IllegalStateException("Problem in creating hawasil playlist")
                } else {
                    playlistId = createdPlaylistId
                }
            } else {
                playlistId = fetchedPlaylistId
            }
        }
        return playlistId
    }

    private fun isCachedPlaylistId() = playlistId > -1L

    fun reset() {
        playlistId = -1L
    }
}