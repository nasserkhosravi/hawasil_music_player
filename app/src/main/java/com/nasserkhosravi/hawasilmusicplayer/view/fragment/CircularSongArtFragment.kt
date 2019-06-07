package com.nasserkhosravi.hawasilmusicplayer.view.fragment

import android.animation.ValueAnimator
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.ViewModelProviders
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.nasserkhosravi.hawasilmusicplayer.R
import com.nasserkhosravi.hawasilmusicplayer.data.model.SongModel
import com.nasserkhosravi.hawasilmusicplayer.view.fragment.component.BaseFragment
import com.nasserkhosravi.hawasilmusicplayer.viewmodel.CircularSongArtViewModel
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.row_cirular_song_art.*

class CircularSongArtFragment : BaseFragment() {

    override val layoutRes = R.layout.row_cirular_song_art
    private lateinit var viewModel: CircularSongArtViewModel
    private val compositeDisposable = CompositeDisposable()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val songModel = arguments!!.getParcelable<SongModel>("song")!!
        viewModel = ViewModelProviders.of(this).get(CircularSongArtViewModel::class.java)
        setInfoInView(songModel)
    }

    private fun setInfoInView(model: SongModel) {
        tvSongTitle.text = model.title
        tvArtist.text = model.artist
        val art = viewModel.getArt(context!!, model)
        Glide.with(context!!).load(art).apply(RequestOptions.circleCropTransform()).into(imgPlayer)
        viewModel.getPalette(context!!, model, compositeDisposable) {
            setPalletInView(it[0])
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
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
        fun newInstance(model: SongModel): CircularSongArtFragment {
            return CircularSongArtFragment().apply {
                val bundle = Bundle()
                bundle.putParcelable("song", model)
                arguments = bundle
            }
        }

        fun tag(): String {
            return CircularSongArtFragment::class.java.simpleName
        }
    }
}