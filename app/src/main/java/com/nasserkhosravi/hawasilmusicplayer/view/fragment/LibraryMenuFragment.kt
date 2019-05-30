package com.nasserkhosravi.hawasilmusicplayer.view.fragment

import android.os.Bundle
import android.view.View
import com.nasserkhosravi.appcomponent.view.fragment.BaseComponentFragment
import com.nasserkhosravi.hawasilmusicplayer.R
import com.nasserkhosravi.hawasilmusicplayer.app.setOnClickListeners
import kotlinx.android.synthetic.main.fragment_library_menu.*

class LibraryMenuFragment : BaseComponentFragment() {
    override val layoutRes: Int
        get() = R.layout.fragment_library_menu

    var viewClickListener: View.OnClickListener? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners(viewClickListener, tvAlbums, tvArtists, tvSongs, tvPlayLists)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        setOnClickListeners(null, tvAlbums, tvArtists, tvSongs, tvPlayLists)
    }

    companion object {
        fun newInstance(): LibraryMenuFragment {
            return LibraryMenuFragment()
        }
    }
}