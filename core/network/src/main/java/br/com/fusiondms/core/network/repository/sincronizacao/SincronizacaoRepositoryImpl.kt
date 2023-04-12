package br.com.fusiondms.core.network.repository.sincronizacao

import br.com.fusiondms.core.database.AppDatabase
import br.com.fusiondms.core.datastore.chaves.PreferencesChaves.KEY_CANHOTO_APROVACAO
import br.com.fusiondms.core.datastore.chaves.PreferencesChaves.KEY_CANHOTO_CORTE
import br.com.fusiondms.core.datastore.chaves.PreferencesChaves.KEY_CANHOTO_EXCLUIR
import br.com.fusiondms.core.datastore.chaves.PreferencesChaves.KEY_CANHOTO_EXIGIR_EM_COLETA
import br.com.fusiondms.core.datastore.chaves.PreferencesChaves.KEY_CANHOTO_OBRIGATORIO
import br.com.fusiondms.core.datastore.chaves.PreferencesChaves.KEY_CANHOTO_QUALIDADE
import br.com.fusiondms.core.datastore.chaves.PreferencesChaves.KEY_ENTREGAS_ADIAR_TODAS
import br.com.fusiondms.core.datastore.chaves.PreferencesChaves.KEY_ENTREGAS_EVENTO_CLIENTES
import br.com.fusiondms.core.datastore.chaves.PreferencesChaves.KEY_ENTREGAS_INDICAR_COLETA
import br.com.fusiondms.core.datastore.chaves.PreferencesChaves.KEY_ENTREGAS_POR_CLIENTE
import br.com.fusiondms.core.datastore.chaves.PreferencesChaves.KEY_ENTREGAS_SOLICITAR_COLETA
import br.com.fusiondms.core.datastore.chaves.PreferencesChaves.KEY_ENTREGAS_TEMPO_ESPERA
import br.com.fusiondms.core.datastore.chaves.PreferencesChaves.KEY_LOCALIZACAO_TEMPO_ENVIO
import br.com.fusiondms.core.datastore.repository.DataStoreRepository
import br.com.fusiondms.core.model.exceptions.ErrorApiSincronizacao
import br.com.fusiondms.core.network.api.FusionApi
import br.com.fusiondms.core.network.model.ParametrosDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runBlocking
import java.net.ConnectException
import java.net.SocketTimeoutException
import javax.inject.Inject

class SincronizacaoRepositoryImpl @Inject constructor(
    private val fusionApi: FusionApi,
    private val appDatabase: AppDatabase,
    private val dataStoreRepository: DataStoreRepository,
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
                           appDatabase.getRecebimentoDao().inserirAllRecebimentos(it.listaRecebimento)
                           appDatabase.getRecebimentoDao().inserirAllFormasPagamento(it.listaTipoPagamento)
                           addParametros(it.parametrosDao)
                           emit(code())
                       } ?: throw ErrorApiSincronizacao(message(), code())
                   } else {
                       throw ErrorApiSincronizacao(message(), code())
                   }
               }
            } catch (ste: SocketTimeoutException) {
                throw Exception("Não houve resposta do servidor:\n${ste.message}")
            } catch (ce: ConnectException) {
                throw Exception(ce.message)
            } catch (ise: IllegalStateException) {
                throw Exception("Erro no banco de dados local:\n${ise.message}")
            } catch (e: Exception) {
                e.printStackTrace()
                throw Exception(e.message)
            }
        }.flowOn(dispatcher)
    }

    override fun addParametros(parametrosDao: ParametrosDao?) {
        try {
            runBlocking {
                parametrosDao?.run {
                    dataStoreRepository.putBoolean(KEY_CANHOTO_OBRIGATORIO, canhotoObrigatorio)
                    dataStoreRepository.putBoolean(KEY_CANHOTO_APROVACAO, canhotoAprovacao)
                    dataStoreRepository.putBoolean(KEY_CANHOTO_QUALIDADE, canhotoQualidade)
                    dataStoreRepository.putBoolean(KEY_CANHOTO_CORTE, canhotoCorte)
                    dataStoreRepository.putBoolean(KEY_CANHOTO_EXIGIR_EM_COLETA, canhotoExigirEmColeta)
                    dataStoreRepository.putBoolean(KEY_CANHOTO_EXCLUIR, canhotoExcluir)
                    dataStoreRepository.putBoolean(KEY_ENTREGAS_POR_CLIENTE, entregasPorCliente)
                    dataStoreRepository.putBoolean(KEY_ENTREGAS_ADIAR_TODAS, entregasAdiarTodas)
                    dataStoreRepository.putBoolean(KEY_ENTREGAS_INDICAR_COLETA, entregasIndicarColeta)
                    dataStoreRepository.putBoolean(KEY_ENTREGAS_SOLICITAR_COLETA, entregasSolicitarColeta)
                    dataStoreRepository.putBoolean(KEY_ENTREGAS_EVENTO_CLIENTES, entregasEventoClientes)
                    dataStoreRepository.putString(KEY_ENTREGAS_TEMPO_ESPERA, entregasTempoEspera)
                    dataStoreRepository.putInt(KEY_LOCALIZACAO_TEMPO_ENVIO, localizacaoTempoEnvio)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}