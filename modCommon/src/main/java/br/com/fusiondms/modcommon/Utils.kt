package br.com.fusiondms.modcommon

import android.content.Context
import android.util.TypedValue

fun Context.getActionBarSize(): Int {
    val tv = TypedValue()
    return if (this.theme.resolveAttribute(android.R.attr.actionBarSize, tv, true))
        TypedValue.complexToDimensionPixelSize(tv.data, this.resources.displayMetrics)
    else 0
}