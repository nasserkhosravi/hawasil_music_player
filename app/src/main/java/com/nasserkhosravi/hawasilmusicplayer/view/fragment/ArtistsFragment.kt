package com.nasserkhosravi.hawasilmusicplayer.view.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.nasserkhosravi.appcomponent.view.adapter.BaseComponentAdapter
import com.nasserkhosravi.appcomponent.view.fragment.BaseComponentFragment
import com.nasserkhosravi.hawasilmusicplayer.R
import com.nasserkhosravi.hawasilmusicplayer.di.ArtistFragmentModule
import com.nasserkhosravi.hawasilmusicplayer.di.DaggerArtistsFragmentComponent
import com.nasserkhosravi.hawasilmusicplayer.di.RecycleItemListenerModule
import com.nasserkhosravi.hawasilmusicplayer.view.adapter.ArtistAdapter
import com.nasserkhosravi.hawasilmusicplayer.view.adapter.RecycleItemListener
import com.nasserkhosravi.hawasilmusicplayer.viewmodel.ArtistsViewModel
import kotlinx.android.synthetic.main.fragment_artist.*
import kotlinx.android.synthetic.main.inc_toolbar.*
import javax.inject.Inject

class ArtistsFragment : BaseComponentFragment(), BaseComponentAdapter.ItemClickListener {
    override val layoutRes: Int
        get() = R.layout.fragment_artist

    @Inject
    lateinit var adapter: ArtistAdapter
    @Inject
    lateinit var viewModel: ArtistsViewModel
    @Inject
    lateinit var itemTouchListener: RecycleItemListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DaggerArtistsFragmentComponent.builder()
            .recycleItemListenerModule(RecycleItemListenerModule(context!!, this))
            .artistFragmentModule(ArtistFragmentModule(this))
            .build()
            .inject(this)

//        todo: notify items after providing items
        viewModel.getArtists.observe(this, Observer {
            adapter.items = it
            rvArtists.adapter = adapter
        })

        rvArtists.layoutManager = LinearLayoutManager(context)
        tvTitle.text = getString(R.string.Artists)
        imgBack.setOnClickListener {
            fragmentManager!!.popBackStack()
        }
        itemTouchListener = RecycleItemListener(context, this)
        rvArtists.addOnItemTouchListener(itemTouchListener)
    }

    override fun onRecycleItemClick(view: View, position: Int) {
        val model = adapter.items!![position]
        flQueue.visibility = View.VISIBLE

        val queueFragment = QueueFragment.newInstance(model)
        queueFragment.imgBackClickListener = View.OnClickListener {
            flQueue.visibility = View.GONE
        }
        childFragmentManager.beginTransaction().replace(R.id.flQueue, queueFragment)
            .addToBackStack("items")
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        imgBack.setOnClickListener(null)
        rvArtists.removeOnItemTouchListener(itemTouchListener)
    }

    companion object {
        fun newInstance(): ArtistsFragment {
            return ArtistsFragment()
        }
    }
}