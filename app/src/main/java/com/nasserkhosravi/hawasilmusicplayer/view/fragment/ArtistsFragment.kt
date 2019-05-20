package com.nasserkhosravi.hawasilmusicplayer.view.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.nasserkhosravi.appcomponent.view.adapter.BaseComponentAdapter
import com.nasserkhosravi.appcomponent.view.fragment.BaseComponentFragment
import com.nasserkhosravi.hawasilmusicplayer.R
import com.nasserkhosravi.hawasilmusicplayer.view.adapter.ArtistAdapter
import com.nasserkhosravi.hawasilmusicplayer.viewmodel.ArtistsViewModel
import kotlinx.android.synthetic.main.fragment_artist.*
import kotlinx.android.synthetic.main.inc_toolbar.*

class ArtistsFragment : BaseComponentFragment(), BaseComponentAdapter.ItemClickListener {
    override val layoutRes: Int
        get() = R.layout.fragment_artist

    private val adapter = ArtistAdapter()
    private lateinit var viewModel: ArtistsViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ArtistsViewModel::class.java)
        viewModel.getArtists.observe(this, Observer {
            adapter.items = it
            rvArtists.adapter = adapter
        })
        adapter.itemClickListener = this

        rvArtists.layoutManager = LinearLayoutManager(context)
        tvTitle.text = getString(R.string.Artists)
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
                val model = adapter.items!![position]
                flQueue.visibility = View.VISIBLE

                childFragmentManager.beginTransaction().replace(R.id.flQueue, QueueFragment.newInstance(model))
                    .addToBackStack("queue")
                    .commit()
            }
        }
    }

    companion object {
        fun newInstance(): ArtistsFragment {
            return ArtistsFragment()
        }
    }
}