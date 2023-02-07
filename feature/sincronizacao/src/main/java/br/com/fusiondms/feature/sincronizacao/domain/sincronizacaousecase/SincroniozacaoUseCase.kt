package br.com.fusiondms.feature.sincronizacao.domain.sincronizacaousecase

import kotlinx.coroutines.flow.Flow

interface SincroniozacaoUseCase {
    suspend fun getSincronizacao() : Flow<Int>
}