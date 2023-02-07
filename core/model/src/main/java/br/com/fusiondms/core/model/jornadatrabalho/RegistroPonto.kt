package br.com.fusiondms.core.model.jornadatrabalho

data class RegistroPonto(
    val id: Int,
    val matricula: Int,
    val dataRegistro: Long,
    val registroEfetuado: Boolean
)
