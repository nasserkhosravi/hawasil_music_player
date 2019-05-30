package com.nasserkhosravi.hawasilmusicplayer.view.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.nasserkhosravi.appcomponent.view.adapter.BaseComponentAdapter
import com.nasserkhosravi.appcomponent.view.fragment.BaseComponentFragment
import com.nasserkhosravi.hawasilmusicplayer.R
import com.nasserkhosravi.hawasilmusicplayer.app.setOnClickListeners
import com.nasserkhosravi.hawasilmusicplayer.data.model.PlayListModel
import com.nasserkhosravi.hawasilmusicplayer.view.adapter.PlayListAdapter
import com.nasserkhosravi.hawasilmusicplayer.view.adapter.RecycleItemListener
import com.nasserkhosravi.hawasilmusicplayer.view.dialog.CreatingPlaylistDialog
import com.nasserkhosravi.hawasilmusicplayer.viewmodel.PlayListsViewModel
import kotlinx.android.synthetic.main.fragment_playlist.*

class PlayListsFragment : BaseComponentFragment(), BaseComponentAdapter.ItemClickListener {
    override val layoutRes: Int
        get() = R.layout.fragment_playlist

    private val adapter = PlayListAdapter()
    private lateinit var viewModel: PlayListsViewModel
    private lateinit var itemTouchListener: RecycleItemListener
    private val creatingDialog: CreatingPlaylistDialog by lazy {
        CreatingPlaylistDialog.newInstance()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getPlayLists.observe(this, Observer {
            adapter.items = it
            rvPlayLists.adapter = adapter
        })
        rvPlayLists.layoutManager = LinearLayoutManager(context)

        tvTitle.text = getString(R.string.PlayLists)
        imgBack.setOnClickListener {
            fragmentManager!!.popBackStack()
        }
        imgCreatePlaylist.setOnClickListener {
            if (!isShowingDialog()) {
                showCreatingPlaylistDialog()
            }
        }
        rvPlayLists.addOnItemTouchListener(itemTouchListener)
    }

    private fun isShowingDialog() = creatingDialog.dialog.isShowing && !creatingDialog.isRemoving

    private fun showCreatingPlaylistDialog() {
        if (creatingDialog.onClickListener == null) {
            creatingDialog.onClickListener = View.OnClickListener {
                when (it!!.id) {
                    R.id.btnConfirm -> {
                        createPlayList()
                    }
                    R.id.btnCancel -> {
                        creatingDialog.dismiss()
                    }
                }
            }
        }
        creatingDialog.showNow(childFragmentManager, CreatingPlaylistDialog.tag())
    }

    override fun onRecycleItemClick(view: View, position: Int) {
        val model = adapter.items!![position]
        flQueue.visibility = View.VISIBLE
//        todo: new dependency
        val queueFragment = QueueFragment.newInstance(model)
        queueFragment.imgBackClickListener = View.OnClickListener {
            flQueue.visibility = View.GONE
        }
        childFragmentManager.beginTransaction()
            .replace(R.id.flQueue, queueFragment)
            .addToBackStack("items")
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        rvPlayLists.removeOnItemTouchListener(itemTouchListener)
        creatingDialog.dismiss()
        setOnClickListeners(null, imgBack, imgCreatePlaylist)
    }

    companion object {
        fun newInstance(): PlayListsFragment {
            return PlayListsFragment()
        }
    }

    private fun createPlayList() {
        val playlistName = creatingDialog.getEditText()!!.text.toString()
        if (playlistName.isNotEmpty() && playlistName.isNotBlank()) {
            val playlistId = viewModel.createPlaylist(playlistName)
            creatingDialog.dismiss()
            if (playlistId > -1) {
                val newPlaylist = PlayListModel(playlistId, playlistName, ArrayList())
                viewModel.getPlayLists.value!!.add(newPlaylist)
                adapter.notifyItemInserted(viewModel.getPlayLists.value!!.lastIndex)
            }
        }
    }
}