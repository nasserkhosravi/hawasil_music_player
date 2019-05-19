package com.nasserkhosravi.hawasilmusicplayer.view.fragment

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import com.nasserkhosravi.appcomponent.view.fragment.BaseComponentFragment
import com.nasserkhosravi.hawasilmusicplayer.R
import com.nasserkhosravi.hawasilmusicplayer.app.formatString
import com.nasserkhosravi.hawasilmusicplayer.app.safeDispose
import com.nasserkhosravi.hawasilmusicplayer.data.MediaPlayerService
import com.nasserkhosravi.hawasilmusicplayer.data.QueueBrain
import com.nasserkhosravi.hawasilmusicplayer.data.SongEventPublisher
import com.nasserkhosravi.hawasilmusicplayer.data.UserPref
import com.nasserkhosravi.hawasilmusicplayer.data.model.SongModel
import com.nasserkhosravi.hawasilmusicplayer.data.model.SongStatus
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_mini_player.*

class MiniPlayerFragment : BaseComponentFragment() {
    override val layoutRes: Int
        get() = R.layout.fragment_mini_player

    private var player: MediaPlayerService? = null

    private var newSongPlayObserver: Disposable? = null
    private var songCompletedObserver: Disposable? = null
    private var songStatusObserver: Disposable? = null
    private var progressObserver: Disposable? = null

    private lateinit var serviceConnection: ServiceConnection

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val lastSongPlayed = QueueBrain.getSelected()
        if (lastSongPlayed != null) {
            setSongDataInTV(lastSongPlayed)
            setBackgroundPassed((lastSongPlayed.songPassed.toFloat() / lastSongPlayed.duration.toFloat()))
            checkButtonStatusView(lastSongPlayed.status)
        }

        imgPlayStatus.setOnClickListener {
            QueueBrain.reversePlay()
        }
        if (UserPref.hasQueue()) {
            view.visibility = View.VISIBLE
        }
        newSongPlayObserver = SongEventPublisher.newSongPlay.subscribe {
            if (view.visibility == View.GONE) {
                view.visibility = View.VISIBLE
            }
            setSongDataInTV(it!!)
            imgPlayStatus.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp)
        }
        songStatusObserver = SongEventPublisher.songStatusChange.subscribe {
            checkButtonStatusView(it)
        }
        songCompletedObserver = SongEventPublisher.songComplete.subscribe {
            setBackgroundPassed(0f)
            checkButtonStatusView(SongStatus.PAUSE)
        }

        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                player = (service as MediaPlayerService.LocalBinder).service
                registerProgressObserver()
            }

            override fun onServiceDisconnected(name: ComponentName) {}
        }
        val serviceIntent = Intent(context, MediaPlayerService::class.java)
        context!!.startService(serviceIntent)
        context!!.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun setSongDataInTV(lastSongPlayed: SongModel) {
        tvArtist.text = formatString(R.string.TEMPLATE_artist_album_song, lastSongPlayed.artist, lastSongPlayed.album)
        tvSongTitle.text = lastSongPlayed.title
    }

    private fun checkButtonStatusView(it: SongStatus) {
        if (it.isPlay()) {
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
        if (player != null) {
            registerProgressObserver()
        }
    }

    fun registerProgressObserver() {
        if (progressObserver == null || progressObserver?.isDisposed!!) {
            progressObserver = player!!.progressPublisher.observable.subscribe {
                if (player!!.isReadyToComputePassedDuration()) {
                    setBackgroundPassed(it.toFloat())
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (player != null) {
            context?.unbindService(serviceConnection)
        }
        imgPlayStatus.setOnClickListener(null)
        safeDispose(newSongPlayObserver, songCompletedObserver, songStatusObserver, progressObserver)
    }

    override fun onStop() {
        super.onStop()
        progressObserver?.dispose()
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