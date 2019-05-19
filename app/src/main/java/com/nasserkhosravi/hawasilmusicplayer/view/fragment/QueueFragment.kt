package com.nasserkhosravi.hawasilmusicplayer.view.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.nasserkhosravi.appcomponent.view.adapter.BaseComponentAdapter
import com.nasserkhosravi.appcomponent.view.fragment.BaseComponentFragment
import com.nasserkhosravi.hawasilmusicplayer.R
import com.nasserkhosravi.hawasilmusicplayer.data.MediaProvider
import com.nasserkhosravi.hawasilmusicplayer.data.QueueBrain
import com.nasserkhosravi.hawasilmusicplayer.data.model.*
import com.nasserkhosravi.hawasilmusicplayer.view.adapter.SongAdapter
import kotlinx.android.synthetic.main.fragment_queue.*
import kotlinx.android.synthetic.main.inc_toolbar.*

class QueueFragment : BaseComponentFragment(), BaseComponentAdapter.ItemClickListener {
    override val layoutRes: Int
        get() = R.layout.fragment_queue

    private val adapter = SongAdapter()
    private lateinit var queueId: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (QueueType.fromId(getQueueType())) {
            QueueType.FOLDER -> {
                val model = arguments!!.getParcelable<FlatFolderModel>("selected")!!
                tvTitle.text = model.name
                adapter.items = MediaProvider.getSongsByFolder(model)
                queueId = model.name
            }
            QueueType.ARTIST -> {
                val id = arguments!!.getLong("id")
                tvTitle.text = arguments!!.getString("title")!!
                adapter.items = MediaProvider.getSongsByArtist(id)
                queueId = id.toString()
            }
            QueueType.ALBUM -> {
                val id = arguments!!.getLong("id")
                tvTitle.text = arguments!!.getString("title")!!
                adapter.items = MediaProvider.getSongsByAlbum(id)
                queueId = id.toString()
            }
            QueueType.PLAYLIST -> {
                val id = arguments!!.getLong("id")
                tvTitle.text = arguments!!.getString("title")!!
                adapter.items = MediaProvider.getPlaylistTracks(id)
                queueId = id.toString()
            }
            else -> throw IllegalArgumentException()
        }
        adapter.itemClickListener = this
        rvQueue.adapter = adapter
        rvQueue.layoutManager = LinearLayoutManager(context)
        imgBack.setOnClickListener {
            fragmentManager!!.popBackStack()
            parentFragment!!.view!!.findViewById<View>(R.id.flQueue).visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter.itemClickListener = null
        imgBack.setOnClickListener(null)
    }

    fun getQueueType(): Int {
        return arguments!!.getInt("type")
    }

    override fun onRecycleItemClick(view: View, position: Int) {
        QueueBrain.checkNewQueueRequest(adapter.items!!, position, queueId)
        adapter.makeThisSelect(position)
    }

    companion object {
        fun newInstance(model: ArtistModel): QueueFragment {
            val fragment = QueueFragment()
            val bundle = Bundle()
            bundle.putInt("type", QueueType.ARTIST.toInt())
            bundle.putLong("id", model.id.toLong())
            bundle.putString("title", model.title)

            fragment.arguments = bundle
            return fragment
        }

        fun newInstance(model: FlatFolderModel): QueueFragment {
            val fragment = QueueFragment()
            val bundle = Bundle()
            bundle.putInt("type", QueueType.FOLDER.toInt())
            bundle.putParcelable("selected", model)
            fragment.arguments = bundle
            return fragment
        }

        fun newInstance(model: AlbumModel): QueueFragment {
            val fragment = QueueFragment()
            val bundle = Bundle()
            bundle.putInt("type", QueueType.ALBUM.toInt())
            bundle.putLong("id", model.id.toLong())
            bundle.putString("title", model.title)
            fragment.arguments = bundle
            return fragment
        }

        fun newInstance(model: PlayListModel): QueueFragment {
            val fragment = QueueFragment()
            val bundle = Bundle()
            bundle.putInt("type", QueueType.PLAYLIST.toInt())
            bundle.putLong("id", model.id)
            bundle.putString("title", model.title)
            fragment.arguments = bundle
            return fragment
        }
    }
}