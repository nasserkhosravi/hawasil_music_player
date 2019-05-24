package com.nasserkhosravi.hawasilmusicplayer.view.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.nasserkhosravi.appcomponent.view.adapter.BaseComponentAdapter
import com.nasserkhosravi.appcomponent.view.fragment.BaseComponentFragment
import com.nasserkhosravi.hawasilmusicplayer.R
import com.nasserkhosravi.hawasilmusicplayer.view.adapter.AlbumAdapter
import com.nasserkhosravi.hawasilmusicplayer.viewmodel.AlbumsViewModel
import kotlinx.android.synthetic.main.fragment_albums.*
import kotlinx.android.synthetic.main.inc_toolbar.*

class AlbumsFragment : BaseComponentFragment(), BaseComponentAdapter.ItemClickListener {
    override val layoutRes: Int
        get() = R.layout.fragment_albums

    private val adapter = AlbumAdapter()

    private lateinit var viewModel: AlbumsViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AlbumsViewModel::class.java)
        viewModel.getAlbums.observe(this, Observer {
            adapter.items = it
            rvAlbums.adapter = adapter
        })
        adapter.itemClickListener = this
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

        fun tag(): String {
            return AlbumsFragment::class.java.simpleName
        }
    }
}