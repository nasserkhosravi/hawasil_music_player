package com.nasserkhosravi.hawasilmusicplayer.view.activity

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import android.view.View
import androidx.fragment.app.Fragment
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener
import com.nasserkhosravi.appcomponent.ResHelper
import com.nasserkhosravi.appcomponent.view.BaseComponentActivity
import com.nasserkhosravi.hawasilmusicplayer.PermissionUtils
import com.nasserkhosravi.hawasilmusicplayer.R
import com.nasserkhosravi.hawasilmusicplayer.StorageUtils
import com.nasserkhosravi.hawasilmusicplayer.app.App
import com.nasserkhosravi.hawasilmusicplayer.data.MediaPlayerService
import com.nasserkhosravi.hawasilmusicplayer.data.MediaProvider
import com.nasserkhosravi.hawasilmusicplayer.data.QueueManager
import com.nasserkhosravi.hawasilmusicplayer.data.UIMediaCommand
import com.nasserkhosravi.hawasilmusicplayer.data.model.QueueModel
import com.nasserkhosravi.hawasilmusicplayer.data.persist.UserPref
import com.nasserkhosravi.hawasilmusicplayer.view.fragment.*
import com.nasserkhosravi.hawasilmusicplayer.view.fragment.component.FragmentLifecycleListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseComponentActivity(), View.OnClickListener, BubbleNavigationChangeListener {

    private var playerFragment: SongPlayerFragment? = null
    private val mediaBrowser: MediaBrowserCompat by lazy {
        MediaBrowserCompat(this, ComponentName(this, MediaPlayerService::class.java), connectionCallback, null)
    }
    private val connectionCallback = object : MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            super.onConnected()
            mediaBrowser.sessionToken.also { token ->
                val myMediaController = MediaControllerCompat(this@MainActivity, token)
                MediaControllerCompat.setMediaController(this@MainActivity, myMediaController)
                UIMediaCommand.setController(myMediaController)
            }
            if (!isNormalIntent()) {
                runOnlyPlayerFlow()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            setTaskDescription(
                ActivityManager.TaskDescription(
                    getString(R.string.app_name),
                    R.mipmap.ic_launcher,
                    ResHelper.getColorRes(R.color.white)
                )
            )
        }
        if (isNormalIntent()) {
            runNormalFlow()
        } else {
            App.get().isNormalIntent = false
        }
        mediaBrowser.connect()
    }

    private fun runOnlyPlayerFlow() {
        val data = intent.data!!
        val path = StorageUtils.getRealPathFromURI(this, data)
        val song = MediaProvider.getSongByPath(path)!!
        QueueManager.get().queue = QueueModel().apply {
            items.add(song)
            active(0)
            shouldLoad = true
        }
        flPlayer!!.visibility = View.VISIBLE
        layoutBody!!.visibility = View.GONE
        playerFragment = SongPlayerFragment.newInstance(false)
        supportFragmentManager.beginTransaction().replace(R.id.flPlayer, playerFragment!!).commit()
    }

    /**
     * true if activity has no input data (app started from app icon)
     * else false (app started from either source)
     */
    private fun isNormalIntent() = intent.action == Intent.ACTION_MAIN

    private fun runNormalFlow() {
        if (UserPref.hasQueue()) {
            val queueData = UserPref.retrieveQueueData()
            if (queueData != null) {
                QueueManager.get().queue = (queueData)
            }
            QueueManager.get().queue!!.shouldLoad = true
        } else {
            QueueManager.get().queue = QueueModel()
        }

        PermissionUtils.requestWritePermission(this)
        //we can use dagger
        val navigationFragment = NavigationFragment.newInstance()
        navigationFragment.listener = this

        val miniPlayerFragment = MiniPlayerFragment.newInstance()
        val libraryFragment = LibraryNavigatorFragment.newInstance()

        supportFragmentManager.beginTransaction()
            .replace(R.id.flBody, libraryFragment)
            .replace(R.id.flMiniPlayer, miniPlayerFragment, MiniPlayerFragment.tag())
            .replace(R.id.flNavigation, navigationFragment)
            .commit()
        flMiniPlayer.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.flMiniPlayer -> {
                playerFragment = SongPlayerFragment.newInstance()
                playerFragment!!.lifecycleListener = (object :
                    FragmentLifecycleListener {
                    override fun onStartFragment() {
                        flPlayer!!.visibility = View.VISIBLE
                        layoutBody!!.visibility = View.GONE
                    }

                    override fun onStopFragment() {
                        flPlayer!!.visibility = View.GONE
                        layoutBody!!.visibility = View.VISIBLE
                    }

                    override fun onDestroyViewFragment() {
                        playerFragment?.lifecycleListener = null
                    }
                })
                supportFragmentManager.beginTransaction().replace(R.id.flPlayer, playerFragment!!, SongPlayerFragment.tag())
                    .addToBackStack("song player")
                    .commit()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (App.get().isNormalIntent) {
            QueueManager.get().queue?.let {
                UserPref.saveQueueData(it)
            }
            flMiniPlayer.setOnClickListener(null)
            UIMediaCommand.setController(null)
        }
        mediaBrowser.disconnect()
    }

    override fun onNavigationChanged(view: View, position: Int) {
        when (view.id) {
            R.id.itemLibrary -> {
                replaceFragment(R.id.flBody, LibraryNavigatorFragment.newInstance())
            }
            R.id.itemFolder -> {
                replaceFragment(R.id.flBody, FoldersFragment.newInstance())
            }
            R.id.itemSearch -> {
                replaceFragment(R.id.flBody, SearchFragment.newInstance())
            }
            R.id.itemHawasil -> {
                replaceFragment(R.id.flBody, HawasilFragment.newInstance())
            }
        }
    }

    private fun replaceFragment(id: Int, fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(id, fragment).commit()
    }

    fun tag(): String {
        return MainActivity::class.java.simpleName
    }

}