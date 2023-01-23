package br.com.fusiondms.modnetwork.repository.recusarcarga

import br.com.fusiondms.modmodel.Resource
import br.com.fusiondms.modmodel.romaneio.Romaneio
import kotlinx.coroutines.flow.Flow

interface RecusarCargaRepository {
    suspend fun recusarCarga(romaneio: Romaneio) : Flow<Resource<Int>>
}