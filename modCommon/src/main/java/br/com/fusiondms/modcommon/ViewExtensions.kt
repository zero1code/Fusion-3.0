package br.com.fusiondms.modcommon

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.core.animation.doOnEnd

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

fun View.fabAnimation(value: Float) {
    this.animate().scaleX(value).scaleY(value).setDuration(0).start()
}