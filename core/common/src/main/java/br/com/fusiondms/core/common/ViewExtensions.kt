package br.com.fusiondms.core.common

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.AnticipateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import com.google.android.material.progressindicator.LinearProgressIndicator

fun View.fadeOutAnimation() {
    val fade = ObjectAnimator.ofFloat(this, View.ALPHA, 1f, 0f)
    fade.start()
}

fun View.fadeOutMoveAnimation() {
    val mover = ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, 0f, -this.height.toFloat())
    mover.interpolator = AccelerateInterpolator(1f)
    val fade = ObjectAnimator.ofFloat(this, View.ALPHA, 1f, 0f)

    val set = AnimatorSet()
    set.duration = 300L
    set.playTogether(mover, fade)

    set.start()

    set.doOnEnd {
        this.animate()
            .translationX(0F)
            .translationY(0F)
            .duration = 0
        set.removeAllListeners()
    }
}

fun View.fadeInMoveAnimation() {
    val mover = ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, this.height.toFloat(), 0f)
    mover.interpolator = AccelerateInterpolator(1f)
    val fade = ObjectAnimator.ofFloat(this, View.ALPHA, 0f, 1f)

    val set = AnimatorSet()
    set.duration = 1500L
    set.playTogether(mover, fade)

    set.start()

    set.doOnEnd {
        this.animate()
            .translationX(0F)
            .translationY(0F)
            .duration = 0
        set.removeAllListeners()
    }
}

fun View.fadeInZoomInAnimation() {
    val fade = ObjectAnimator.ofFloat(this, View.ALPHA, 0f, 1f)
    val originalScaleX = this.scaleX
    val originalScaleY = this.scaleY
    val startScaleX = 2f
    val startScaleY = 2f

    val scaleXAnimator = ObjectAnimator.ofFloat(this, View.SCALE_X, startScaleX, originalScaleX)
    val scaleYAnimator = ObjectAnimator.ofFloat(this, View.SCALE_Y, startScaleY, originalScaleY)

    val anim = AnimatorSet()
    anim.duration = 400
    anim.playTogether(fade, scaleXAnimator, scaleYAnimator)
    anim.start()
}

fun View.scaleXYAnimation() {
    val displayMetrics = resources.displayMetrics

    val screenWidth = displayMetrics.widthPixels
    val screenHeight = displayMetrics.heightPixels
    val x = this.x
    val y = this.y
    val w = this.width
    val h = this.height

    val difX = (screenWidth - w) / 2 - x
    val diffY = (screenHeight - h) / 2 - y

    with(this.animate()) {
        translationX(difX)
        translationY(diffY)
        scaleX(8F)
        scaleY(8F)
        interpolator = AnticipateInterpolator(1f)
        duration = 400
    }
}

fun View.moveInAnimation() {
    if (!this.isVisible) this.visibility = View.VISIBLE
    val mover = ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, this.height.toFloat(), 0f)
    mover.interpolator = AccelerateInterpolator(1f)
    mover.duration = 500L
    mover.start()
}

fun View.moveOutAnimation() {
    if (this.isVisible) this.visibility = View.INVISIBLE
    val mover = ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, 0f, this.height.toFloat())
    mover.interpolator = AccelerateInterpolator(1f)
    mover.duration = 500L
    mover.start()
}

fun View.hideFabAnimation(value: Float) {
    this.animate().scaleX(value).scaleY(value).setDuration(300).start()
}

fun LinearProgressIndicator.progressAnimation(progress: Int) {
    val progressTo = this.progress + progress
    val mover = ObjectAnimator.ofInt(this, "progress", this.progress, progressTo)
    mover.interpolator = DecelerateInterpolator()
    mover.duration = 500L
    mover.start()
}

fun View.fabAnimation(value: Float) {
    this.animate().scaleX(value).scaleY(value).setDuration(0).start()
}