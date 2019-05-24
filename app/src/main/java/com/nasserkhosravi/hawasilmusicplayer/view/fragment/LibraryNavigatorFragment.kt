package com.nasserkhosravi.hawasilmusicplayer.view.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.nasserkhosravi.appcomponent.view.fragment.BaseComponentFragment
import com.nasserkhosravi.hawasilmusicplayer.R

class LibraryNavigatorFragment : BaseComponentFragment(), View.OnClickListener {
    override val layoutRes: Int
        get() = R.layout.fragment_library
    private var menuFragment: LibraryMenuFragment? = null
    private val albumsFragment: AlbumsFragment by lazy {
        AlbumsFragment.newInstance()
    }
    private val artistsFragment: ArtistsFragment by lazy {
        ArtistsFragment.newInstance()
    }
    private val queueFragment: QueueFragment by lazy {
        QueueFragment.newInstance()
    }
    private val playListsFragment: PlayListsFragment by lazy {
        PlayListsFragment.newInstance()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        menuFragment = LibraryMenuFragment.newInstance()
        menuFragment!!.viewClickListener = this
        childFragmentManager.beginTransaction().replace(R.id.flLibrary, menuFragment!!).commit()
    }

    private fun replace(fragment: Fragment) {
        childFragmentManager.beginTransaction().replace(R.id.flLibrary, fragment)
            .addToBackStack("menu navigator").commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        menuFragment!!.viewClickListener = null
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tvAlbums -> {
                replace(albumsFragment)
            }
            R.id.tvArtists -> {
                replace(artistsFragment)
            }
            R.id.tvSongs -> {
                replace(queueFragment)
            }
            R.id.tvPlayLists -> {
                replace(playListsFragment)
            }
            else -> {
                throw IllegalArgumentException()
            }
        }
    }

    companion object {

        fun tag(): String {
            return LibraryNavigatorFragment::class.java.simpleName
        }

        fun newInstance(): LibraryNavigatorFragment {
            return LibraryNavigatorFragment()
        }
    }
}