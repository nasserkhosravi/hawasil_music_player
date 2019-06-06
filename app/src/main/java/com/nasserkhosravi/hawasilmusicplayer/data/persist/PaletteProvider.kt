package com.nasserkhosravi.hawasilmusicplayer.data.persist

import android.content.Context
import android.provider.MediaStore
import androidx.palette.graphics.Palette
import com.nasserkhosravi.hawasilmusicplayer.app.App
import com.nasserkhosravi.hawasilmusicplayer.data.model.SongModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.FileNotFoundException

object PaletteProvider {

    fun getPalette(
        context: Context,
        model: SongModel,
        disposables: CompositeDisposable,
        onResult: (ArrayList<Palette.Swatch>) -> Unit
    ) {
        val dao = App.get().database.getSongDao()
        val subscribe = dao.getSwatch(model.id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                onResult(arrayListOf(it.toSwatch()))
            }, {
                if (model.artUri != null) {
                    try {
                        extractAndSaveSwatch(context, model, dao, disposables, onResult)
                    } catch (e: FileNotFoundException) {
                        onResult(defaultPalette)
                    }
                } else {
                    onResult(defaultPalette)
                }
            })
        disposables.add(subscribe)
    }

    private fun extractAndSaveSwatch(
        context: Context,
        model: SongModel,
        dao: SongDao,
        disposables: CompositeDisposable,
        onResult: (ArrayList<Palette.Swatch>) -> Unit
    ) {
        val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, model.artUri!!)
        Palette.from(bitmap).generate { generated ->
            if (generated != null) {
                val mutedSwatch = generated.mutedSwatch
                if (mutedSwatch != null) {
                    val swatchTable = SwatchTable(model.id, mutedSwatch.population, mutedSwatch.rgb, SwatchTable.MUTED)
                    val subscribe = saveSwatchInDB(dao, swatchTable)
                    disposables.add(subscribe)
                    onResult(arrayListOf(swatchTable.toSwatch()))
                } else {
                    onResult(defaultPalette)
                }
            } else {
                onResult(defaultPalette)
            }
        }
    }

    private fun saveSwatchInDB(dao: SongDao, swatchTable: SwatchTable): Disposable {
        return dao.insertSwatch(swatchTable)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }


    private val defaultPalette: ArrayList<Palette.Swatch> by lazy {
        val palette = Palette.from(App.get().defaultArt).generate()
        arrayListOf(palette.mutedSwatch!!)
    }

}
