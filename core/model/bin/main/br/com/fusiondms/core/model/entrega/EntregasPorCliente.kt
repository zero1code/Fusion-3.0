package br.com.fusiondms.core.model.entrega

data class EntregasPorCliente(
    var idRomaneio: Int,
    var idCliente: Int,
    var cliente: String,
    var local: String,
    val latitude: Double,
    val longitude: Double,
    var entregas: List<Entrega>
) {
}