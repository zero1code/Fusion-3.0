package br.com.fusiondms.modnetwork.repository.sincronizacao

import br.com.fusiondms.modnetwork.model.SincronizacaoDto
import br.com.fusiondms.modnetwork.util.Resource
import kotlinx.coroutines.flow.Flow


interface SincronizacaoRepository {

    fun getSincronizacao() : Flow<Resource<SincronizacaoDto>>
}