package br.com.fusiondms.modaceitarcarga.domain.cargasusecase

import br.com.fusiondms.modmodel.Romaneio
import br.com.fusiondms.modnetwork.util.Resource
import kotlinx.coroutines.flow.Flow

interface CargasUseCase {
    suspend fun deleteCarga(romaneio: Romaneio) : Flow<Int>
    suspend fun getListaCarga() : Flow<List<Romaneio>>
}