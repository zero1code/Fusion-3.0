package br.com.fusiondms.moddatabase.repository.entregas

import br.com.fusiondms.modmodel.Entrega

interface EntregasRepository {
    suspend fun getListaEntrega() : List<Entrega>
}