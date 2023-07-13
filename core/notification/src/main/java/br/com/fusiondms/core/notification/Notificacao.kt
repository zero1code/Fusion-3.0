package br.com.fusiondms.core.notification

import android.content.Context
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import br.com.fusiondms.core.common.R
import br.com.fusiondms.core.notification.R.*
import br.com.fusiondms.core.notification.model.NotificacaoViagem

class Notificacao {
    companion object {
        private const val NOTIFICACAO_PROXIMO_CLIENTE = 100

        fun distanciaProximoCliente(context: Context, notificacaoViagem: NotificacaoViagem, situacao: EnumNotificacao) {
            val notificacaoLayout = RemoteViews(context.packageName, layout.layout_notificacao_proximo_cliente)
            val notificacaoLayoutExpandido = RemoteViews(context.packageName, layout.layout_notificacao_proximo_cliente)

            notificacaoLayout.setTextViewText(id.tv_titulo, "Chegada: ${notificacaoViagem.horarioAproximadoChegada}")
            notificacaoLayoutExpandido.setTextViewText(id.tv_titulo, "Chegada: ${notificacaoViagem.horarioAproximadoChegada}")

            when (val progresso = notificacaoViagem.progresso) {
                0 -> {
                    notificacaoLayout.setImageViewResource(
                        id.iv_progresso_caminhao_1,
                        drawable.ic_caminhao
                    )
                    notificacaoLayout.setProgressBar(id.pb_proximo_cliente, 100, progresso, false)

                    notificacaoLayoutExpandido.setImageViewResource(
                        id.iv_progresso_caminhao_1,
                        drawable.ic_caminhao
                    )
                    notificacaoLayoutExpandido.setProgressBar(id.pb_proximo_cliente, 100, progresso, false)
                }
                13 -> {
                    notificacaoLayout.setImageViewResource(
                        id.iv_progresso_caminhao_2,
                        drawable.ic_caminhao
                    )
                    notificacaoLayout.setProgressBar(id.pb_proximo_cliente, 100, progresso, false)

                    notificacaoLayoutExpandido.setImageViewResource(
                        id.iv_progresso_caminhao_2,
                        drawable.ic_caminhao
                    )
                    notificacaoLayoutExpandido.setProgressBar(id.pb_proximo_cliente, 100, progresso, false)
                }
                25 -> {
                    notificacaoLayout.setImageViewResource(
                        id.iv_progresso_caminhao_3,
                        drawable.ic_caminhao
                    )
                    notificacaoLayout.setProgressBar(id.pb_proximo_cliente, 100, progresso, false)

                    notificacaoLayoutExpandido.setImageViewResource(
                        id.iv_progresso_caminhao_3,
                        drawable.ic_caminhao
                    )
                    notificacaoLayoutExpandido.setProgressBar(id.pb_proximo_cliente, 100, progresso, false)
                }
                38 -> {
                    notificacaoLayout.setImageViewResource(
                        id.iv_progresso_caminhao_4,
                        drawable.ic_caminhao
                    )
                    notificacaoLayout.setProgressBar(id.pb_proximo_cliente, 100, progresso, false)

                    notificacaoLayoutExpandido.setImageViewResource(
                        id.iv_progresso_caminhao_4,
                        drawable.ic_caminhao
                    )
                    notificacaoLayoutExpandido.setProgressBar(id.pb_proximo_cliente, 100, progresso, false)
                }
                50 -> {
                    notificacaoLayout.setImageViewResource(
                        id.iv_progresso_caminhao_5,
                        drawable.ic_caminhao
                    )
                    notificacaoLayout.setProgressBar(id.pb_proximo_cliente, 100, progresso, false)

                    notificacaoLayoutExpandido.setImageViewResource(
                        id.iv_progresso_caminhao_5,
                        drawable.ic_caminhao
                    )
                    notificacaoLayoutExpandido.setProgressBar(id.pb_proximo_cliente, 100, progresso, false)
                }
                63 -> {
                    notificacaoLayout.setImageViewResource(
                        id.iv_progresso_caminhao_6,
                        drawable.ic_caminhao
                    )
                    notificacaoLayout.setProgressBar(id.pb_proximo_cliente, 100, progresso, false)

                    notificacaoLayoutExpandido.setImageViewResource(
                        id.iv_progresso_caminhao_6,
                        drawable.ic_caminhao
                    )
                    notificacaoLayoutExpandido.setProgressBar(id.pb_proximo_cliente, 100, progresso, false)
                }
                76 -> {
                    notificacaoLayout.setImageViewResource(
                        id.iv_progresso_caminhao_7,
                        drawable.ic_caminhao
                    )
                    notificacaoLayout.setProgressBar(id.pb_proximo_cliente, 100, progresso, false)

                    notificacaoLayoutExpandido.setImageViewResource(
                        id.iv_progresso_caminhao_7,
                        drawable.ic_caminhao
                    )
                    notificacaoLayoutExpandido.setProgressBar(id.pb_proximo_cliente, 100, progresso, false)
                }
                88 -> {
                    notificacaoLayout.setImageViewResource(
                        id.iv_progresso_caminhao_8,
                        drawable.ic_caminhao
                    )
                    notificacaoLayout.setProgressBar(id.pb_proximo_cliente, 100, progresso, false)

                    notificacaoLayoutExpandido.setImageViewResource(
                        id.iv_progresso_caminhao_8,
                        drawable.ic_caminhao
                    )
                    notificacaoLayoutExpandido.setProgressBar(id.pb_proximo_cliente, 100, progresso, false)
                }
                100 -> {
                    notificacaoLayout.setImageViewResource(
                        id.iv_progresso_caminhao_9,
                        drawable.ic_caminhao
                    )
                    notificacaoLayout.setProgressBar(id.pb_proximo_cliente, 100, progresso, false)

                    notificacaoLayoutExpandido.setImageViewResource(
                        id.iv_progresso_caminhao_9,
                        drawable.ic_caminhao
                    )
                    notificacaoLayoutExpandido.setProgressBar(id.pb_proximo_cliente, 100, progresso, false)
                }
                else -> Unit
            }


            val mNotificationUtils = NotificacaoUltis(context, EnumTipoChannel.CHANNEL_LOCALIZACAO_PROXIMO_CLIENTE)

            val nb = NotificationCompat.Builder(context,
                NotificacaoUltis.LOCALIZACAO_PROXIMO_CLIENTE_CHANNEL_ID
            )
                .setSmallIcon(R.drawable.ic_notificacao_app)
                .setAutoCancel(false)
                .setOnlyAlertOnce(true)
                .setShowWhen(false)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCustomContentView(notificacaoLayout)
                .setCustomBigContentView(notificacaoLayoutExpandido)

            when (situacao) {
                EnumNotificacao.LOCAL_PROXIMO_CLIENTE -> {

                }
                EnumNotificacao.UPDATE_LOCAL_PROXIMO_CLIENTE -> {
                    nb.setOngoing(true)
                }
            }

            mNotificationUtils.getManager().notify(NOTIFICACAO_PROXIMO_CLIENTE, nb.build())
        }
    }
}