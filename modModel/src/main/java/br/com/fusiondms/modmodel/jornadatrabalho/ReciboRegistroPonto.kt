package br.com.fusiondms.modmodel

data class ReciboRegistroPonto(
    val id: Int,
    val matricula: Int,
    val dataRegistro: Long,
    val registroEfetuado: Boolean,
    val nome: String
)