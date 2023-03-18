package br.com.fusiondms.core.network.repository.sincronizacao

import android.util.Log
import br.com.fusiondms.core.database.AppDatabase
import br.com.fusiondms.core.model.exceptions.ErrorApiSincronizacao
import br.com.fusiondms.core.network.api.FusionApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.net.SocketTimeoutException
import javax.inject.Inject

class SincronizacaoRepositoryImpl @Inject constructor(
    private val fusionApi: FusionApi,
    private val appDatabase: AppDatabase,
    private val dispatcher: CoroutineDispatcher
) : SincronizacaoRepository {
    override fun getSincronizacao(): Flow<Int> {
        return flow {
            try {
               fusionApi.getSincronizacao().run {
                   if (isSuccessful) {
                       body()?.let {
                           appDatabase.getRomaneioDao().inserirRomaneios(it.listaRomaneio)
                           appDatabase.getEntregaDao().inserirEntregas(it.listaEntrega)
                           appDatabase.getEntregaDao().inserirEntregasItens(it.listaEntregaItem)
                       }
                       emit(code())
                   } else {
                       throw ErrorApiSincronizacao(message(), code())
                   }
               }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.flowOn(dispatcher)
    }
}