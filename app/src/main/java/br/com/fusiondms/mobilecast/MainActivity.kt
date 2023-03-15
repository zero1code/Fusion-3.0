package br.com.fusiondms.mobilecast

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewTreeObserver
import android.view.animation.AnticipateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import br.com.fusiondms.core.common.R
import br.com.fusiondms.core.common.statusBarIconColor
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var splashScreen: SplashScreen

    override fun onCreate(savedInstanceState: Bundle?) {

        splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        setContentView(br.com.fusiondms.mobilecast.R.layout.activity_main)

        window.apply {
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            statusBarColor = Color.TRANSPARENT
        }
        usarAnimacaoDeSaidaCustomizada()
        statusBarIconColor(this, Color.WHITE)
    }

    private fun usarAnimacaoDeSaidaCustomizada() {
        splashScreen.setOnExitAnimationListener { splashScreenViewProvider ->
            val splashScreenView = splashScreenViewProvider.view
            val scX = splashScreenView.width / 2
            val scY = splashScreenView.height / 2

            val finalRadius = Math.hypot(scX.toDouble(), scY.toDouble()).toFloat()

            val mover = ObjectAnimator.ofFloat(splashScreenView, View.TRANSLATION_Y, 0f, splashScreenView.height.toFloat())
            mover.interpolator = AnticipateInterpolator()
            val reveal = ViewAnimationUtils.createCircularReveal(splashScreenView, scX, scY, finalRadius, finalRadius / 5)
            reveal.interpolator = DecelerateInterpolator(2f)

            val set = AnimatorSet()
            set.duration = 700L
            set.playTogether(reveal, mover)
            set.doOnEnd {
                splashScreenViewProvider.remove()
            }
            set.start()
        }
    }

    private fun manterSplashScreenIndefinitamente() {
        splashScreen.setKeepOnScreenCondition { true }
    }

    private fun manterSplashScreenPor5Segundos() {
//        binding.root.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
//            override fun onPreDraw(): Boolean {
//                Thread.sleep(5000)
//                binding.root.viewTreeObserver.removeOnPreDrawListener(this)
//                return true
//            }
//        })
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}