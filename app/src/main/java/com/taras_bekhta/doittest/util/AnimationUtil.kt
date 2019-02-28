package com.taras_bekhta.doittest.util

import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator

class AnimationUtil {
    companion object {
        private const val defaultAnimationDuration = 400.toLong()

        fun animateViewChangeScaleFade(fromView: View, toView: View, viewGone: Boolean = false, duration: Long = defaultAnimationDuration) {
            fromView.animate()
                .setDuration(duration / 2)
                .alpha(0f)
                .scaleX(0f)
                .scaleY(0f)
                .withEndAction {
                    fromView.visibility = if (viewGone) View.GONE else View.INVISIBLE
                    toView.alpha = 0f
                    toView.scaleX = 0f
                    toView.scaleY = 0f
                    toView.visibility = View.VISIBLE
                    toView.animate()
                        .setDuration(duration / 2)
                        .alpha(1f)
                        .scaleX(1f)
                        .scaleY(1f)
                        .setInterpolator(AccelerateDecelerateInterpolator())
                }
        }

        fun animateViewShow(toView: View, duration: Long = defaultAnimationDuration) {
            toView.visibility = View.VISIBLE
            toView.alpha = 0f
            toView.scaleX = 0f
            toView.scaleY = 0f
            toView.animate()
                .setDuration(duration)
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setInterpolator(AccelerateDecelerateInterpolator())
        }

        fun animateViewHide(view: View, viewGone: Boolean = false, duration: Long = defaultAnimationDuration) {
            view.visibility = View.VISIBLE
            view.alpha = 1f
            view.scaleX = 1f
            view.scaleY = 1f
            view.animate()
                .setDuration(duration)
                .alpha(0f)
                .scaleX(0f)
                .scaleY(0f)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .withEndAction {
                    view.visibility = if (viewGone) View.GONE else View.INVISIBLE
                }
        }
    }
}