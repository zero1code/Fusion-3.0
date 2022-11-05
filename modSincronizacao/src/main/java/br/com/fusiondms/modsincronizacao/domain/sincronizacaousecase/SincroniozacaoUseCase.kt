package br.com.fusiondms.modsincronizacao.domain.sincronizacaousecase

import br.com.fusiondms.modnetwork.model.SincronizacaoDto
import br.com.fusiondms.modnetwork.util.Resource
import kotlinx.coroutines.flow.Flow

interface SincroniozacaoUseCase {
    suspend fun getSincronizacao() : Flow<Resource<SincronizacaoDto>>
}