package br.com.fusiondms.feature.cargas.domain.cargasusecase

import br.com.fusiondms.core.database.repository.romaneios.RomaneiosRepository
import br.com.fusiondms.core.model.romaneio.Romaneio
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CargasUseCaseImpl @Inject constructor(
    private val romaneiosRepository: RomaneiosRepository
) : CargasUseCase {
    override suspend fun deleteCarga(romaneio: Romaneio): Flow<Int> {
        return romaneiosRepository.deleteCarga(romaneio)
    }

    override suspend fun getListaCarga(): Flow<List<Romaneio>> {
        return romaneiosRepository.getListaCarga()
    }
}