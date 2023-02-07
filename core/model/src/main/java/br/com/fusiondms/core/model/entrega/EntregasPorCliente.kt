package br.com.fusiondms.core.model.entrega

data class EntregasPorCliente(
    var idRomaneio: Int,
    var idCliente: Int,
    var cliente: String,
    var local: String,
    var entregas: List<Entrega>
) {
}