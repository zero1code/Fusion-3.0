package br.com.fusiondms.modaceitarcarga.domain.recusarcargausecase

import br.com.fusiondms.modmodel.Romaneio
import br.com.fusiondms.modnetwork.util.Resource
import kotlinx.coroutines.flow.Flow

interface RecusarCargaUseCase {
    suspend fun recusarCarga(romaneio: Romaneio) : Flow<Resource<Int>>
}