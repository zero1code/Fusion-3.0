package br.com.fusiondms.modaceitarcarga.domain.recusarcargausecase

import br.com.fusiondms.modmodel.Resource
import br.com.fusiondms.modmodel.romaneio.Romaneio
import kotlinx.coroutines.flow.Flow

interface RecusarCargaUseCase {
    suspend fun recusarCarga(romaneio: Romaneio) : Flow<Resource<Int>>
}