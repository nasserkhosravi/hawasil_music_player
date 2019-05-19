package com.nasserkhosravi.hawasilmusicplayer.view.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.nasserkhosravi.appcomponent.view.adapter.BaseComponentAdapter
import com.nasserkhosravi.appcomponent.view.fragment.BaseComponentFragment
import com.nasserkhosravi.hawasilmusicplayer.R
import com.nasserkhosravi.hawasilmusicplayer.data.MediaProvider
import com.nasserkhosravi.hawasilmusicplayer.view.adapter.AlbumAdapter
import kotlinx.android.synthetic.main.fragment_albums.*
import kotlinx.android.synthetic.main.inc_toolbar.*

class AlbumsFragment : BaseComponentFragment(), BaseComponentAdapter.ItemClickListener {
    override val layoutRes: Int
        get() = R.layout.fragment_albums

    private val adapter = AlbumAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter.items = MediaProvider.getAlbums()
        adapter.itemClickListener = this

        rvAlbums.adapter = adapter
        rvAlbums.layoutManager = LinearLayoutManager(context)

        tvTitle.text = getString(R.string.Albums)
        imgBack.setOnClickListener {
            fragmentManager!!.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter.itemClickListener = null
        imgBack.setOnClickListener(null)
    }

    override fun onRecycleItemClick(view: View, position: Int) {
        when (view.id) {
            R.id.imgMore -> {

            }
            else -> {
                //start queue fragment
                val model = adapter.items!![position]
                flQueue.visibility = View.VISIBLE
                childFragmentManager.beginTransaction().replace(R.id.flQueue, QueueFragment.newInstance(model))
                    .addToBackStack("queue")
                    .commit()
            }
        }
    }

    companion object {
        fun newInstance(): AlbumsFragment {
            return AlbumsFragment()
        }
    }
}