package com.nasserkhosravi.hawasilmusicplayer.view.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.nasserkhosravi.appcomponent.view.adapter.BaseComponentAdapter
import com.nasserkhosravi.appcomponent.view.fragment.BaseComponentFragment
import com.nasserkhosravi.hawasilmusicplayer.R
import com.nasserkhosravi.hawasilmusicplayer.app.setOnClickListeners
import com.nasserkhosravi.hawasilmusicplayer.data.model.PlayListModel
import com.nasserkhosravi.hawasilmusicplayer.view.adapter.PlayListAdapter
import com.nasserkhosravi.hawasilmusicplayer.view.dialog.CreatingPlaylistDialog
import com.nasserkhosravi.hawasilmusicplayer.viewmodel.PlayListsViewModel
import kotlinx.android.synthetic.main.fragment_playlist.*

class PlayListsFragment : BaseComponentFragment(), BaseComponentAdapter.ItemClickListener {
    override val layoutRes: Int
        get() = R.layout.fragment_playlist

    private val adapter = PlayListAdapter()
    private lateinit var viewModel: PlayListsViewModel
    private var creatingDialog: CreatingPlaylistDialog? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(PlayListsViewModel::class.java)
        viewModel.getPlayLists.observe(this, Observer {
            adapter.items = it
            rvPlayLists.adapter = adapter
        })
        adapter.itemClickListener = this
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
    }

    private fun isShowingDialog() = creatingDialog != null && creatingDialog!!.dialog.isShowing && !creatingDialog!!.isRemoving

    private fun showCreatingPlaylistDialog() {
        if (creatingDialog == null) {
            creatingDialog = CreatingPlaylistDialog.newInstance()
            creatingDialog!!.onClickListener = View.OnClickListener {
                when (it!!.id) {
                    R.id.btnConfirm -> {
                        val playlistName = creatingDialog!!.getEditText()!!.text.toString()
                        if (playlistName.isNotEmpty() && playlistName.isNotBlank()) {
                            val playlistId = viewModel.createPlaylist(playlistName)
                            creatingDialog!!.dismiss()
                            if (playlistId > -1) {

                                val newPlaylist = PlayListModel(playlistId, playlistName, ArrayList())
                                viewModel.getPlayLists.value!!.add(newPlaylist)
                                adapter.notifyItemInserted(viewModel.getPlayLists.value!!.lastIndex)
                            }
                        }
                    }
                    R.id.btnCancel -> {
                        creatingDialog!!.dismiss()
                    }
                }
            }
        }
        creatingDialog!!.showNow(childFragmentManager, CreatingPlaylistDialog.tag())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter.itemClickListener = null
        creatingDialog?.dismiss()
        setOnClickListeners(null, imgBack, imgCreatePlaylist)
    }

    override fun onRecycleItemClick(view: View, position: Int) {
        val model = adapter.items!![position]
        flQueue.visibility = View.VISIBLE
        childFragmentManager.beginTransaction()
            .replace(R.id.flQueue, QueueFragment.newInstance(model))
            .addToBackStack("queue")
            .commit()
    }

    companion object {
        fun newInstance(): PlayListsFragment {
            return PlayListsFragment()
        }
    }

}