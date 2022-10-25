package br.com.fusiondms.modcommon

import android.content.Context
import android.util.TypedValue

fun getActionBarSize(context: Context): Int {
    val tv = TypedValue()
    return if (context.theme.resolveAttribute(android.R.attr.actionBarSize, tv, true))
        TypedValue.complexToDimensionPixelSize(tv.data, context.resources.displayMetrics)
    else 0
}