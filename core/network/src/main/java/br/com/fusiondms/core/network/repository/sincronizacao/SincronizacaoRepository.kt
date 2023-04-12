package br.com.fusiondms.core.network.repository.sincronizacao

import br.com.fusiondms.core.network.model.ParametrosDao
import kotlinx.coroutines.flow.Flow


interface SincronizacaoRepository {

    fun getSincronizacao() : Flow<Int>
    fun addParametros(parametrosDao: ParametrosDao?)
}