package br.com.fusiondms.core.model

import br.com.fusiondms.core.model.entrega.EntregasPorCliente

sealed class Conteudo(val id: Int) {
    data class CarouselEntrega(val idCliente: Int, val entregasPorCliente: EntregasPorCliente) : Conteudo(idCliente) {
        fun copy(
            entregasPorCliente: EntregasPorCliente = this.entregasPorCliente.copy()
        ) = CarouselEntrega(idCliente, entregasPorCliente)
    }
}
