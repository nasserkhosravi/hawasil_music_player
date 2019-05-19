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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        menuFragment = LibraryMenuFragment.newInstance()
        childFragmentManager.beginTransaction().add(R.id.flLibrary, menuFragment!!).commit()
    }

    private fun replace(fragment: Fragment) {
        childFragmentManager.beginTransaction().replace(R.id.flLibrary, fragment).addToBackStack("child").commit()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tvAlbums -> {
                replace(AlbumsFragment.newInstance())
            }
            R.id.tvArtists -> {
                replace(ArtistsFragment.newInstance())
            }
            R.id.tvSongs -> {
                replace(SongsFragment.newInstance())
            }
            R.id.tvPlayLists -> {
                replace(PlayListsFragment.newInstance())
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