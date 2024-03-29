package com.nasserkhosravi.hawasilmusicplayer.view.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.nasserkhosravi.appcomponent.view.fragment.BaseComponentFragment
import com.nasserkhosravi.hawasilmusicplayer.R

class LibraryNavigatorFragment : BaseComponentFragment(), View.OnClickListener {
    override val layoutRes: Int
        get() = R.layout.fragment_library

    private val menuFragment: LibraryMenuFragment by lazy { LibraryMenuFragment.newInstance() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        menuFragment.viewClickListener = this
        childFragmentManager.beginTransaction().replace(R.id.flLibrary, menuFragment).commit()
    }

    private fun replace(fragment: Fragment) {
        childFragmentManager.beginTransaction().replace(R.id.flLibrary, fragment)
            .addToBackStack("menu navigator").commit()
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
                replace(QueueFragment.newInstance())
            }
            R.id.tvPlayLists -> {
                replace(PlayListsFragment.newInstance())
            }
            else -> {
                throw IllegalArgumentException()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        menuFragment.viewClickListener = null
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