package br.com.fusiondms.core.model.recebimento

import java.math.BigDecimal

data class Recebimento(
    var id: Int = 0,
    var idEntrega: Int = 0,
    var idRomaneio: Int = 0,
    var valor: BigDecimal = BigDecimal.ZERO,
    var tipo: String = "",
    var banco: String = "",
    var agencia: String = "",
    var conta: String = "",
    var numCh: String = "",
    var dataCh: String = "",
    var codigoTransacao: String = "",
    var formaPagamento: String = "",
    var parcelas: Int = 0,
    var bandeira: String = "",
    var nsu: String = "",
    var txid: String = "",
    var dataRecebimento: Long = 0
) : java.io.Serializable
