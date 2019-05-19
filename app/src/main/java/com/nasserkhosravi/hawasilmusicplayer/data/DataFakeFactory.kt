package com.nasserkhosravi.hawasilmusicplayer.data

import com.nasserkhosravi.hawasilmusicplayer.data.model.*

object DataFakeFactory {
    fun getSongs(): ArrayList<SongModel> {
        val m1 = SongModel(
            1L,
            "",
            "Shafaf",
            "Taham",
            "Degaran",
            100
        )
        return arrayListOf(m1)
    }

    fun getArtist(): List<ArtistModel> {
        val taham = ArtistModel(1, "Taham")
        return arrayListOf(taham)
    }

    fun getFolders(): List<FlatFolderModel> {
        return arrayListOf(
            FlatFolderModel("folder1", "")
        )
    }

    fun getAlbums(): List<AlbumModel> {
        return arrayListOf(
            AlbumModel(1, "Degaran", "Taham", "")
        )
    }

    fun getPlayLists(): List<PlayListModel> {
        return listOf(PlayListModel(1, "Hip Hop", arrayListOf()))
    }
}