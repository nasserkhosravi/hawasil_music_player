package com.nasserkhosravi.hawasilmusicplayer.view.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.nasserkhosravi.appcomponent.view.adapter.BaseComponentAdapter
import com.nasserkhosravi.appcomponent.view.fragment.BaseComponentFragment
import com.nasserkhosravi.hawasilmusicplayer.R
import com.nasserkhosravi.hawasilmusicplayer.di.AlbumsFragmentModule
import com.nasserkhosravi.hawasilmusicplayer.di.DaggerAlbumsFragmentComponent
import com.nasserkhosravi.hawasilmusicplayer.di.RecycleItemListenerModule
import com.nasserkhosravi.hawasilmusicplayer.view.adapter.AlbumAdapter
import com.nasserkhosravi.hawasilmusicplayer.view.adapter.RecycleItemListener
import com.nasserkhosravi.hawasilmusicplayer.viewmodel.AlbumsViewModel
import kotlinx.android.synthetic.main.fragment_albums.*
import kotlinx.android.synthetic.main.inc_toolbar.*
import javax.inject.Inject

class AlbumsFragment : BaseComponentFragment(), BaseComponentAdapter.ItemClickListener {
    override val layoutRes: Int
        get() = R.layout.fragment_albums

    private val adapter = AlbumAdapter()
    private var queueFragment: QueueFragment? = null

    @Inject
    lateinit var viewModel: AlbumsViewModel
    @Inject
    lateinit var itemTouchListener: RecycleItemListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DaggerAlbumsFragmentComponent.builder()
            .recycleItemListenerModule(RecycleItemListenerModule(context!!, this))
            .albumsFragmentModule(AlbumsFragmentModule(this))
            .build()
            .inject(this)

        viewModel.getAlbums.observe(this, Observer {
            adapter.items = it
            rvAlbums.adapter = adapter
        })

        rvAlbums.layoutManager = LinearLayoutManager(context)
        tvTitle.text = getString(R.string.Albums)
        imgBack.setOnClickListener {
            fragmentManager!!.popBackStack()
        }
        itemTouchListener = RecycleItemListener(context, this)
        rvAlbums.addOnItemTouchListener(itemTouchListener)
    }

    override fun onRecycleItemClick(view: View, position: Int) {
        val model = adapter.items!![position]
        flQueue.visibility = View.VISIBLE

        queueFragment = QueueFragment.newInstance(model)
        queueFragment!!.imgBackClickListener = View.OnClickListener {
            flQueue.visibility = View.GONE
        }
        childFragmentManager.beginTransaction().replace(R.id.flQueue, queueFragment!!)
            .addToBackStack("items")
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        queueFragment = null
        rvAlbums.removeOnItemTouchListener(itemTouchListener)
        queueFragment?.imgBackClickListener = null
        imgBack.setOnClickListener(null)
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