package com.nasserkhosravi.hawasilmusicplayer.view.fragment

import android.animation.ValueAnimator
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.nasserkhosravi.appcomponent.utils.UIUtils
import com.nasserkhosravi.appcomponent.view.fragment.BaseComponentFragment
import com.nasserkhosravi.hawasilmusicplayer.FormatUtils
import com.nasserkhosravi.hawasilmusicplayer.R
import com.nasserkhosravi.hawasilmusicplayer.app.safeDispose
import com.nasserkhosravi.hawasilmusicplayer.app.setOnClickListeners
import com.nasserkhosravi.hawasilmusicplayer.data.QueueBrain
import com.nasserkhosravi.hawasilmusicplayer.data.SongEventPublisher
import com.nasserkhosravi.hawasilmusicplayer.data.model.SongModel
import com.nasserkhosravi.hawasilmusicplayer.data.model.SongStatus
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_player.*
import java.io.FileNotFoundException

class SongPlayerFragment : BaseComponentFragment(), View.OnClickListener {
    override val layoutRes: Int
        get() = R.layout.fragment_player

    private var seekBarChangeListener: SeekBar.OnSeekBarChangeListener? = null
    private var seekBarObserver: Disposable? = null
    private var newSongPlayObserver: Disposable? = null
    private var songCompletedObserver: Disposable? = null
    private var songStatusObserver: Disposable? = null
    private var progressObserver: Disposable? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //seek bar has un wanted padding and we don't want it's padding
        skbTimeline.setPadding(0, 0, 0, 0)

        val model = QueueBrain.getSelected()!!
        setModelInView(model)
        UIUtils.filterColor(imgFavorite, R.color.favorite)
        setOnClickListeners(this, imgPlayStatus, imgFavorite, imgUp, imgPrevious, imgNext, imgShuffle, imgRepeat)

        newSongPlayObserver = SongEventPublisher.newSongPlay.subscribe {
            setModelInView(it!!)
        }
        songStatusObserver = SongEventPublisher.songStatusChange.subscribe {
            checkButtonStatusView(it)
        }
        songCompletedObserver = SongEventPublisher.songComplete.subscribe {
            checkButtonStatusView(SongStatus.PAUSE)
//            progressObserver?.dispose()
            refreshDynamicTimeInfo(model)
        }
        refreshImgShuffle()
        refreshImgRepeat()
    }

    override fun onStart() {
        super.onStart()
        activity?.findViewById<View>(R.id.flPlayer)!!.visibility = View.VISIBLE
        activity?.findViewById<View>(R.id.layoutBody)!!.visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()
        activity?.findViewById<View>(R.id.flPlayer)!!.visibility = View.GONE
        activity?.findViewById<View>(R.id.layoutBody)!!.visibility = View.VISIBLE
    }

    private fun setModelInView(model: SongModel) {
        tvSongTitle.text = model.title
        tvArtist.text = model.artist
        tvDuration.text = model.getFormatDuration()
        skbTimeline.max = model.duration.toInt()
        refreshDynamicTimeInfo(model)
        //first remove last seek bar listener
        //then set new
        skbTimeline.setOnSeekBarChangeListener(null)
        seekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    QueueBrain.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        }
        skbTimeline.setOnSeekBarChangeListener(seekBarChangeListener)
        if (model.artUri != null) {
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(context!!.contentResolver, model.artUri!!)
                generateAndSetColor(bitmap)
                Glide.with(this).load(bitmap).apply(RequestOptions.circleCropTransform()).into(imgPlayer)
            } catch (e: FileNotFoundException) {
                loadDefaultImage()
            }
        } else {
            loadDefaultImage()
        }
        registerReportTimeOfSong(model)
        checkButtonStatusView(model.status)
    }

    private fun refreshDynamicTimeInfo(model: SongModel) {
        tvCurrentDuration.text = FormatUtils.milliSeconds(model.songPassed)
        skbTimeline.progress = model.songPassed.toInt()
    }

    private fun registerReportTimeOfSong(model: SongModel) {
        progressObserver?.dispose()
        progressObserver = QueueBrain.playerService!!.progressPublisher.observable.subscribe {
            skbTimeline.progress = model.songPassed.toInt()
            tvCurrentDuration.text = FormatUtils.milliSeconds(model.songPassed)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.imgPlayStatus -> {
                QueueBrain.reversePlay()
            }
            R.id.imgUp -> {
                fragmentManager?.popBackStack()
            }
            R.id.imgPrevious -> {
                QueueBrain.playPrevious()
            }
            R.id.imgNext -> {
                QueueBrain.playNext()
            }
            R.id.imgRepeat -> {
                QueueBrain.toggleRepeat()
                refreshImgRepeat()
            }
            R.id.imgShuffle -> {
                QueueBrain.toggleShuffle()
                refreshImgShuffle()
            }
        }
    }

    private fun refreshImgRepeat() {
        if (QueueBrain.data.isEnableRepeat) {
            UIUtils.filterColor(imgRepeat, R.color.main)
        } else {
            UIUtils.filterColor(imgRepeat, R.color.second)
        }
    }

    private fun refreshImgShuffle() {
        if (QueueBrain.data.isShuffle) {
            UIUtils.filterColor(imgShuffle, R.color.main)
        } else {
            UIUtils.filterColor(imgShuffle, R.color.second)
        }
    }

    private fun loadDefaultImage() {
        Glide.with(this).load(R.drawable.art_balabar).apply(RequestOptions.circleCropTransform()).into(imgPlayer)
    }

    private fun checkButtonStatusView(songStatus: SongStatus) {
        if (songStatus.isPlay()) {
            imgPlayStatus.setImageResource(R.drawable.ic_pause_black_24dp)
        } else {
            imgPlayStatus.setImageResource(R.drawable.ic_play_arrow_black_24dp)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        skbTimeline.setOnSeekBarChangeListener(null)
        safeDispose(seekBarObserver, newSongPlayObserver, songCompletedObserver, songStatusObserver, progressObserver)
        setOnClickListeners(null, imgPlayStatus, imgFavorite, imgUp, imgPrevious, imgNext, imgShuffle, imgRepeat, imgPlayStatus)
    }

    private fun generateAndSetColor(bitmap: Bitmap) {
        Palette.from(bitmap).generate { palette ->
            //some images may not contain some profile color: like light vibrant,be careful
            val colorL1 = ColorUtils.setAlphaComponent(palette!!.mutedSwatch!!.rgb, 60)
            val colorL2 = ColorUtils.setAlphaComponent(palette.mutedSwatch!!.rgb, 30)

            val mDrawable = ContextCompat.getDrawable(activity!!, R.drawable.dr_circle)!!
            val mDrawable2 = ContextCompat.getDrawable(activity!!, R.drawable.dr_circle)!!

            flL1.background = mDrawable
            layoutL2.background = mDrawable2

            mDrawable.colorFilter = PorterDuffColorFilter(colorL1, PorterDuff.Mode.SRC_IN)
            mDrawable2.colorFilter = PorterDuffColorFilter(colorL2, PorterDuff.Mode.SRC_IN)
        }
    }

    private fun startAnimation(colorL1: Int, mDrawable: Drawable, colorL2: Int, mDrawable2: Drawable) {
        val anim = ValueAnimator.ofFloat(0f, 1f).setDuration(3000)
        anim.repeatMode = ValueAnimator.REVERSE
        anim.repeatCount = Animation.INFINITE
        anim.interpolator = DecelerateInterpolator()
        anim.addUpdateListener {
            val value = it.animatedValue as Float
            val runColor = ColorUtils.setAlphaComponent(colorL1, (value * 100).toInt())
            mDrawable.colorFilter = PorterDuffColorFilter(runColor, PorterDuff.Mode.SRC_IN)
            val runColor2 = ColorUtils.setAlphaComponent(colorL2, (it.animatedFraction * 100).toInt())
            mDrawable2.colorFilter = PorterDuffColorFilter(runColor2, PorterDuff.Mode.SRC_IN)
        }
        anim.start()
    }

    companion object {
        fun newInstance(): SongPlayerFragment {
            return SongPlayerFragment()
        }

        fun tag(): String {
            return SongPlayerFragment::class.java.simpleName
        }
    }
}