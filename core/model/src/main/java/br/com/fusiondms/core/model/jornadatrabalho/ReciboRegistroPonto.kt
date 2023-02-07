package br.com.fusiondms.core.model.jornadatrabalho

data class ReciboRegistroPonto(
    val id: Int,
    val matricula: Int,
    val dataRegistro: Long,
    val registroEfetuado: Boolean,
    val nome: String
)
