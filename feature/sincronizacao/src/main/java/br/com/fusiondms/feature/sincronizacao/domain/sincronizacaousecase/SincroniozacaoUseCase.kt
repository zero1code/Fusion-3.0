package br.com.fusiondms.modsincronizacao.domain.sincronizacaousecase

import br.com.fusiondms.modmodel.Resource
import br.com.fusiondms.modnetwork.model.SincronizacaoDto
import kotlinx.coroutines.flow.Flow

interface SincroniozacaoUseCase {
    suspend fun getSincronizacao() : Flow<Int>
}