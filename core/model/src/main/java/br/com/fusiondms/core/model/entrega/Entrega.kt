package br.com.fusiondms.core.model.entrega

import java.math.BigDecimal

data class Entrega(
    val id: Int,
    val idEntrega: Int,
    val idRomaneio: Int,
    val idCliente: Int,
    val dadosCliente: String,
    val localCliente: String,
    var numeroNotaFiscal: String,
    val valor: BigDecimal,
    val statusEntrega: String,
    val formaPagamento: String
)
