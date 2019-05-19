package com.nasserkhosravi.hawasilmusicplayer.view.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.nasserkhosravi.appcomponent.view.adapter.BaseComponentAdapter
import com.nasserkhosravi.appcomponent.view.fragment.BaseComponentFragment
import com.nasserkhosravi.hawasilmusicplayer.R
import com.nasserkhosravi.hawasilmusicplayer.data.MediaProvider
import com.nasserkhosravi.hawasilmusicplayer.data.QueueBrain
import com.nasserkhosravi.hawasilmusicplayer.view.adapter.SongAdapter
import kotlinx.android.synthetic.main.fragment_songs.*
import kotlinx.android.synthetic.main.inc_toolbar.*


class SongsFragment : BaseComponentFragment(), BaseComponentAdapter.ItemClickListener {
    override val layoutRes: Int
        get() = R.layout.fragment_songs

    private val adapter = SongAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter.itemClickListener = this
        adapter.items = MediaProvider.getSongs()
        rvSongs.adapter = adapter
        rvSongs.layoutManager = LinearLayoutManager(context)
        tvTitle.text = getString(R.string.Songs)
        imgBack.setOnClickListener {
            fragmentManager!!.popBackStack()
        }
    }

    override fun onRecycleItemClick(view: View, position: Int) {
        when (view.id) {
            R.id.imgMore -> {
            }
            else -> {
                QueueBrain.checkNewQueueRequest(adapter.items!!, position, "${context!!.packageName}.songList")
                adapter.makeThisSelect(position)
            }
        }
    }

    companion object {
        fun newInstance(): SongsFragment {
            return SongsFragment()
        }
    }
}