package br.com.fusiondms.core.model.parametros

data class Parametros(
    val canhotoObrigatorio: Boolean = false,
    val canhotoAprovacao: Boolean = false,
    val canhotoQualidade: Boolean = false,
    val canhotoCorte: Boolean = false,
    val canhotoExigirEmColeta: Boolean = false,
    val canhotoExcluir: Boolean = false,
    val entregasPorCliente: Boolean = false,
    val entregasAdiarTodas: Boolean = false,
    val entregasIndicarColeta: Boolean = false,
    val entregasSolicitarColeta: Boolean = false,
    val entregasEventoClientes: Boolean = false,
    val entregasTempoEspera: String = "4",
    val localizacaoTempoEnvio: Int = 20
) {
    fun isEntregasPorCliente() = entregasPorCliente
}
