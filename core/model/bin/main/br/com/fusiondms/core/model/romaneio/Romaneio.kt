package br.com.fusiondms.core.model.romaneio

data class Romaneio(
    var id: Int = 0,
    var idRomaneio: Int = 0,
    var destino: String = "",
    var qtdEntregas: Int = 0,
    var status: String = "",
    var kmTotal: Int = 0,
    var corIdentificador: Int = 0
) {

}
