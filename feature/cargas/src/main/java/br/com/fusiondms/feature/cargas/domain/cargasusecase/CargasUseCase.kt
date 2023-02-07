package br.com.fusiondms.feature.cargas.domain.cargasusecase

import br.com.fusiondms.core.model.romaneio.Romaneio
import kotlinx.coroutines.flow.Flow

interface CargasUseCase {
    suspend fun deleteCarga(romaneio: Romaneio) : Flow<Int>
    suspend fun getListaCarga() : Flow<List<Romaneio>>
}