package com.nasserkhosravi.hawasilmusicplayer.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import com.nasserkhosravi.hawasilmusicplayer.R
import com.nasserkhosravi.hawasilmusicplayer.app.formatString
import com.nasserkhosravi.hawasilmusicplayer.data.MediaTerminal
import com.nasserkhosravi.hawasilmusicplayer.data.model.SongModel
import com.nasserkhosravi.hawasilmusicplayer.di.DaggerMiniPlayerFragmentComponent
import com.nasserkhosravi.hawasilmusicplayer.di.MiniPlayerFragmentModule
import com.nasserkhosravi.hawasilmusicplayer.view.fragment.component.BaseFragment
import com.nasserkhosravi.hawasilmusicplayer.viewmodel.MiniPlayerViewModel
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_mini_player.*
import javax.inject.Inject

class MiniPlayerFragment : BaseFragment() {
    override val layoutRes: Int
        get() = R.layout.fragment_mini_player
    @Inject
    lateinit var viewModel: MiniPlayerViewModel
    @Inject
    lateinit var compositeDisposable: CompositeDisposable

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        DaggerMiniPlayerFragmentComponent.builder().miniPlayerFragmentModule(MiniPlayerFragmentModule(this))
            .build().inject(this)
        val lastSongPlayed = viewModel.getLastSong()
        if (lastSongPlayed != null) {
            setSongInfo(lastSongPlayed)
            setBackgroundPassed((lastSongPlayed.passedDuration.toFloat() / lastSongPlayed.duration.toFloat()))
            checkButtonStatusView(lastSongPlayed.status.isPlay())
            Log.d(tag(), "${lastSongPlayed.toJson()}: ")
        }

        imgPlayStatus.setOnClickListener {
            viewModel.reversePlay()
        }
        if (viewModel.hasQueue()) {
            view.visibility = View.VISIBLE
        }
        val newSongPlayDisposable = viewModel.getNewSongPlay().subscribe {
            if (view.visibility == View.GONE) {
                view.visibility = View.VISIBLE
            }
            setSongInfo(it!!)
            imgPlayStatus.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp)
        }
        val songStatusDisposable = viewModel.getSongStatus().subscribe {
            checkButtonStatusView(it.isPlay())
        }
        val songCompletedDisposable = viewModel.getSongComplete().subscribe {
            setBackgroundPassed(0f)
            checkButtonStatusView(false)
        }
        val songPassedDisposable = viewModel.getSongPassed().subscribe {
            if (it > 0) {
                setBackgroundPassed(MediaTerminal.queue.selected!!.computePassedDuration())
            }
        }
        compositeDisposable.add(newSongPlayDisposable)
        compositeDisposable.add(songStatusDisposable)
        compositeDisposable.add(songCompletedDisposable)
        compositeDisposable.add(songPassedDisposable)
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

    override fun onDestroyView() {
        super.onDestroyView()
        imgPlayStatus.setOnClickListener(null)
        compositeDisposable.clear()
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