package br.com.fusiondms.moddatabase.repository.romaneios

import br.com.fusiondms.modmodel.Romaneio
import kotlinx.coroutines.flow.Flow

interface CargasRepository {
    suspend fun deleteCarga(romaneio: Romaneio) : Flow<Int>
    suspend fun getListaCarga() : Flow<List<Romaneio>>
}