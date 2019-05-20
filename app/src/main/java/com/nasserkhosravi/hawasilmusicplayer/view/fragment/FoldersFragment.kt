package com.nasserkhosravi.hawasilmusicplayer.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.nasserkhosravi.appcomponent.view.adapter.BaseComponentAdapter
import com.nasserkhosravi.hawasilmusicplayer.R
import com.nasserkhosravi.hawasilmusicplayer.view.adapter.FolderAdapter
import com.nasserkhosravi.hawasilmusicplayer.viewmodel.FoldersViewModel
import kotlinx.android.synthetic.main.fragment_folder.*

class FoldersFragment : Fragment(), BaseComponentAdapter.ItemClickListener {

    private val adapter = FolderAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_folder, container, false)
    }

    private lateinit var viewModel: FoldersViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(FoldersViewModel::class.java)
        viewModel.getFolders.observe(this, Observer {
            adapter.items = it
            rvFolder.adapter = adapter

        })
        adapter.itemClickListener = this
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