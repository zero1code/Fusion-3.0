package br.com.fusiondms.modmodel

data class Entrega(
    var idEntrega: Int = 0,
    var ordermEntrega: Int = 0,
    var dadosCliente: String = "",
    var localCliente: String = "",
    var numeroNotaFiscal: Int = 0,
    var valor: String = "",
    var statusEntrega: String = ""
) {

}
