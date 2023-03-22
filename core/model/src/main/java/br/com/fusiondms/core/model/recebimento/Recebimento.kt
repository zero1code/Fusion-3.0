package br.com.fusiondms.core.model.recebimento

import java.math.BigDecimal

data class Recebimento(
    val id: Int,
    val idEntrega: Int,
    val idRomaneio: Int,
    val valor: BigDecimal,
    val tipo: String,
    val banco: String,
    val agencia: String,
    val conta: String,
    val numCh: String,
    val dataCh: String,
    val codigoTransacao: String,
    val formaPagamento: String,
    val parcelas: Int,
    val bandeira: String,
    val nsu: String,
    val txid: String,
    val dataRecebimento: Long
)
