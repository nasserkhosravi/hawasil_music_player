package com.nasserkhosravi.hawasilmusicplayer.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.nasserkhosravi.hawasilmusicplayer.data.model.QueueModel
import com.nasserkhosravi.hawasilmusicplayer.view.fragment.CircularSongArtFragment


class CircularSongArtPagerAdapter(fm: FragmentManager, private val queue: QueueModel) : FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        val songModel = queue.items[position]
        return CircularSongArtFragment.newInstance(songModel)
    }

    override fun getCount(): Int {
        return queue.items.size
    }
}