package br.com.fusiondms.modmodel

data class RegistroPonto(
    val id: Int,
    val matricula: Int,
    val dataRegistro: Long,
    val registroEfetuado: Boolean
)
