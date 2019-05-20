package com.nasserkhosravi.hawasilmusicplayer.view.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.nasserkhosravi.appcomponent.view.fragment.BaseComponentFragment
import com.nasserkhosravi.hawasilmusicplayer.R
import com.nasserkhosravi.hawasilmusicplayer.app.formatString
import com.nasserkhosravi.hawasilmusicplayer.app.safeDispose
import com.nasserkhosravi.hawasilmusicplayer.data.SongEventPublisher
import com.nasserkhosravi.hawasilmusicplayer.data.model.SongModel
import com.nasserkhosravi.hawasilmusicplayer.viewmodel.MiniPlayerViewModel
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_mini_player.*

class MiniPlayerFragment : BaseComponentFragment() {
    override val layoutRes: Int
        get() = R.layout.fragment_mini_player

    private var newSongPlayObserver: Disposable? = null
    private var songCompletedObserver: Disposable? = null
    private var songStatusObserver: Disposable? = null
    private lateinit var viewModel: MiniPlayerViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MiniPlayerViewModel::class.java)
        val lastSongPlayed = viewModel.getLastSong()
        if (lastSongPlayed != null) {
            setSongInfo(lastSongPlayed)
            setBackgroundPassed((lastSongPlayed.songPassed.toFloat() / lastSongPlayed.duration.toFloat()))
            checkButtonStatusView(lastSongPlayed.status.isPlay())
        }

        imgPlayStatus.setOnClickListener {
            viewModel.reversePlay()
        }
        if (viewModel.hasQueue()) {
            view.visibility = View.VISIBLE
        }
        newSongPlayObserver = SongEventPublisher.newSongPlay.subscribe {
            if (view.visibility == View.GONE) {
                view.visibility = View.VISIBLE
            }
            setSongInfo(it!!)
            imgPlayStatus.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp)
        }
        songStatusObserver = SongEventPublisher.songStatusChange.subscribe {
            viewModel.registerProgressReporting()
            checkButtonStatusView(it.isPlay())
        }
        songCompletedObserver = SongEventPublisher.songComplete.subscribe {
            viewModel.unRegisterProgressReporting()
            setBackgroundPassed(0f)
            checkButtonStatusView(false)
        }
        viewModel.getProgress.observe(this, Observer {
            setBackgroundPassed(it)
        })

    }

    private fun setSongInfo(lastSongPlayed: SongModel) {
        tvArtist.text = formatString(R.string.TEMPLATE_artist_album_song, lastSongPlayed.artist, lastSongPlayed.album)
        tvSongTitle.text = lastSongPlayed.title
    }

    private fun checkButtonStatusView(isPlay: Boolean) {
        if (isPlay) {
            imgPlayStatus.setImageResource(R.drawable.ic_pause_black_24dp)
        } else {
            imgPlayStatus.setImageResource(R.drawable.ic_play_circle_filled_black_24dp)
        }
    }

    /**
     * set percentage of background color between 0 and 1
     * 1 will be computed as 100%
     */
    private fun setBackgroundPassed(p: Float) {
        //why 10_000: background level will be complete at 10_000
        flFilled.background.level = (p * 10_000).toInt()
    }

    override fun onStart() {
        super.onStart()
        viewModel.onStart()
    }

    override fun onStop() {
        super.onStop()
        viewModel.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.onDestroyView()

        imgPlayStatus.setOnClickListener(null)
        viewModel.unRegisterProgressReporting()
        safeDispose(newSongPlayObserver, songCompletedObserver, songStatusObserver)
    }

    companion object {
        fun newInstance(): MiniPlayerFragment {
            return MiniPlayerFragment()
        }

        fun tag(): String {
            return MiniPlayerFragment::class.java.name
        }
    }

}