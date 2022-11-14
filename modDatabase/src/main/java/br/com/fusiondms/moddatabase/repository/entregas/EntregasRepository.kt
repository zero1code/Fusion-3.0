package br.com.fusiondms.moddatabase.repository.entregas

import br.com.fusiondms.modmodel.Entrega
import kotlinx.coroutines.flow.Flow

interface EntregasRepository {
    suspend fun getListaEntrega(idRomaneio: Int) : Flow<List<Entrega>>
}