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
import com.nasserkhosravi.hawasilmusicplayer.data.QueueBrain
import com.nasserkhosravi.hawasilmusicplayer.data.UserPref
import com.nasserkhosravi.hawasilmusicplayer.view.fragment.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseComponentActivity(), View.OnClickListener, BubbleNavigationChangeListener {

    var miniPlayerFragment: MiniPlayerFragment? = null
    var libraryFragment: LibraryNavigatorFragment? = null
    var searchFragment: SearchFragment? = null
    var hawasilFragment: HawasilFragment? = null
    var foldersFragment: FoldersFragment? = null
    val navigationFragment: NavigationFragment? = null

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
                QueueBrain.setData(queueData)
            }
            QueueBrain.data.isSongRestored = true
        }
        QueueBrain.startAndBindService()

        PermissionUtils.requestPhoneState(this)
        PermissionUtils.requestWritePermission(this)

        val navigationFragment = NavigationFragment.newInstance()
        navigationFragment.listener = this

        miniPlayerFragment = MiniPlayerFragment.newInstance()
        foldersFragment = FoldersFragment.newInstance()
        libraryFragment = LibraryNavigatorFragment.newInstance()
        hawasilFragment = HawasilFragment.newInstance()
        searchFragment = SearchFragment.newInstance()

        supportFragmentManager.beginTransaction()
            .replace(R.id.flMiniPlayer, miniPlayerFragment!!, MiniPlayerFragment.tag())
            .replace(R.id.flBody, libraryFragment!!)
            .replace(R.id.flNavigation, navigationFragment)
            .commit()
        flMiniPlayer.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.flMiniPlayer -> {
                val playerFragment = SongPlayerFragment.newInstance()
                supportFragmentManager.beginTransaction().replace(R.id.flPlayer, playerFragment, SongPlayerFragment.tag())
                    .addToBackStack("song player")
                    .commit()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        UserPref.saveQueueData(QueueBrain.data)
        flMiniPlayer.setOnClickListener(null)
        navigationFragment?.listener = null
    }

    override fun onNavigationChanged(view: View, p1: Int) {
        when (view.id) {
            R.id.itemLibrary -> {
                replaceFragment(R.id.flBody, libraryFragment!!)
            }
            R.id.itemFolder -> {
                replaceFragment(R.id.flBody, foldersFragment!!)
            }
            R.id.itemSearch -> {
                replaceFragment(R.id.flBody, searchFragment!!)
            }
            R.id.itemHawasil -> {
                replaceFragment(R.id.flBody, hawasilFragment!!)
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