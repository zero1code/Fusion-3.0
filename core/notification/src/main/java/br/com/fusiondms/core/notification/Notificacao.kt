package br.com.fusiondms.core.notification

import android.content.Context
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import br.com.fusiondms.core.common.R
import br.com.fusiondms.core.notification.R.*

class Notificacao {
    companion object {
        private const val NOTIFICACAO_DEFAULT = 101

        fun distanciaProximoCliente(context: Context, progresso: Int = 1, situacao: EnumNotificacao) {
            val notificacaoLayout = RemoteViews(context.packageName, layout.layout_notificacao_proximo_cliente)
            notificacaoLayout.setProgressBar(id.pb_proximo_cliente, 100, progresso, false)
            when(progresso) { //Para smartphones apenas
                in 0..9 -> notificacaoLayout.setImageViewResource(id.iv_progresso_caminhao_1, drawable.ic_caminhao)
                in 10..19 -> notificacaoLayout.setImageViewResource(id.iv_progresso_caminhao_2, drawable.ic_caminhao)
                in 20..29 -> notificacaoLayout.setImageViewResource(id.iv_progresso_caminhao_3, drawable.ic_caminhao)
                in 30..39 -> notificacaoLayout.setImageViewResource(id.iv_progresso_caminhao_4, drawable.ic_caminhao)
                in 40..49 -> notificacaoLayout.setImageViewResource(id.iv_progresso_caminhao_5, drawable.ic_caminhao)
                in 50..59 -> notificacaoLayout.setImageViewResource(id.iv_progresso_caminhao_6, drawable.ic_caminhao)
                in 60..69 -> notificacaoLayout.setImageViewResource(id.iv_progresso_caminhao_7, drawable.ic_caminhao)
                in 70..79 -> notificacaoLayout.setImageViewResource(id.iv_progresso_caminhao_8, drawable.ic_caminhao)
                in 80..89 -> notificacaoLayout.setImageViewResource(id.iv_progresso_caminhao_9, drawable.ic_caminhao)
                in 90..97 -> notificacaoLayout.setImageViewResource(id.iv_progresso_caminhao_10, drawable.ic_caminhao)
                in 98..100 -> notificacaoLayout.setImageViewResource(id.iv_progresso_caminhao_11, drawable.ic_caminhao)
                else -> Unit
            }


            val mNotificationUtils = NotificacaoUltis(context, EnumTipoChannel.CHANNEL_LOCALIZACAO_PROXIMO_CLIENTE)

            val nb = NotificationCompat.Builder(context,
                NotificacaoUltis.LOCALIZACAO_PROXIMO_CLIENTE_CHANNEL_ID
            )
                .setSmallIcon(R.drawable.ic_notificacao_app)
                .setAutoCancel(false)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContent(notificacaoLayout)

            when (situacao) {
                EnumNotificacao.LOCAL_PROXIMO_CLIENTE -> {

                }
                EnumNotificacao.UPDATE_LOCAL_PROXIMO_CLIENTE -> {
                    nb.setContentText("")
                    nb.setOngoing(true)
                }
            }

            mNotificationUtils.getManager().notify(NOTIFICACAO_DEFAULT, nb.build())
        }
    }
}