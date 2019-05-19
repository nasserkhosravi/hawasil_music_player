package com.nasserkhosravi.hawasilmusicplayer.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nasserkhosravi.hawasilmusicplayer.app.launchActivity

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        launchActivity<MainActivity>()
        finish()
    }
}