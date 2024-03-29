package com.nasserkhosravi.hawasilmusicplayer.data.model

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.*
import com.nasserkhosravi.hawasilmusicplayer.FormatUtils
import com.nasserkhosravi.hawasilmusicplayer.app.App
import com.nasserkhosravi.hawasilmusicplayer.data.model.SongStatus.PAUSE
import com.nasserkhosravi.hawasilmusicplayer.data.model.SongStatus.PLAYING
import java.lang.reflect.Type

enum class SongStatus {
    PLAYING,
    PAUSE;

    fun isPlay(): Boolean {
        return this == PLAYING
    }
}

data class SongModel(
    val id: Long,
    val path: String,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long
) : Parcelable {

    var status = PAUSE
    var passedDuration = 0L
    //an example of uri is: content://media/external/audio/albumart/2
    var artUri: Uri? = null

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readLong()
    ) {
        passedDuration = parcel.readLong()
        artUri = parcel.readParcelable(Uri::class.java.classLoader)
    }


    fun getFormatDuration(): String {
        return FormatUtils.milliSeconds(duration)
    }

    fun reversePlayStatus() {
        status = when (status) {
            PLAYING -> PAUSE
            PAUSE -> PLAYING
        }
    }

    fun isPlaying(): Boolean {
        return status.isPlay()
    }

    fun setStatus(enable: Boolean) {
        status = if (enable) {
            PLAYING
        } else {
            PAUSE
        }
    }

    fun toJson(): String {
        return App.get().jsonAdapter.toJson(this)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(path)
        parcel.writeString(title)
        parcel.writeString(artist)
        parcel.writeString(album)
        parcel.writeLong(id)
        parcel.writeLong(passedDuration)
        parcel.writeLong(duration)
        parcel.writeParcelable(artUri, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    fun resetToPassiveState() {
        passedDuration = 0
        status = SongStatus.PAUSE
    }

    fun computePassedDuration(): Float {
        val duration = duration.toFloat()
        val current = passedDuration.toFloat()
        return (current / duration)
    }

    companion object CREATOR : Parcelable.Creator<SongModel> {
        override fun createFromParcel(parcel: Parcel): SongModel {
            return SongModel(parcel)
        }

        override fun newArray(size: Int): Array<SongModel?> {
            return arrayOfNulls(size)
        }

        fun fromJson(json: String?): SongModel? {
            if (json == null) {
                return null
            }
            return App.get().jsonAdapter.fromJson(json, SongModel::class.java)
        }
    }

}

class SongModelAdapter : JsonDeserializer<SongModel>, JsonSerializer<SongModel> {

    override fun serialize(src: SongModel, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        val json = JsonObject()
        json.addProperty("id", src.id)
        json.addProperty("path", src.path)
        json.addProperty("title", src.title)
        json.addProperty("artist", src.artist)
        json.addProperty("album", src.album)
        json.addProperty("duration", src.duration)
        json.addProperty("passedDuration", src.passedDuration)
        json.addProperty("isPlaying", src.isPlaying())
        json.addProperty("artUri", src.artUri?.toString())
        return json
    }

    override fun deserialize(element: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): SongModel {
        val json = element.asJsonObject
        val id = json.get("id").asLong
        val path = json.get("path").asString
        val title = json.get("title").asString
        val album = json.get("album").asString
        val artist = json.get("artist").asString
        val duration = json.get("duration").asLong
        val songPassed = json.get("passedDuration").asLong
        val isPlaying = json.get("isPlaying").asBoolean
        val artUri = json.get("artUri").asString
        val model = SongModel(id, path, title, artist, album, duration)
        model.passedDuration = songPassed
        model.setStatus(isPlaying)
        if (artUri != null) {
            model.artUri = Uri.parse(artUri)
        }
        return model
    }
}