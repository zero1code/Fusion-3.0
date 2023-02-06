package br.com.fusiondms.modnetwork.repository.sincronizacao

import br.com.fusiondms.modmodel.Resource
import br.com.fusiondms.modnetwork.model.SincronizacaoDto
import kotlinx.coroutines.flow.Flow


interface SincronizacaoRepository {

    fun getSincronizacao() : Flow<Int>
}