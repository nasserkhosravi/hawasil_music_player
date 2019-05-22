package com.nasserkhosravi.hawasilmusicplayer.data

import android.database.Cursor
import android.provider.MediaStore
import com.nasserkhosravi.hawasilmusicplayer.data.model.ArtistModel

object ArtistCache {
    var artists: HashSet<ArtistModel>? = null
        private set

    fun cache(cursor: Cursor) {
        if (artists == null) {
            artists = HashSet()
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists._ID))
                    val title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST))
                    artists!!.add(ArtistModel(id.toInt(), title))
                } while (cursor.moveToNext())
            }
        }
    }

    fun isCached(): Boolean {
        return artists != null
    }

    fun clear() {
        artists = null
    }
}