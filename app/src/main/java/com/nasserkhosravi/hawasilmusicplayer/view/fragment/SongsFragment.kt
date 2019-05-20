package com.nasserkhosravi.hawasilmusicplayer.view.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.nasserkhosravi.appcomponent.view.adapter.BaseComponentAdapter
import com.nasserkhosravi.appcomponent.view.fragment.BaseComponentFragment
import com.nasserkhosravi.hawasilmusicplayer.R
import com.nasserkhosravi.hawasilmusicplayer.view.adapter.SongAdapter
import com.nasserkhosravi.hawasilmusicplayer.viewmodel.SongsViewModel
import kotlinx.android.synthetic.main.fragment_songs.*
import kotlinx.android.synthetic.main.inc_toolbar.*


class SongsFragment : BaseComponentFragment(), BaseComponentAdapter.ItemClickListener {
    override val layoutRes: Int
        get() = R.layout.fragment_songs

    private val adapter = SongAdapter()
    private lateinit var viewModel: SongsViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SongsViewModel::class.java)
        viewModel.getSongs.observe(this, Observer {
            adapter.items = it
            rvSongs.adapter = adapter
        })
        adapter.itemClickListener = this
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
                viewModel.onSongClick(position)
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