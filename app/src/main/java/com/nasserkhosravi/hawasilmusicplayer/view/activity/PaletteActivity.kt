package com.nasserkhosravi.hawasilmusicplayer.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.palette.graphics.Palette
import com.nasserkhosravi.hawasilmusicplayer.R
import kotlinx.android.synthetic.main.palette.*

class PaletteActivity : AppCompatActivity() {

    fun tag(): String {
        return PaletteActivity::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.palette)

    }

    private fun setColorsInPalette(palette: Palette) {
        fl1.setBackgroundColor(palette.mutedSwatch!!.rgb)
        fl2.setBackgroundColor(palette.darkMutedSwatch!!.rgb)
        fl3.setBackgroundColor(palette.lightMutedSwatch!!.rgb)
        //
        fl4.setBackgroundColor(palette.vibrantSwatch!!.rgb)
        fl5.setBackgroundColor(palette.darkVibrantSwatch!!.rgb)
        if (palette.lightVibrantSwatch?.rgb != null) {
            fl6.setBackgroundColor(palette.lightVibrantSwatch!!.rgb)
        }
    }
}