package br.com.fusiondms.feature.entregas.domain.entregasusecase

import br.com.fusiondms.core.model.Conteudo
import br.com.fusiondms.core.model.entrega.Entrega
import br.com.fusiondms.core.model.parametros.Parametros
import kotlinx.coroutines.flow.Flow

interface EntregasUseCase {
    suspend fun getListaEntrega(idRomaneio: Int) : Flow<List<Conteudo>>
    suspend fun updateStatusEntrega(entrega: Entrega, idEvento: Int, parametros: Parametros) : Flow<Int>
}