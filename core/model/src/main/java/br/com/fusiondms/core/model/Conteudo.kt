package br.com.fusiondms.core.model

import br.com.fusiondms.core.model.entrega.EntregasPorCliente

sealed class Conteudo(val id: Int) {
    data class CarouselEntrega(val idCliente: Int, val cliente: EntregasPorCliente) : Conteudo(idCliente)
}
