package br.com.fusiondms.moddatabase.repository.entregas

import br.com.fusiondms.modmodel.Entrega
import br.com.fusiondms.modmodel.Resource
import kotlinx.coroutines.flow.Flow

interface EntregasRepository {
    suspend fun getListaEntrega(idRomaneio: Int) : Flow<List<Entrega>>
    suspend fun updateStatusEntrega(entrega: Entrega) : Flow<Resource<Int>>
}