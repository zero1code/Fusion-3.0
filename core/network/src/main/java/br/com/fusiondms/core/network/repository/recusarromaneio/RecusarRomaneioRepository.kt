package br.com.fusiondms.core.network.repository.recusarromaneio

import br.com.fusiondms.core.model.romaneio.Romaneio
import kotlinx.coroutines.flow.Flow

interface RecusarRomaneioRepository {
    suspend fun recusarRomaneio(romaneio: Romaneio) : Flow<Int>
}