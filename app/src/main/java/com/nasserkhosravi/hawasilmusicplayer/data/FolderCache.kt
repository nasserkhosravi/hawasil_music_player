package com.nasserkhosravi.hawasilmusicplayer.data

import android.database.Cursor
import android.provider.MediaStore
import com.nasserkhosravi.hawasilmusicplayer.data.model.FlatFolderModel

object FolderCache {
    val flats = ArrayList<FlatFolderModel>()

    private var foldersPath = HashSet<String>()
    var isCached = false

    fun cacheFlats(cursor: Cursor) {
        if (!isCached) {
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
                    val data = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                    val title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME))
                    add(data, title, id)
                } while (cursor.moveToNext())
            }
            isCached = true
        }
    }

    private fun add(songPath: String, songTitle: String, id: Long) {
        val detectedPath = songPath.removeRange(songPath.length - songTitle.length, songPath.length)
        if (foldersPath.add(detectedPath)) {
            //new folder
            val folderName = detectedPath.removeSuffix("/").substringAfterLast('/')
            val new = FlatFolderModel(folderName, detectedPath)
            new.songs = ArrayList()
            new.songs!!.add(id)
            flats.add(new)
        } else {
            flats[flats.size - 1].songs!!.add(id)
        }
    }

    fun clear() {
        foldersPath.clear()
        flats.clear()
        isCached = false
    }
}