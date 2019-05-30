package com.nasserkhosravi.hawasilmusicplayer.view.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.nasserkhosravi.appcomponent.view.adapter.BaseComponentAdapter
import com.nasserkhosravi.appcomponent.view.fragment.BaseComponentFragment
import com.nasserkhosravi.hawasilmusicplayer.R
import com.nasserkhosravi.hawasilmusicplayer.view.adapter.FolderAdapter
import com.nasserkhosravi.hawasilmusicplayer.view.adapter.RecycleItemListener
import com.nasserkhosravi.hawasilmusicplayer.viewmodel.FoldersViewModel
import kotlinx.android.synthetic.main.fragment_folder.*

class FoldersFragment : BaseComponentFragment(), BaseComponentAdapter.ItemClickListener {

    override val layoutRes: Int
        get() = R.layout.fragment_folder

    private val adapter = FolderAdapter()
    private lateinit var viewModel: FoldersViewModel
    private lateinit var itemTouchListener: RecycleItemListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(FoldersViewModel::class.java)
        viewModel.getFolders.observe(this, Observer {
            adapter.items = it
            rvFolder.adapter = adapter
        })
        rvFolder.layoutManager = LinearLayoutManager(context)
        itemTouchListener = RecycleItemListener(context, this)
        rvFolder.addOnItemTouchListener(itemTouchListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        rvFolder.removeOnItemTouchListener(itemTouchListener)
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

    companion object {
        fun tag(): String {
            return FoldersFragment::class.java.simpleName
        }

        fun newInstance(): FoldersFragment {
            return FoldersFragment()
        }
    }
}