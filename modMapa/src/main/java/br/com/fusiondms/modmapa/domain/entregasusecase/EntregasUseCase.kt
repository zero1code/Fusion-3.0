package br.com.fusiondms.modmapa.domain

import br.com.fusiondms.modmodel.Entrega

interface EntregasUseCase {
    suspend fun getListaEntrega() : List<Entrega>
}