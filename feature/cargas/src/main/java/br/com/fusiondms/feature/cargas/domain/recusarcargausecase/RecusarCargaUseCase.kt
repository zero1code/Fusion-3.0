package br.com.fusiondms.feature.cargas.domain.recusarcargausecase

import br.com.fusiondms.core.model.Resource
import br.com.fusiondms.core.model.romaneio.Romaneio
import kotlinx.coroutines.flow.Flow

interface RecusarCargaUseCase {
    suspend fun recusarCarga(romaneio: Romaneio) : Flow<Resource<Int>>
}