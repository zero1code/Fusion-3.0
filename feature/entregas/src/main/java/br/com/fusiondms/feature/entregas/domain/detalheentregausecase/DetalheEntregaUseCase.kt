package br.com.fusiondms.feature.entregas.domain.detalheentregausecase

import br.com.fusiondms.core.model.Conteudo
import br.com.fusiondms.core.model.entrega.DetalheEntrega
import kotlinx.coroutines.flow.Flow

interface DetalheEntregaUseCase {
    suspend fun getDetalheEntrega(idRomaneio: Int, idEntrega: Int) : Flow<DetalheEntrega>
}