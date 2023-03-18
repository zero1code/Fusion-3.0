package br.com.fusiondms.core.model.entrega

import java.math.BigDecimal

data class EntregaItem(
    val idCarga: Int,
    val idRomaneio: Int,
    val codigoMercadoria: String,
    val descricao: String,
    val quantidade: Double,
    val preco: BigDecimal,
    val unidade: String,
    val valorST: BigDecimal,
    val subTotal: BigDecimal,
    val peso: Double,
    val motivoDevolucao: String,
    val quantidadeDevolucao: Double,
    val entregue: Boolean
)
