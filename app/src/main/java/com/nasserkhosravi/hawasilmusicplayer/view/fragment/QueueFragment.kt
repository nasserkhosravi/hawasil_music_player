package com.nasserkhosravi.hawasilmusicplayer.view.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.nasserkhosravi.appcomponent.AppContext
import com.nasserkhosravi.appcomponent.view.adapter.BaseComponentAdapter
import com.nasserkhosravi.appcomponent.view.fragment.BaseComponentFragment
import com.nasserkhosravi.hawasilmusicplayer.R
import com.nasserkhosravi.hawasilmusicplayer.data.SongEventPublisher
import com.nasserkhosravi.hawasilmusicplayer.data.model.*
import com.nasserkhosravi.hawasilmusicplayer.view.adapter.SongAdapter
import com.nasserkhosravi.hawasilmusicplayer.viewmodel.QueueViewModel
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_queue.*
import kotlinx.android.synthetic.main.inc_toolbar.*

class QueueFragment : BaseComponentFragment(), BaseComponentAdapter.ItemClickListener {
    override val layoutRes: Int
        get() = R.layout.fragment_queue

    private val adapter = SongAdapter()
    private lateinit var viewModel: QueueViewModel
    private var shuffleModeDisposable: Disposable? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val queueId = arguments!!.getString("id")!!
        val type = arguments!!.getInt("type")
        val title = arguments!!.getString("title")!!

        viewModel = ViewModelProviders.of(this).get(QueueViewModel::class.java)
        viewModel.setArgs(queueId, type)

        tvTitle.text = title
        adapter.itemClickListener = this
        rvQueue.layoutManager = LinearLayoutManager(context)
        viewModel.getSongs.observe(this, Observer {
            adapter.items = it
            rvQueue.adapter = adapter
        })
        //todo: code smell imgBack
        imgBack.setOnClickListener {
            fragmentManager!!.popBackStack()
            parentFragment?.view?.findViewById<View>(R.id.flQueue)?.visibility = View.GONE
        }
        shuffleModeDisposable = SongEventPublisher.shuffleModeChange.subscribe {
            //            adapter.items = QueueBrain.data.queue
//            adapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        shuffleModeDisposable?.dispose()
        adapter.itemClickListener = null
        imgBack.setOnClickListener(null)
    }

    override fun onRecycleItemClick(view: View, position: Int) {
        viewModel.onSongClick(position)
        adapter.makeThisSelect(position)
    }

    companion object {
        fun tag(): String {
            return QueueFragment::class.java.simpleName
        }

        fun newInstance(model: ArtistModel): QueueFragment {
            val fragment = QueueFragment()
            val bundle = Bundle()
            bundle.putInt("type", QueueType.ARTIST.toInt())
            bundle.putString("id", model.id.toString())
            bundle.putString("title", model.title)
            fragment.arguments = bundle
            return fragment
        }

        fun newInstance(model: FlatFolderModel): QueueFragment {
            val fragment = QueueFragment()
            val bundle = Bundle()
            bundle.putInt("type", QueueType.FOLDER.toInt())
            bundle.putString("id", model.path)
            bundle.putString("title", model.name)
            fragment.arguments = bundle
            return fragment
        }

        fun newInstance(model: AlbumModel): QueueFragment {
            val fragment = QueueFragment()
            val bundle = Bundle()
            bundle.putInt("type", QueueType.ALBUM.toInt())
            bundle.putString("id", model.id.toString())
            bundle.putString("title", model.title)
            fragment.arguments = bundle
            return fragment
        }

        fun newInstance(model: PlayListModel): QueueFragment {
            val fragment = QueueFragment()
            val bundle = Bundle()
            bundle.putInt("type", QueueType.PLAYLIST.toInt())
            bundle.putString("id", model.id.toString())
            bundle.putString("title", model.title)
            fragment.arguments = bundle
            return fragment
        }

        fun newInstance(): QueueFragment {
            val fragment = QueueFragment()
            val bundle = Bundle()
            bundle.putInt("type", QueueType.SONGS.toInt())
            bundle.putString("id", "${AppContext.get().packageName}.songList")
            bundle.putString("title", "Songs")
            fragment.arguments = bundle
            return fragment
        }
    }
}