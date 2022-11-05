package br.com.fusiondms.modnetwork.repository.recusarcarga

import br.com.fusiondms.modmodel.Romaneio
import br.com.fusiondms.modnetwork.util.Resource
import kotlinx.coroutines.flow.Flow

interface RecusarCargaRepository {
    suspend fun recusarCarga(romaneio: Romaneio) : Flow<Resource<Int>>
}