package com.nasserkhosravi.hawasilmusicplayer.data

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.provider.BaseColumns
import android.provider.MediaStore
import android.provider.MediaStore.Audio.*
import android.util.Log
import com.nasserkhosravi.hawasilmusicplayer.data.model.AlbumModel
import com.nasserkhosravi.hawasilmusicplayer.data.model.ArtistModel
import com.nasserkhosravi.hawasilmusicplayer.data.model.PlayListModel
import com.nasserkhosravi.hawasilmusicplayer.data.model.SongModel

object MediaProvider {

    private lateinit var contentResolver: ContentResolver

    fun setContentResolver(contentResolver: ContentResolver) {
        this.contentResolver = contentResolver
    }

    fun getArtists(): ArrayList<ArtistModel> {
        if (ArtistCache.isCached()) {
            return ArrayList(ArtistCache.artists!!)
        } else {
            val columns = arrayOf(Artists.ARTIST, Artists._ID)
            val cursor = contentResolver.query(Artists.EXTERNAL_CONTENT_URI, columns, null, null, null)!!
            ArtistCache.cache(cursor)
            cursor.close()

            return if (ArtistCache.artists == null || ArtistCache.artists!!.isEmpty()) {
                arrayListOf()
            } else {
                ArrayList(ArtistCache.artists!!)
            }
        }
    }

    fun getAlbums(): List<AlbumModel> {
        val projection = arrayOf(Albums._ID, Albums.ALBUM, Albums.ARTIST, Albums.ALBUM_ART)
        val selection: String? = null
        val selectionArgs: Array<String>? = null
        val sortOrder = Media.ALBUM + " ASC"
        val cursor = contentResolver.query(Albums.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, sortOrder)
        val result = ArrayList<AlbumModel>()
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getString(cursor.getColumnIndexOrThrow(Albums._ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(Albums.ALBUM))
                val artist = cursor.getString(cursor.getColumnIndexOrThrow(Albums.ARTIST))
                val art: String? = cursor.getString(cursor.getColumnIndexOrThrow(Albums.ALBUM_ART))
                val model = AlbumModel(id.toInt(), title, artist, art)
                model.artWorkUri = getARtWorkByAlbumId(id.toLong())
                result.add(model)
            } while (cursor.moveToNext())
        }
        return result
    }

    fun getSongs(): ArrayList<SongModel> {
        val result = ArrayList<SongModel>()
        val selection = Media.IS_MUSIC + "!= 0"
        val sortOrder = String.format("%s limit 10", BaseColumns._ID)
        val columns =
            getAudioColumns()
        val cursor = contentResolver.query(Media.EXTERNAL_CONTENT_URI, columns, selection, null, null)!!
        FolderCache.cacheFlats(cursor)
        if (cursor.moveToFirst()) {
            do {
                val model = createSongByCursor(cursor)
                result.add(model)
            } while (cursor.moveToNext())
        }
        return result
    }

    private fun createSongByCursor(cursor: Cursor): SongModel {
        val id = cursor.getLong(cursor.getColumnIndexOrThrow(Media._ID))
        val data = cursor.getString(cursor.getColumnIndexOrThrow(Media.DATA))
        val artist = cursor.getString(cursor.getColumnIndexOrThrow(Media.ARTIST))
        val title = cursor.getString(cursor.getColumnIndexOrThrow(Media.DISPLAY_NAME))
        val duration = cursor.getLong(cursor.getColumnIndexOrThrow(Media.DURATION))
        val albumId = cursor.getString(cursor.getColumnIndexOrThrow(Media.ALBUM_ID))
        val album = cursor.getString(cursor.getColumnIndexOrThrow(Media.ALBUM))

        val model = SongModel(id, data, title, artist, album, duration)
        model.artUri = getARtWorkByAlbumId(albumId.toLong())
        return model
    }

    private fun getARtWorkByAlbumId(albumId: Long): Uri? {
        val artworkUri = Uri.parse("content://media/external/audio/albumart")
        return ContentUris.withAppendedId(artworkUri, albumId)
    }

    fun checkCacheFoldersContainSong() {
        if (!FolderCache.isCached) {
            val selection = Media.IS_MUSIC + "!= 0"
            val columns = arrayOf(Media._ID, Media.DATA, Media.DISPLAY_NAME)
            val cursor = contentResolver.query(Media.EXTERNAL_CONTENT_URI, columns, selection, null, null)!!
            FolderCache.cacheFlats(cursor)
        }
    }

    fun getPlayLists(): List<PlayListModel> {
        val result = ArrayList<PlayListModel>()
        val playlistUri = Playlists.EXTERNAL_CONTENT_URI
        val columns = arrayOf(
            Playlists._ID,
            Playlists.NAME
        )
        val cursor = contentResolver.query(playlistUri, columns, null, null, null)!!
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(Playlists._ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(Playlists.NAME))
                val model = PlayListModel(id, title, getPlayListSongsId(id))
                result.add(model)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return result
    }

    private fun getPlayListSongsId(playlistId: Long): ArrayList<Pair<Long, Uri?>> {
        val result = ArrayList<Pair<Long, Uri?>>()
        val cursor = contentResolver.query(
            Playlists.Members.getContentUri("external", playlistId),
            arrayOf(Playlists.Members.AUDIO_ID, Playlists.Members.ALBUM_ID), null, null,
            MediaStore.Audio.Playlists.Members.DEFAULT_SORT_ORDER
        )
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(Playlists.Members.AUDIO_ID))
                val albumId = cursor.getLong(cursor.getColumnIndexOrThrow(Playlists.Members.ALBUM_ID))
                val artWorkUri = getARtWorkByAlbumId(albumId)

                result.add(Pair(id, artWorkUri))
            } while (cursor.moveToNext())
        }
        return result
    }

    /**
     * @param context The [Context] to use.
     * @param playlistName    The playlistName of the new playlist.
     * @return A new playlist ID.
     */
    fun createPlaylist(playlistName: String): Long {
        if (playlistName.isNotEmpty()) {
            val projection = arrayOf(PlaylistsColumns.NAME)
            val selection = PlaylistsColumns.NAME + " = '" + playlistName + "'"
            val cursor = contentResolver.query(Playlists.EXTERNAL_CONTENT_URI, projection, selection, null, null)
            if (cursor!!.count <= 0) {
                val values = ContentValues(1)
                values.put(PlaylistsColumns.NAME, playlistName)
                val uri = contentResolver.insert(
                    MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                    values
                )
                return uri!!.lastPathSegment!!.toLong()
            }
            cursor.close()
            return -1
        }
        return -1
    }

    /**
     * @return the number of rows updated.
     */
    fun renamePlaylist(newPlaylist: String, playlistId: Long): Int {
        if (newPlaylist.isNotEmpty()) {
            val values = ContentValues()
            val where = MediaStore.Audio.Playlists._ID + " =? "
            val whereVal = arrayOf(playlistId.toString())
            values.put(MediaStore.Audio.Playlists.NAME, newPlaylist)
            return contentResolver.update(Playlists.EXTERNAL_CONTENT_URI, values, where, whereVal)
        }
        return 0
    }

    /**
     * return id of playlist if exist
     */
    fun isExistPlayList(name: String): Long {
        if (name.isEmpty()) {
            throw IllegalArgumentException("Name should not be empty")
        }
        val projection = arrayOf(Playlists.NAME, Playlists._ID)
        val selection = PlaylistsColumns.NAME + " = '" + name + "'"
        val cursor = contentResolver.query(Playlists.EXTERNAL_CONTENT_URI, projection, selection, null, null)
        if (cursor.moveToFirst()) {
            return cursor.getLong(cursor.getColumnIndexOrThrow(Playlists._ID))
        } else {
            return -1
        }
    }

    /**
     * @return The number of rows deleted.
     */
    fun removePlaylist(playlistId: Long): Int {
        val where = MediaStore.Audio.Playlists._ID + " =? "
        val whereVal = arrayOf(playlistId.toString())
        return contentResolver.delete(Playlists.EXTERNAL_CONTENT_URI, where, whereVal)
    }

    fun isExistInPlaylist(songId: Long, playlistId: Long): Boolean {
        val projection = arrayOf(Playlists.Members.TITLE)
        val where = Playlists.Members.AUDIO_ID + " =? "
        val args = arrayOf(songId.toString())
        val uri = Playlists.Members.getContentUri("external", playlistId)
        val cursor = contentResolver.query(uri, projection, where, args, null)!!
        return cursor.count > 0
    }

    fun addToPlayList(songId: Long, playlistId: Long): Boolean {
        val tracks = getPlaylistTracks(playlistId)
        val value = ContentValues()
        value.put(Playlists.Members.AUDIO_ID, songId)
        value.put(Playlists.Members.PLAY_ORDER, tracks.lastIndex + 1)

        val uri = Playlists.Members.getContentUri("external", playlistId)
        val insertedUri = contentResolver.insert(uri, value)
        if (insertedUri == null) {
            Log.d(tag(), "addToPlayList: failed to add song to play list-> songId:$songId playlistId:$playlistId")
            return false
        }
        return true
    }

    fun removeFromPlayList(songId: Long, playlistId: Long): Boolean {
        val where = Playlists.Members._ID + " =? "
        val args = arrayOf(songId.toString())
        val uri = Playlists.Members.getContentUri("external", playlistId)
        val numberOfRow = contentResolver.delete(uri, where, args)
        if (numberOfRow == -1) {
            Log.d(tag(), "removeFromPlayList: failed to remove song from play list-> songId:$songId playlistId:$playlistId")
            return false
        }
        return true
    }

    fun getSongsByFolder(path: String): ArrayList<SongModel> {
        val result = ArrayList<SongModel>()
        val columns = getAudioColumns()
        val selection = MediaStore.Audio.Media.DATA + " like ? "
        val args = arrayOf("$path%")
        val cursor = contentResolver.query(Media.EXTERNAL_CONTENT_URI, columns, selection, args, null)
        if (cursor.moveToFirst()) {
            do {
                result.add(createSongByCursor(cursor))
            } while (cursor.moveToNext())
        }
        return result
    }

    fun getSongsByArtist(artistId: Long): ArrayList<SongModel> {
        val result = ArrayList<SongModel>()
        val columns = getAudioColumns()
        val selection = Media.ARTIST_ID + " = ? "
        val args = arrayOf(artistId.toString())
        val cursor = contentResolver.query(Media.EXTERNAL_CONTENT_URI, columns, selection, args, null)
        if (cursor.moveToFirst()) {
            do {
                result.add(createSongByCursor(cursor))
            } while (cursor.moveToNext())
        }
        return result
    }

    fun getSongsByAlbum(albumId: Long): ArrayList<SongModel> {
        val result = ArrayList<SongModel>()
        val columns = getAudioColumns()
        val selection = Media.ALBUM_ID + " = ? "
        val args = arrayOf(albumId.toString())
        val cursor = contentResolver.query(Media.EXTERNAL_CONTENT_URI, columns, selection, args, null)
        if (cursor.moveToFirst()) {
            do {
                result.add(createSongByCursor(cursor))
            } while (cursor.moveToNext())
        }
        return result
    }

    fun getPlaylistTracks(playlistId: Long): ArrayList<SongModel> {
        val result = ArrayList<SongModel>()
        val uri = Playlists.Members.getContentUri("external", playlistId)
        val columns = getAudioColumns()
        val cursor = contentResolver.query(uri, columns, null, null, null)
        if (cursor.moveToFirst()) {
            do {
                val model = createSongByCursor(cursor)
                result.add(model)
            } while (cursor.moveToNext())
        }
        return result
    }

    private fun getAudioColumns(): Array<String> {
        return arrayOf(Media._ID, Media.DATA, Media.ALBUM_ID, Media.ALBUM, Media.ARTIST, Media.DISPLAY_NAME, Media.DURATION)
    }

    fun tag(): String {
        return MediaProvider::class.java.simpleName
    }
}
