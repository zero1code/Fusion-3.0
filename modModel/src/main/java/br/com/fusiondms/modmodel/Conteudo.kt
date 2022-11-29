package br.com.fusiondms.modmodel

sealed class Conteudo(val id: Int) {
    class CarouselEntrega(id: Int, val cliente: EntregasPorCliente) : Conteudo(id)
}
