package br.com.fusiondms.feature.mapa.views

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import br.com.fusiondms.core.common.R
import br.com.fusiondms.core.common.getColorFromAttr
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class FabEsperaButton(
    context: Context,
    attributeSet: AttributeSet? = null
) : ExtendedFloatingActionButton(context, attributeSet) {

    fun isActive(isActive: Boolean) {
        if (isActive) {
            backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.brand_green_success, null))
            icon = ContextCompat.getDrawable(context, R.drawable.ic_timer_on)
            iconTint = ColorStateList.valueOf(context.getColorFromAttr(com.google.android.material.R.attr.colorOnPrimary))
            setTextColor(context.getColorFromAttr(com.google.android.material.R.attr.colorOnPrimary))
        } else {
            backgroundTintList = ColorStateList.valueOf(context.getColorFromAttr(com.google.android.material.R.attr.colorOnPrimary))
            icon = ContextCompat.getDrawable(context, R.drawable.ic_timer_off)
            iconTint = ColorStateList.valueOf(context.getColorFromAttr(com.google.android.material.R.attr.colorOnSurface))
            setTextColor(context.getColorFromAttr(com.google.android.material.R.attr.colorOnSurface))
        }
        invalidate()
    }
}