package br.com.fusiondms.modmodel

data class Sincronizacao(
    val listaRomaneio: List<Romaneio>,
    val listaEntrega: List<Entrega>
)