package br.com.fusiondms.modcommon.snackbar

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import br.com.fusiondms.modcommon.R
import br.com.fusiondms.modcommon.databinding.SnackLayoutBinding
import br.com.fusiondms.modcommon.getActionBarSize
import br.com.fusiondms.modcommon.getColorFromAttr
import br.com.fusiondms.modcommon.statusBarIconColor
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.internal.managers.FragmentComponentManager


fun View.mensagemCurta(message: String, isError: Boolean = false) {
    val activity = FragmentComponentManager.findActivity(this.context) as Activity
    statusBarIconColor(activity, Color.BLACK)
    val inflater = LayoutInflater.from(this.context)
    val binding = SnackLayoutBinding.inflate(inflater)
    binding.snackMensagem.text = message
    binding.snackCard.setBackgroundColor(
        if (isError)
            ContextCompat.getColor(this.context, R.color.brand_red) else
            ContextCompat.getColor(this.context, R.color.brand_green_success)
    )


    val actionBarHeight = this.context.getActionBarSize()

    val view = this.rootView as ViewGroup
    val params = ConstraintLayout.LayoutParams(
        ConstraintLayout.LayoutParams.MATCH_PARENT,
        actionBarHeight
    )
    binding.root.layoutParams = params

    val moveDown =
        ObjectAnimator.ofFloat(
            binding.snackCard,
            View.TRANSLATION_Y,
            -actionBarHeight.toFloat(),
            0f
        )
    moveDown.interpolator = AccelerateInterpolator(1f)
    moveDown.duration = 300
    moveDown.start()
    view.addView(binding.root)

    val fade = ObjectAnimator.ofFloat(binding.snackCard, View.ALPHA, 1f, 1f)
    val set = AnimatorSet()
    set.duration = 6000L
    set.playTogether(fade)
    set.start()

    set.doOnEnd {
        val moveUp =
            ObjectAnimator.ofFloat(
                binding.snackCard,
                View.TRANSLATION_Y,
                0f,
                -actionBarHeight.toFloat()
            )
        moveUp.duration = 300
        moveUp.start()
        moveUp.doOnEnd {
            statusBarIconColor(activity, Color.WHITE)
            view.removeView(binding.root)
        }
    }
}

enum class TipoMensagem {
    NORMAL,
    ERROR,
    SUCCESS
}

fun Snackbar.tipoMensagem(tipoMensagem: TipoMensagem): Snackbar {
    this.setTextColor(this.context.getColorFromAttr(com.google.android.material.R.attr.colorOnPrimary))
    this.setBackgroundTint(
        when (tipoMensagem) {
                TipoMensagem.NORMAL-> this.context.getColorFromAttr(com.google.android.material.R.attr.colorOnSurface)
                TipoMensagem.ERROR-> this.context.getColor(R.color.brand_red)
                TipoMensagem.SUCCESS -> this.context.getColor(R.color.brand_green_success)
        }
    )
    return this
}

fun View.showMessage(message: String, tipoMensagem: TipoMensagem) {
    Snackbar
        .make(this, message, Snackbar.LENGTH_LONG)
        .tipoMensagem(tipoMensagem)
        .show()

}