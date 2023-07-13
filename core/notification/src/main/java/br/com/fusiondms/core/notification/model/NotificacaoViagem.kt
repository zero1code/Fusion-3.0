package br.com.fusiondms.core.notification.model

data class NotificacaoViagem(
    val progresso: Int,
    val distanciaRestanteDoLocalAtual: Float,
    val velocidade: Int,
    val horarioAproximadoChegada: String
) {
}