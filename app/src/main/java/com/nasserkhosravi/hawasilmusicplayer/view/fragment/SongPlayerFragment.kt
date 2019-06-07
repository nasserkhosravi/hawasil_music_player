package com.nasserkhosravi.hawasilmusicplayer.view.fragment

import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.nasserkhosravi.appcomponent.utils.UIUtils
import com.nasserkhosravi.hawasilmusicplayer.FormatUtils
import com.nasserkhosravi.hawasilmusicplayer.R
import com.nasserkhosravi.hawasilmusicplayer.app.setOnClickListeners
import com.nasserkhosravi.hawasilmusicplayer.data.QueueEvents
import com.nasserkhosravi.hawasilmusicplayer.data.QueueManager
import com.nasserkhosravi.hawasilmusicplayer.data.UIMediaCommand
import com.nasserkhosravi.hawasilmusicplayer.data.model.SongModel
import com.nasserkhosravi.hawasilmusicplayer.di.DaggerSongPlayerFragmentComponent
import com.nasserkhosravi.hawasilmusicplayer.di.SongPlayerModule
import com.nasserkhosravi.hawasilmusicplayer.view.adapter.CircularSongArtPagerAdapter
import com.nasserkhosravi.hawasilmusicplayer.view.fragment.component.BaseFragment
import com.nasserkhosravi.hawasilmusicplayer.viewmodel.SongPlayerViewModel
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_player.*
import javax.inject.Inject

class SongPlayerFragment : BaseFragment(), View.OnClickListener {

    override val layoutRes: Int
        get() = R.layout.fragment_player

    private var seekBarChangeListener: SeekBar.OnSeekBarChangeListener? = null
    @Inject
    lateinit var compositeDisposable: CompositeDisposable
    @Inject
    lateinit var viewModel: SongPlayerViewModel

    private lateinit var onPageChangeListener: ViewPager.OnPageChangeListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DaggerSongPlayerFragmentComponent.builder().songPlayerModule(SongPlayerModule(this)).build().inject(this)
//        seek bar has un wanted padding and we don't want it's padding
        skbTimeline.setPadding(0, 0, 0, 0)
        viewModel = ViewModelProviders.of(this).get(SongPlayerViewModel::class.java)
        val progressObserver = viewModel.getSongChangeEvent().subscribe {
            refreshTimeInfo(it)
        }
        viewModel.getFavorite.observe(this, Observer {
            refreshImgFavorite(it)
        })

        val songStatusObserver = viewModel.getSongStatusEvent().subscribe {
            refreshStatusView(it.isPlay())
        }
        val songCompletedObserver = viewModel.getSongCompleteEvent().subscribe {
            refreshStatusView(false)
            refreshTimeInfo(0)
        }
        compositeDisposable.add(progressObserver)
        compositeDisposable.add(songStatusObserver)
        compositeDisposable.add(songCompletedObserver)
        if (arguments!!.getBoolean("fromInternal")) {
            viewModel.getRepeat.observe(this, Observer {
                refreshImgRepeat(it)
            })
            val shuffleModeDisposable = viewModel.getShuffleEvent().subscribe {
                refreshImgShuffle(it)
            }
            val newSongPlayObserver = viewModel.getNewSongEvent().subscribe {
                setSongInfoInView(it!!)
            }
            compositeDisposable.add(shuffleModeDisposable)
            compositeDisposable.add(newSongPlayObserver)
            refreshImgShuffle(viewModel.getShuffle.value!!)
        } else {
            imgShuffle.visibility = View.GONE
            imgRepeat.visibility = View.GONE
            UIMediaCommand.resume()
        }
        UIUtils.filterColor(imgFavorite, R.color.favorite)

        val model = viewModel.getCurrentSong()
        setSongInfoInView(model)
        setOnClickListeners(this, imgPlayStatus, imgFavorite, imgUp, imgPrevious, imgNext, imgShuffle, imgRepeat)

        seekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    viewModel.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        }
        skbTimeline.setOnSeekBarChangeListener(seekBarChangeListener)

        val queue = QueueManager.get().queue
        val adapter = CircularSongArtPagerAdapter(childFragmentManager, queue!!)
        vpCircularSongArt.adapter = adapter
        vpCircularSongArt.currentItem = queue.selectedIndex
        onPageChangeListener = object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                QueueManager.get().playByPosition(position)
            }

            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
        }
        vpCircularSongArt.addOnPageChangeListener(onPageChangeListener)

        val newSongDisposable = QueueEvents.newSongPlay.subscribe {
            vpCircularSongArt.currentItem = queue.selectedIndex
        }
        compositeDisposable.add(newSongDisposable)
    }

    private fun setSongInfoInView(model: SongModel) {
        tvDuration.text = model.getFormatDuration()
        skbTimeline.max = model.duration.toInt()
        refreshStatusView(model.status.isPlay())
        refreshTimeInfo(model.passedDuration)
    }

    private fun refreshTimeInfo(passedDuration: Long) {
        skbTimeline.progress = passedDuration.toInt()
        tvCurrentDuration.text = FormatUtils.milliSeconds(passedDuration)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.imgPlayStatus -> {
                viewModel.reversePlay()
            }
            R.id.imgUp -> {
                fragmentManager?.popBackStack()
            }
            R.id.imgPrevious -> {
                viewModel.playPrevious()
            }
            R.id.imgNext -> {
                viewModel.playNext()
            }
            R.id.imgRepeat -> {
                viewModel.toggleRepeat()
            }
            R.id.imgShuffle -> {
                viewModel.toggleShuffle()
            }
            R.id.imgFavorite -> {
                viewModel.toggleFavorite()
            }
        }
    }

    private fun refreshImgRepeat(isEnable: Boolean) {
        if (isEnable) {
            UIUtils.filterColor(imgRepeat, R.color.main)
        } else {
            UIUtils.filterColor(imgRepeat, R.color.second)
        }
    }

    private fun refreshImgShuffle(isEnable: Boolean) {
        if (isEnable) {
            UIUtils.filterColor(imgShuffle, R.color.main)
        } else {
            UIUtils.filterColor(imgShuffle, R.color.second)
        }
    }

    private fun refreshImgFavorite(isEnable: Boolean) {
        if (isEnable) {
            imgFavorite.setImageResource(R.drawable.ic_favorite_black_24dp)
        } else {
            imgFavorite.setImageResource(R.drawable.ic_favorite_border_black_24dp)
        }
    }

    private fun refreshStatusView(isPlay: Boolean) {
        if (isPlay) {
            imgPlayStatus.setImageResource(R.drawable.ic_pause_black_24dp)
        } else {
            imgPlayStatus.setImageResource(R.drawable.ic_play_arrow_black_24dp)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        vpCircularSongArt.removeOnPageChangeListener(onPageChangeListener)
        skbTimeline.setOnSeekBarChangeListener(null)
        seekBarChangeListener = null
        compositeDisposable.clear()
        setOnClickListeners(null, imgPlayStatus, imgFavorite, imgUp, imgPrevious, imgNext, imgShuffle, imgRepeat, imgPlayStatus)
    }

    companion object {
        fun newInstance(fromInternal: Boolean = true): SongPlayerFragment {
            return SongPlayerFragment().apply {
                val bundle = Bundle()
                bundle.putBoolean("fromInternal", fromInternal)
                arguments = bundle
            }
        }

        fun tag(): String {
            return SongPlayerFragment::class.java.simpleName
        }
    }
}