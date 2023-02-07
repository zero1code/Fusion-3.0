package br.com.fusiondms.feature.cargas.domain.recusarcargausecase

import br.com.fusiondms.core.model.Resource
import br.com.fusiondms.core.model.romaneio.Romaneio
import br.com.fusiondms.core.network.repository.recusarcarga.RecusarCargaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RecusarCargaUseCaseImpl @Inject constructor(
    private val recusarCargaRepository: RecusarCargaRepository
) : RecusarCargaUseCase {
    override suspend fun recusarCarga(romaneio: Romaneio): Flow<Resource<Int>> {
        return recusarCargaRepository.recusarCarga(romaneio)
    }
}