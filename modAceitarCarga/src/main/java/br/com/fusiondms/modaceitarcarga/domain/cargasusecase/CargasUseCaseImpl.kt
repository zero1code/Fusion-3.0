package br.com.fusiondms.modaceitarcarga.domain.cargasusecase

import br.com.fusiondms.moddatabase.repository.romaneios.CargasRepository
import br.com.fusiondms.modmodel.Romaneio
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CargasUseCaseImpl @Inject constructor(
    private val cargasRepository: CargasRepository
) : CargasUseCase{
    override suspend fun deleteCarga(romaneio: Romaneio): Flow<Int> {
        return cargasRepository.deleteCarga(romaneio)
    }

    override suspend fun getListaCarga(): Flow<List<Romaneio>> {
        return cargasRepository.getListaCarga()
    }
}