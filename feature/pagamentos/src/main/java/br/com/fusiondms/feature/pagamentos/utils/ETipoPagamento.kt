package br.com.fusiondms.feature.pagamentos.utils

import br.com.fusiondms.core.common.R

enum class ETipoPagamento(val descricao: String, val icone: Int) {
    DN("Dinheiro", R.drawable.ic_dinheiro),
    CH("Cheque", R.drawable.ic_dinheiro),
    C3("Cheque Terceiros", R.drawable.ic_dinheiro),
    BO("Boleto", R.drawable.ic_boleto),
    PR("Promissória", R.drawable.ic_dinheiro),
    DP("Depósito Bancário", R.drawable.ic_dinheiro),
    CART("Cartão", R.drawable.ic_cartao_credito_debito),
    NC("Nota de Crédito", R.drawable.ic_boleto),
    CC("Cartão de Crédito", R.drawable.ic_cartao_credito_debito),
    CD("Cartão de Débito", R.drawable.ic_cartao_credito_debito),
    CVOU("Cartão Voucher", R.drawable.ic_cartao_credito_debito),
    BNF("Bonificação", R.drawable.ic_dinheiro),
    PIX("Pix", R.drawable.ic_pix),
    OUT("Outros", R.drawable.ic_dinheiro),

}

fun String.toPagamentoFusion(): ETipoPagamento = enumValueOf(this)
fun String.getTipoErp(): String {
    var tipoErpEncontrado = ""
    for (tipoErp in ETipoPagamento.values()) {
        if (tipoErp.descricao == this) {
            tipoErpEncontrado = tipoErp.toString()
            break
        }
    }
    return tipoErpEncontrado
}