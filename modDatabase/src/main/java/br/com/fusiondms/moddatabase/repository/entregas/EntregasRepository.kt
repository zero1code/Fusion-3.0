package br.com.fusiondms.moddatabase.repository

import br.com.fusiondms.modmodel.Entrega

interface EntregasRepository {
    suspend fun getListaEntrega() : List<Entrega>
}