package br.com.fusiondms.core.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.DimenRes
import androidx.core.content.ContextCompat
import java.io.Serializable
import java.math.BigDecimal
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

fun Context.getActionBarSize(): Int {
    val tv = TypedValue()
    return if (this.theme.resolveAttribute(android.R.attr.actionBarSize, tv, true))
        TypedValue.complexToDimensionPixelSize(tv.data, this.resources.displayMetrics)
    else 0
}

@ColorInt
fun Context.getColorFromAttr(
    @AttrRes attrColor: Int,
    typedValue: TypedValue = TypedValue(),
    resolveRefs: Boolean = true
): Int {
    theme.resolveAttribute(attrColor, typedValue, resolveRefs)
    return typedValue.data
}

fun Context.px(@DimenRes dimen: Int): Int = resources.getDimension(dimen).toInt()

fun Context.dp(@DimenRes dimen: Int): Float = px(dimen) / resources.displayMetrics.density

fun Context.hideKeyboard(view: View) {
    val inputManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun ImageView.mudarCorIcone(context: Context, color: Int) {
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        this.colorFilter = BlendModeColorFilter(ContextCompat.getColor(context, color), BlendMode.SRC_ATOP)
    } else {
        this.setColorFilter(ContextCompat.getColor(context, color), PorterDuff.Mode.SRC_IN)
    }
}

fun BigDecimal.toMoedaLocal(): String {
    return NumberFormat
        .getCurrencyInstance(Locale.getDefault())
        .format(this)
}

fun converterDataParaDiaMesAnoHoraMinuto(data: Long) : String {
    val timestamp = Math.multiplyExact(data, 1000)
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
//    sdf.timeZone = TimeZone.getTimeZone("UTC")
    val netDate = Date(timestamp)
    val date = sdf.format(netDate)
    return date

}

fun converterDataParaDiaMesAno(data: Long) : String {
    val timestamp = Math.multiplyExact(data, 1000)
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
//    sdf.timeZone = TimeZone.getTimeZone("UTC")
    val netDate = Date(timestamp)
    val date = sdf.format(netDate)
    return date

}

fun converterDataParaHorasMinutos(dataAtual: Long): String {
    val timestamp = Math.multiplyExact(dataAtual, 1000)
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
//    sdf.timeZone = TimeZone.getTimeZone("UTC")
    val netDate = Date(timestamp)
    val date = sdf.format(netDate)
    return date.toString()
}

fun converterDataParatextoLegivel(dataAtual: Long): String {
    val timestamp = Math.multiplyExact(dataAtual, 1000)
    val sdf = SimpleDateFormat("E, dd MMM HH:mm", Locale.getDefault())
//    sdf.timeZone = TimeZone.getTimeZone("UTC")
    val netDate = Date(timestamp)
    val date = sdf.format(netDate).replace(".", "")
    return date.toString()
}

fun <T : Serializable?> getSerializable(activity: Activity, name: String, clazz: Class<T>): T? {
    return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        activity.intent.getSerializableExtra(name, clazz)
    else
        activity.intent.getSerializableExtra(name) as T
}

fun <T : Serializable?> getSerializable(intent: Intent?, name: String, clazz: Class<T>): T? {
    return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        intent?.getSerializableExtra(name, clazz)
    else
        intent?.getSerializableExtra(name) as T
}
