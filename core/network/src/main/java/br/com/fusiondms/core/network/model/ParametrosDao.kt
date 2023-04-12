package br.com.fusiondms.core.network.model

data class ParametrosDao(
    val canhotoObrigatorio: Boolean,
    val canhotoAprovacao: Boolean,
    val canhotoQualidade: Boolean,
    val canhotoCorte: Boolean,
    val canhotoExigirEmColeta: Boolean,
    val canhotoExcluir: Boolean,
    val entregasPorCliente: Boolean,
    val entregasAdiarTodas: Boolean,
    val entregasIndicarColeta: Boolean,
    val entregasSolicitarColeta: Boolean,
    val entregasEventoClientes: Boolean,
    val entregasTempoEspera: String,
    val localizacaoTempoEnvio: Int
)
