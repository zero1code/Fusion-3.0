package br.com.fusiondms.core.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi

class NotificacaoUltis(context: Context?, tipoChannel: EnumTipoChannel) : ContextWrapper(context) {
    init {
        createChannels(tipoChannel)
    }

    fun getManager() : NotificationManager {
        return getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private fun createChannels(tipoChannel: EnumTipoChannel) {
        val notificationChannel: NotificationChannel? = when (tipoChannel) {

            EnumTipoChannel.CHANNEL_LOCALIZACAO_PROXIMO_CLIENTE -> {
                NotificationChannel(
                    LOCALIZACAO_PROXIMO_CLIENTE_CHANNEL_ID,
                    LOCALIZACAO_PROXIMO_CLIENTE_CHANNEL_NAME,
                    IMPORTANCE_HIGH
                )
            }
            else -> null
        }

        notificationChannel?.let {
            it.enableLights(true)
//            it.enableVibration(true)
//            it.vibrationPattern = longArrayOf(100, 50, 50, 300)
//            it.shouldVibrate()
            it.lightColor = Color.BLUE
            it.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            getManager().createNotificationChannel(it)
        }
    }

    companion object {
        const val LOCALIZACAO_PROXIMO_CLIENTE_CHANNEL_ID = "localizacao_proximo_cliente"
        const val LOCALIZACAO_PROXIMO_CLIENTE_CHANNEL_NAME = "Localizacao proximo cliente"
    }
}