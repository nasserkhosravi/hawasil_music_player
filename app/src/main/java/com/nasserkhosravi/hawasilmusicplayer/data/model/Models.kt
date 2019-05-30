package com.nasserkhosravi.hawasilmusicplayer.data.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Holds small model
 */
@Parcelize
data class FlatFolderModel(val name: String, val path: String) : Parcelable {
    var songs: ArrayList<Long>? = null
}

@Parcelize
data class ArtistModel(val id: Int, val title: String) : Parcelable

@Parcelize
data class AlbumModel(val id: Int, val title: String, val artist: String, val art: String?) : Parcelable {
    var artWorkUri: Uri? = null
}

@Parcelize
data class PlayListModel(val id: Long, val title: String, val members: ArrayList<Pair<Long, Uri?>>) : Parcelable {

    fun get4Artworks(): ArrayList<Pair<Long, Uri>> {
        val result = ArrayList<Pair<Long, Uri>>()
        for (pair in members) {
            if (result.size == 4) {
                break
            }
            if (pair.second != null) {
                result.add(pair as Pair<Long, Uri>)
            }
        }
        return result
    }

    override fun toString(): String {
        return "PlayListModel(id=$id, title='$title', members=$members)"
    }


}

enum class QueueType(val id: Int) {
    UN_KNOWN(0), SONGS(1), ALBUM(2), PLAYLIST(3), FOLDER(4), ARTIST(5);

    fun toInt() = id

    companion object {
        fun fromId(id: Int) = QueueType.values().first { it.id == id }
    }
}