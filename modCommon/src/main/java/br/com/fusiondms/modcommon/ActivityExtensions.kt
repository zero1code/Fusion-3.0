package br.com.fusiondms.modcommon

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.core.view.WindowInsetsControllerCompat


fun Activity.setStatusBarColor(color:Int){
    statusBarIconColor(this, color)
    val colorFrom = window.statusBarColor
    val colorAnimator = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, color)
    colorAnimator.duration = 250
    colorAnimator.addUpdateListener { animator ->
        window?.statusBarColor = animator.animatedValue as Int
    }
    colorAnimator.start()
}

fun Activity.setDefaultStatusBarColor() {
    val color = this.getColor(R.color.brand_background_color)
    setStatusBarColor(color)
}

fun Activity.setTransparentStatusBar() {
    window.statusBarColor = Color.TRANSPARENT
    statusBarIconColor(this, Color.WHITE)
}

fun Activity.setDefaultStatusBar() {
    window.apply {
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        setDefaultStatusBarColor()
    }
    statusBarIconColor(this, Color.WHITE)
}

fun Activity.removerNotchInLandscape() {
    window.apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER
        }
    }
}

fun Activity.getOrientacaoTela() {
    resources.apply {
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            removerNotchInLandscape()
        }
    }
}

fun Activity.getStatusbarHeight() : Int{
    val statusBarHeightId = resources.getIdentifier("status_bar_height", "dimen", "android")
    val statusBarHeight = resources.getDimensionPixelSize(statusBarHeightId)
    val result = if (statusBarHeight > 0) statusBarHeight else 100
    return result / resources.displayMetrics.density.toInt()
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun statusBarIconColor(activity: Activity, color: Int) {
    val decorView: View = activity.window.decorView
    val wic = WindowInsetsControllerCompat(activity.window, decorView)
    wic.isAppearanceLightStatusBars = !isColorDark(color)
}

fun isColorDark(color: Int) : Boolean{
    val darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
    return darkness >= 0.5
}