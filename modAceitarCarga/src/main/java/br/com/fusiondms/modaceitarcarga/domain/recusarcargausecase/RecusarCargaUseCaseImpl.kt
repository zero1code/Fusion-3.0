package br.com.fusiondms.modaceitarcarga.domain.recusarcargausecase

import br.com.fusiondms.modmodel.Resource
import br.com.fusiondms.modmodel.Romaneio
import br.com.fusiondms.modnetwork.repository.recusarcarga.RecusarCargaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RecusarCargaUseCaseImpl @Inject constructor(
    private val recusarCargaRepository: RecusarCargaRepository
) : RecusarCargaUseCase {
    override suspend fun recusarCarga(romaneio: Romaneio): Flow<Resource<Int>> {
        return recusarCargaRepository.recusarCarga(romaneio)
    }
}