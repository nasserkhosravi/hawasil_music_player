package com.nasserkhosravi.hawasilmusicplayer.view.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.nasserkhosravi.appcomponent.view.adapter.BaseComponentAdapter
import com.nasserkhosravi.appcomponent.view.fragment.BaseComponentFragment
import com.nasserkhosravi.hawasilmusicplayer.R
import com.nasserkhosravi.hawasilmusicplayer.data.MediaProvider
import com.nasserkhosravi.hawasilmusicplayer.view.adapter.PlayListAdapter
import kotlinx.android.synthetic.main.fragment_playlist.*
import kotlinx.android.synthetic.main.inc_toolbar.*


class PlayListsFragment : BaseComponentFragment(), BaseComponentAdapter.ItemClickListener {
    override val layoutRes: Int
        get() = R.layout.fragment_playlist

    private val adapter = PlayListAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter.items = MediaProvider.getPlayLists()
        adapter.itemClickListener = this

        rvPlayLists.adapter = adapter
        rvPlayLists.layoutManager = LinearLayoutManager(context)

        tvTitle.text = getString(R.string.PlayLists)
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
        val model = adapter.items!![position]
        flQueue.visibility = View.VISIBLE
        childFragmentManager.beginTransaction().replace(R.id.flQueue, QueueFragment.newInstance(model))
            .addToBackStack("queue")
            .commit()
    }

    companion object {
        fun newInstance(): PlayListsFragment {
            return PlayListsFragment()
        }
    }

}