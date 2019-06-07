package com.nasserkhosravi.hawasilmusicplayer.view

import android.view.View
import androidx.viewpager.widget.ViewPager

class DepthTransformation : ViewPager.PageTransformer {
    override fun transformPage(page: View, position: Float) {

        if (position < -1) {    // [-Infinity,-1)
            // This page is way off-screen to the left.
            page.alpha = 0f

        } else if (position <= 0) {    // [-1,0]
            page.alpha = 1f
            page.translationX = 0f
            page.scaleX = 1f
            page.scaleY = 1f

        } else if (position <= 1) {    // (0,1]
            page.translationX = -position * page.width
            page.alpha = 1 - Math.abs(position)
            page.scaleX = 1 - Math.abs(position)
            page.scaleY = 1 - Math.abs(position)

        } else {    // (1,+Infinity]
            // This page is way off-screen to the right.
            page.alpha = 0f
        }
    }
}

//like
class ZoomOutTransformation : ViewPager.PageTransformer {

    override fun transformPage(page: View, position: Float) {
        if (position < -1) {  // [-Infinity,-1)
            // This page is way off-screen to the left.
            page.alpha = 0f

        } else if (position <= 1) { // [-1,1]
            page.scaleX = Math.max(MIN_SCALE, 1 - Math.abs(position))
            page.scaleY = Math.max(MIN_SCALE, 1 - Math.abs(position))
            page.alpha = Math.max(MIN_ALPHA, 1 - Math.abs(position))
        } else {  // (1,+Infinity]
            // This page is way off-screen to the right.
            page.alpha = 0f
        }
    }

    companion object {
        private val MIN_SCALE = 0.65f
        private val MIN_ALPHA = 0.3f
    }
}

//like
class VerticalFlipTransformation : ViewPager.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        page.translationX = -position * page.width
        page.cameraDistance = 12000f

        if (position < 0.5 && position > -0.5) {
            page.visibility = View.VISIBLE
        } else {
            page.visibility = View.INVISIBLE
        }

        if (position < -1) {     // [-Infinity,-1)
            // This page is way off-screen to the left.
            page.alpha = 0f

        } else if (position <= 0f) {    // [-1,0]
            page.alpha = 1f
            page.rotationY = 180 * (1 - Math.abs(position) + 1)

        } else if (position <= 1) {    // (0,1]
            page.alpha = 1f
            page.rotationY = -180 * (1 - Math.abs(position) + 1)

        } else {    // (1,+Infinity]
            // This page is way off-screen to the right.
            page.alpha = 0f

        }

    }
}

