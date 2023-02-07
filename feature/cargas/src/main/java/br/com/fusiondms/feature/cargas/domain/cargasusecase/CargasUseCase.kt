package br.com.fusiondms.modaceitarcarga.domain.cargasusecase

import br.com.fusiondms.modmodel.romaneio.Romaneio
import kotlinx.coroutines.flow.Flow

interface CargasUseCase {
    suspend fun deleteCarga(romaneio: Romaneio) : Flow<Int>
    suspend fun getListaCarga() : Flow<List<Romaneio>>
}