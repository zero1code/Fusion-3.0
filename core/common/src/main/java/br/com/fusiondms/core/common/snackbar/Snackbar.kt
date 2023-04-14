package br.com.fusiondms.core.common.snackbar

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
import br.com.fusiondms.core.common.R
import br.com.fusiondms.core.common.databinding.SnackLayoutBinding
import br.com.fusiondms.core.common.getActionBarSize
import br.com.fusiondms.core.common.getColorFromAttr
import br.com.fusiondms.core.common.statusBarIconColor
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.internal.managers.FragmentComponentManager
import java.util.LinkedList
import java.util.concurrent.TimeUnit


data class Mensagem(val message: String, val isError: Boolean, val status: Int)

private val listaMensagem = LinkedList<Mensagem>()
fun View.mensagemCurta(message: String, isError: Boolean = false, status: Int = 0) {

    listaMensagem.add(Mensagem(message, isError, status))

    if (listaMensagem.isEmpty()) return

    if (listaMensagem.size == 1) {
        exibirMensagem(this)
    }
}

private fun exibirMensagem(view: View) {
    val mensagem = listaMensagem.peek()

    mensagem?.run {
        val activity = FragmentComponentManager.findActivity(view.context) as Activity
        statusBarIconColor(activity, Color.BLACK)
        val inflater = LayoutInflater.from(view.context)
        val binding = SnackLayoutBinding.inflate(inflater)
        binding.snackMensagem.text = message
        binding.snackCard.setBackgroundColor(
            if (isError) ContextCompat.getColor(view.context, R.color.brand_red)
            else {
                when (status) {
                    2 -> ContextCompat.getColor(view.context, R.color.brand_gomboje_orange)
                    3 -> ContextCompat.getColor(view.context, R.color.brand_celtic_blue)
                    4 -> ContextCompat.getColor(view.context, R.color.brand_green_success)
                    5, 6, 7 -> ContextCompat.getColor(view.context, R.color.brand_red)
                    8 -> ContextCompat.getColor(view.context, R.color.brand_selective_yellow)
                    else -> ContextCompat.getColor(view.context, R.color.brand_green_success)
                }
            }
        )

        val actionBarHeight = view.context.getActionBarSize()

        val mView = view.rootView as ViewGroup
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
        mView.addView(binding.root)

        val fade = ObjectAnimator.ofFloat(binding.snackCard, View.ALPHA, 1f, 1f)
        val set = AnimatorSet()
        set.duration = TimeUnit.SECONDS.toMillis(3)
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
                mView.removeView(binding.root)
                listaMensagem.remove()
                exibirMensagem(view)
            }
        }
    }
}

enum class TipoMensagem {
    NORMAL,
    ERROR,
    SUCCESS
}

fun View.showMessage(message: String, tipoMensagem: TipoMensagem, possuiAncora: Boolean = false) {
    Snackbar
        .make(this, message, Snackbar.LENGTH_LONG)
        .tipoMensagem(tipoMensagem)
        .setAnchorView(if (possuiAncora) this else null)
        .show()

}

private fun Snackbar.tipoMensagem(tipoMensagem: TipoMensagem): Snackbar {
    this.setTextColor(this.context.getColorFromAttr(com.google.android.material.R.attr.colorOnPrimary))
    this.setBackgroundTint(
        when (tipoMensagem) {
            TipoMensagem.NORMAL -> this.context.getColorFromAttr(com.google.android.material.R.attr.colorOnSurface)
            TipoMensagem.ERROR -> this.context.getColor(R.color.brand_red)
            TipoMensagem.SUCCESS -> this.context.getColor(R.color.brand_green_success)
        }
    )
    return this
}