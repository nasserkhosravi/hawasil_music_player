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
import com.nasserkhosravi.hawasilmusicplayer.data.model.*
import com.nasserkhosravi.hawasilmusicplayer.di.DaggerQueueFragmentComponent
import com.nasserkhosravi.hawasilmusicplayer.di.QueueFragmentModule
import com.nasserkhosravi.hawasilmusicplayer.di.RecycleItemListenerModule
import com.nasserkhosravi.hawasilmusicplayer.view.adapter.QueueAdapter
import com.nasserkhosravi.hawasilmusicplayer.view.adapter.RecycleItemListener
import com.nasserkhosravi.hawasilmusicplayer.viewmodel.QueueViewModel
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_queue.*
import kotlinx.android.synthetic.main.inc_toolbar.*
import javax.inject.Inject

class QueueFragment : BaseComponentFragment(), BaseComponentAdapter.ItemClickListener {
    override val layoutRes: Int
        get() = R.layout.fragment_queue

    @Inject
    lateinit var adapter: QueueAdapter
    @Inject
    lateinit var viewModel: QueueViewModel
    @Inject
    lateinit var itemTouchListener: RecycleItemListener

    private var shuffleModeDisposable: Disposable? = null
    var imgBackClickListener: View.OnClickListener? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DaggerQueueFragmentComponent.builder()
            .queueFragmentModule(QueueFragmentModule(this))
            .recycleItemListenerModule(RecycleItemListenerModule(context!!, this))
            .build()
            .inject(this)
        val queueId = arguments!!.getString("id")!!
        val type = arguments!!.getInt("type")
        val title = arguments!!.getString("title")!!

        viewModel = ViewModelProviders.of(this).get(QueueViewModel::class.java)
        viewModel.setArgs(queueId, type)

        tvTitle.text = title
        rvQueue.layoutManager = LinearLayoutManager(context)
        viewModel.getSongs.observe(this, Observer {
            adapter.items = it
            rvQueue.adapter = adapter
        })
        imgBack.setOnClickListener {
            fragmentManager!!.popBackStack()
            imgBackClickListener?.onClick(it)
        }
        rvQueue.addOnItemTouchListener(itemTouchListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        shuffleModeDisposable?.dispose()
        imgBackClickListener = null
        rvQueue.removeOnItemTouchListener(itemTouchListener)
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