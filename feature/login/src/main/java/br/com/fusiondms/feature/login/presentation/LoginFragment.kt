package br.com.fusiondms.feature.login.presentation

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import br.com.fusiondms.core.common.statusBarIconColor
import br.com.fusiondms.feature.login.R

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            installSplashScreen()
        } else {
            theme.applyStyle(br.com.fusiondms.core.common.R.style.Theme_Fusion30, true)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

//        window.apply {
//            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//            statusBarColor = Color.TRANSPARENT
//        }
        statusBarIconColor(this, Color.WHITE)
    }
}