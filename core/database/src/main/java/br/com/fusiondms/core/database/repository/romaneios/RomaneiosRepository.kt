package br.com.fusiondms.core.database.repository.romaneios

import br.com.fusiondms.core.model.romaneio.Romaneio
import kotlinx.coroutines.flow.Flow

interface RomaneiosRepository {
    suspend fun deleteCarga(romaneio: Romaneio) : Flow<Int>
    suspend fun getListaCarga() : Flow<List<Romaneio>>
}