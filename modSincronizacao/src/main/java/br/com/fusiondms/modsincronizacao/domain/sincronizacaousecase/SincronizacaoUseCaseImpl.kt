package br.com.fusiondms.modsincronizacao.domain.sincronizacaousecase

import br.com.fusiondms.modmodel.Resource
import br.com.fusiondms.modnetwork.model.SincronizacaoDto
import br.com.fusiondms.modnetwork.repository.sincronizacao.SincronizacaoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SincronizacaoUseCaseImpl @Inject constructor(
    private val sincronizacaoRepository: SincronizacaoRepository
) : SincroniozacaoUseCase {
    override suspend fun getSincronizacao(): Flow<Resource<SincronizacaoDto>> {
        return sincronizacaoRepository.getSincronizacao()
    }
}