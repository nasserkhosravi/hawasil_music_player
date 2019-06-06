package com.nasserkhosravi.hawasilmusicplayer.view.fragment

import android.animation.ValueAnimator
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.nasserkhosravi.appcomponent.utils.UIUtils
import com.nasserkhosravi.hawasilmusicplayer.FormatUtils
import com.nasserkhosravi.hawasilmusicplayer.R
import com.nasserkhosravi.hawasilmusicplayer.app.setOnClickListeners
import com.nasserkhosravi.hawasilmusicplayer.data.UIMediaCommand
import com.nasserkhosravi.hawasilmusicplayer.data.model.SongModel
import com.nasserkhosravi.hawasilmusicplayer.di.DaggerSongPlayerFragmentComponent
import com.nasserkhosravi.hawasilmusicplayer.di.SongPlayerModule
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
    }

    private fun setSongInfoInView(model: SongModel) {
        tvSongTitle.text = model.title
        tvArtist.text = model.artist
        tvDuration.text = model.getFormatDuration()
        skbTimeline.max = model.duration.toInt()
        refreshStatusView(model.status.isPlay())
        val art = viewModel.getArt(context!!)
        refreshTimeInfo(model.passedDuration)
        viewModel.getPalette(context!!, compositeDisposable) {
            setPalletInView(it[0])
        }
        Glide.with(this).load(art).apply(RequestOptions.circleCropTransform()).into(imgPlayer)
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
        skbTimeline.setOnSeekBarChangeListener(null)
        seekBarChangeListener = null
        compositeDisposable.clear()
        setOnClickListeners(null, imgPlayStatus, imgFavorite, imgUp, imgPrevious, imgNext, imgShuffle, imgRepeat, imgPlayStatus)
    }

    private fun setPalletInView(mutedSwatch: Palette.Swatch) {
        val colorL1 = ColorUtils.setAlphaComponent(mutedSwatch.rgb, 60)
        val colorL2 = ColorUtils.setAlphaComponent(mutedSwatch.rgb, 30)

        val mDrawable = ContextCompat.getDrawable(activity!!, R.drawable.dr_circle)!!
        val mDrawable2 = ContextCompat.getDrawable(activity!!, R.drawable.dr_circle)!!

        flL1.background = mDrawable
        layoutL2.background = mDrawable2

        mDrawable.colorFilter = PorterDuffColorFilter(colorL1, PorterDuff.Mode.SRC_IN)
        mDrawable2.colorFilter = PorterDuffColorFilter(colorL2, PorterDuff.Mode.SRC_IN)
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