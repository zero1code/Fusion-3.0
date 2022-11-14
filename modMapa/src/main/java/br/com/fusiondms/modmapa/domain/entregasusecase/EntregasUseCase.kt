package br.com.fusiondms.modmapa.domain.entregasusecase

import br.com.fusiondms.modmodel.EntregasPorCliente
import kotlinx.coroutines.flow.Flow

interface EntregasUseCase {
    suspend fun getListaEntrega(idRomaneio: Int) : Flow<List<EntregasPorCliente>>
}