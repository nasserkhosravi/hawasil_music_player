package com.nasserkhosravi.hawasilmusicplayer.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.palette.graphics.Palette
import com.nasserkhosravi.hawasilmusicplayer.R
import com.nasserkhosravi.hawasilmusicplayer.data.model.SongModel
import com.nasserkhosravi.hawasilmusicplayer.data.persist.PaletteProvider
import io.reactivex.disposables.CompositeDisposable
import java.io.FileNotFoundException

class CircularSongArtViewModel : ViewModel() {

    fun getArt(context: Context, song: SongModel): Bitmap {
        return if (song.artUri != null) {
            try {
                MediaStore.Images.Media.getBitmap(context.contentResolver, song.artUri!!)
            } catch (e: FileNotFoundException) {
                getDefaultArt(context)
            }
        } else {
            getDefaultArt(context)
        }
    }

    fun getDefaultArt(context: Context): Bitmap {
        return BitmapFactory.decodeResource(context.resources, R.drawable.art_default)
    }


    fun getPalette(
        context: Context,
        song: SongModel,
        compositeDisposable: CompositeDisposable,
        onResult: (ArrayList<Palette.Swatch>) -> Unit
    ) {
        PaletteProvider.getPalette(context, song, compositeDisposable, onResult)
    }

}