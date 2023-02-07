package br.com.fusiondms.core.network.repository.sincronizacao

import kotlinx.coroutines.flow.Flow


interface SincronizacaoRepository {

    fun getSincronizacao() : Flow<Int>
}