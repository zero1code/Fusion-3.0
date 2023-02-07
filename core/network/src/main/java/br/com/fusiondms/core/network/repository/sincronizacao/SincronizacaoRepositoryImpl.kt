package br.com.fusiondms.modnetwork.repository.sincronizacao

import br.com.fusiondms.moddatabase.AppDatabase
import br.com.fusiondms.modmodel.Resource
import br.com.fusiondms.modmodel.exceptions.ErrorApiSincronizacao
import br.com.fusiondms.modmodel.exceptions.ErrorSincronizacao
import br.com.fusiondms.modnetwork.api.FusionApi
import br.com.fusiondms.modnetwork.model.SincronizacaoDto
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class SincronizacaoRepositoryImpl @Inject constructor(
    private val fusionApi: FusionApi,
    private val appDatabase: AppDatabase,
    private val dispatcher: CoroutineDispatcher
) : SincronizacaoRepository {
    override fun getSincronizacao(): Flow<Int> {
        return flow {
            try {
                val response = fusionApi.getSincronizacao()
                if (response.isSuccessful) {
                    response.body()?.let {
                        appDatabase.getRomaneioDao().inserirCargas(it.listaRomaneio)
                        appDatabase.getEntregaDao().inserirEntregas(it.listaEntrega)
                        appDatabase.getColaboradorDto().inserirColaboradores(it.listaColaborador)
                        appDatabase.getColaboradorDto().inserirRegistrosPonto(it.listaRegistroPonto)
                    }
                    emit(response.code())
                } else {
                    throw ErrorApiSincronizacao(response.message(), response.code())
                }
            } catch (e: Exception) {
                throw ErrorSincronizacao(e.message, )
            }
        }.flowOn(dispatcher)
    }
}