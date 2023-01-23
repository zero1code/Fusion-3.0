package br.com.fusiondms.modentrega.domain.entregasusecase

import br.com.fusiondms.modmodel.Conteudo
import br.com.fusiondms.modmodel.entrega.Entrega
import br.com.fusiondms.modmodel.Resource
import kotlinx.coroutines.flow.Flow

interface EntregasUseCase {
    suspend fun getListaEntrega(idRomaneio: Int) : Flow<List<Conteudo>>
    suspend fun updateStatusEntrega(entrega: Entrega, idEvento: Int) : Flow<Resource<Int>>
}