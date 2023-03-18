package br.com.fusiondms.feature.cargas.domain.recusarcargausecase

import br.com.fusiondms.core.model.romaneio.Romaneio
import br.com.fusiondms.core.network.repository.recusarromaneio.RecusarRomaneioRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RecusarCargaUseCaseImpl @Inject constructor(
    private val recusarRomaneioRepository: RecusarRomaneioRepository
) : RecusarCargaUseCase {
    override suspend fun recusarCarga(romaneio: Romaneio): Flow<Int> {
        return recusarRomaneioRepository.recusarRomaneio(romaneio)
    }
}