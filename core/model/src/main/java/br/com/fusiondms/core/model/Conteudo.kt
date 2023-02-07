package br.com.fusiondms.modmodel

import br.com.fusiondms.modmodel.entrega.EntregasPorCliente

sealed class Conteudo(val id: Int) {
    class CarouselEntrega(id: Int, val cliente: EntregasPorCliente) : Conteudo(id)
}
