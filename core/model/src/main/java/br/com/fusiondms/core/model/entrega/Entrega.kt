package br.com.fusiondms.core.model.entrega

data class Entrega(
    var id: Int = 0,
    var idEntrega: Int = 0,
    var idRomaneio: Int = 0,
    var idCliente: Int = 0,
    var dadosCliente: String = "",
    var localCliente: String = "",
    var numeroNotaFiscal: Int = 0,
    var valor: String = "",
    var statusEntrega: String = ""
) {

}
