package com.nasserkhosravi.hawasilmusicplayer.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.nasserkhosravi.appcomponent.view.adapter.BaseComponentAdapter
import com.nasserkhosravi.hawasilmusicplayer.R
import com.nasserkhosravi.hawasilmusicplayer.data.FolderCache
import com.nasserkhosravi.hawasilmusicplayer.data.MediaProvider
import com.nasserkhosravi.hawasilmusicplayer.view.adapter.FolderAdapter
import kotlinx.android.synthetic.main.fragment_folder.*

class FoldersFragment : Fragment(), BaseComponentAdapter.ItemClickListener {

    private val adapter = FolderAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_folder, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MediaProvider.checkCacheFoldersContainSong()
        val items = FolderCache.flats
        adapter.items = items
        adapter.itemClickListener = this
        rvFolder.adapter = adapter
        rvFolder.layoutManager = LinearLayoutManager(context)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter.itemClickListener = null
    }

    override fun onRecycleItemClick(view: View, position: Int) {
        val model = adapter.items!![position]
        flQueue.visibility = View.VISIBLE
        childFragmentManager.beginTransaction().replace(R.id.flQueue, QueueFragment.newInstance(model))
            .addToBackStack("queue")
            .commit()
    }

    companion object {
        fun tag(): String {
            return FoldersFragment::class.java.simpleName
        }

        fun newInstance(): FoldersFragment {
            return FoldersFragment()
        }
    }
}