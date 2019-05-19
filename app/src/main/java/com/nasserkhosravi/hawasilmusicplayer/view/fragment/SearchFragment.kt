package com.nasserkhosravi.hawasilmusicplayer.view.fragment

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.recyclerview.widget.LinearLayoutManager
import com.nasserkhosravi.appcomponent.view.fragment.BaseComponentFragment
import com.nasserkhosravi.hawasilmusicplayer.R
import com.nasserkhosravi.hawasilmusicplayer.data.MediaProvider
import com.nasserkhosravi.hawasilmusicplayer.view.adapter.AlbumAdapter
import com.nasserkhosravi.hawasilmusicplayer.view.adapter.ArtistAdapter
import com.nasserkhosravi.hawasilmusicplayer.view.adapter.SongAdapter
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : BaseComponentFragment() {
    override val layoutRes: Int
        get() = R.layout.fragment_search

    private var songAdapter = SongAdapter()
    private var albumAdapter = AlbumAdapter()
    private var artistAdapter = ArtistAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews()
        edSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                updateItems(v?.text.toString())
            }
            false
        }
    }

    private fun initViews() {
        songAdapter.items = arrayListOf()
        rvSongs.adapter = songAdapter
        rvSongs.layoutManager = LinearLayoutManager(context)

        albumAdapter.items = arrayListOf()
        rvAlbums.adapter = albumAdapter
        rvArtists.layoutManager = LinearLayoutManager(context)

        artistAdapter.items = arrayListOf()
        rvArtists.adapter = artistAdapter
        rvArtists.layoutManager = LinearLayoutManager(context)
    }

    private fun updateItems(text: String) {
        songAdapter.items = MediaProvider.getSongs().filter { it.title.contains(text, true) }
//        albumAdapter.items = MediaProvider.getAlbums().filter { it.title.contains(text, true) }
        artistAdapter.items = MediaProvider.getAllArtists().filter { it.title.contains(text, true) }

        songAdapter.notifyDataSetChanged()
//        albumAdapter.notifyDataSetChanged()
        artistAdapter.notifyDataSetChanged()
    }

    companion object {
        fun tag(): String {
            return SearchFragment::class.java.simpleName
        }

        fun newInstance(): SearchFragment {
            return SearchFragment()
        }
    }

}