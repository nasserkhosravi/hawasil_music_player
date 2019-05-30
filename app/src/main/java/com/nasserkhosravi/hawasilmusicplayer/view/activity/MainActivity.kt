package com.nasserkhosravi.hawasilmusicplayer.view.activity

import android.app.ActivityManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener
import com.nasserkhosravi.appcomponent.ResHelper
import com.nasserkhosravi.appcomponent.view.BaseComponentActivity
import com.nasserkhosravi.hawasilmusicplayer.PermissionUtils
import com.nasserkhosravi.hawasilmusicplayer.R
import com.nasserkhosravi.hawasilmusicplayer.data.MediaTerminal
import com.nasserkhosravi.hawasilmusicplayer.data.UserPref
import com.nasserkhosravi.hawasilmusicplayer.view.fragment.*
import com.nasserkhosravi.hawasilmusicplayer.view.fragment.component.FragmentLifecycleListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseComponentActivity(), View.OnClickListener, BubbleNavigationChangeListener {

    var playerFragment: SongPlayerFragment? = null

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
        if (UserPref.hasQueue()) {
            val queueData = UserPref.retrieveQueueData()
            if (queueData != null) {
                MediaTerminal.queue = (queueData)
            }
            MediaTerminal.queue.isSongRestored = true
        }
        MediaTerminal.startAndBindService()
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
        UserPref.saveQueueData(MediaTerminal.queue)
        flMiniPlayer.setOnClickListener(null)
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