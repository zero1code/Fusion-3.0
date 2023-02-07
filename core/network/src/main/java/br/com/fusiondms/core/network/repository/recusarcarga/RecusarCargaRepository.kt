package br.com.fusiondms.core.network.repository.recusarcarga

import br.com.fusiondms.core.model.Resource
import br.com.fusiondms.core.model.romaneio.Romaneio
import kotlinx.coroutines.flow.Flow

interface RecusarCargaRepository {
    suspend fun recusarCarga(romaneio: Romaneio) : Flow<Resource<Int>>
}