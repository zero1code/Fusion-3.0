package br.com.fusiondms.core.model.recebimento

data class TipoPagamento(
    val id: Int,
    val descricao: String,
    val tipoFusion: String,
    val tipoErp: String,
    val solicitaPagamento: Boolean,
    val obrigatorioErp: Boolean,
    val permitirMudarTipoPagamento: Boolean,
    val formasPagamento: String,
    val bandeiraCartao: String,
    val qtdParcelaCartao: Int,
    val pixKey: String,
    val pixNome: String,
    val pixDescricao: String,
    val pixCidade: String,
)
