package br.com.fusiondms.core.database.repository.entregas.detalheentrega

import android.content.Context
import br.com.fusiondms.core.database.dao.DetalheEntregaDao
import br.com.fusiondms.core.database.dao.RecebimentoDao
import br.com.fusiondms.core.database.model.entrega.EntregaItemEntity
import br.com.fusiondms.core.database.model.recebimento.RecebimentoEntity
import br.com.fusiondms.core.database.model.recebimento.TipoPagamentoEntity
import br.com.fusiondms.core.model.entrega.DetalheEntrega
import br.com.fusiondms.core.model.exceptions.ErrorInserirRecebimento
import br.com.fusiondms.core.model.recebimento.Recebimento
import br.com.fusiondms.core.model.recebimento.TipoPagamento
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class DetalheEntregaRepositoryImpl @Inject constructor(
    private val detalheEntregaDao: DetalheEntregaDao,
    private val recebimentoDao: RecebimentoDao,
    private val dispatcher: CoroutineDispatcher,
    private val context: Context
) : DetalheEntregaRepository {
    override suspend fun getDetalheEntrega(idRomaneio: Int, idEntrega: Int): Flow<DetalheEntrega> {
        return flow {
            try {
                val entityList = detalheEntregaDao.getEntregaItem(idRomaneio, idEntrega)
                val listaEntregaItem = EntregaItemEntity().entityListToModel(entityList)
                emit(DetalheEntrega(listaEntregaItem = listaEntregaItem))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.flowOn(dispatcher)
    }

    override suspend fun getListaRecebimento(
        idRomaneio: Int,
        idEntrega: Int
    ): Flow<List<Recebimento>> {
        return flow {
            try {
                val entityList = recebimentoDao.getAllRecebimento(idRomaneio, idEntrega)
                val listaRecebimento = RecebimentoEntity().entityListToModel(entityList)
                emit(listaRecebimento)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.flowOn(dispatcher)
    }

    override suspend fun inserirRecebimento(recebimento: Recebimento): Flow<Long> {
        return flow {
            try {
                val entity = RecebimentoEntity().mapModelToEntity(recebimento)
                val result = recebimentoDao.inserirRecebimento(entity)
                if (result > 0) {
                    emit(result)
                } else {
                    throw ErrorInserirRecebimento("Não foi possível adicionar o recebimento.")
                }
            }catch (e: Exception) {
                e.printStackTrace()
            }
        }.flowOn(dispatcher)
    }

    override suspend fun getFormaPagamento(formaPagamento: String): Flow<TipoPagamento> {
        return flow {
            try {
                val entity = recebimentoDao.getFormaPagamento(formaPagamento)
                val tipoPagamento = TipoPagamentoEntity().mapEntityToModel(entity)
                emit(tipoPagamento)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.flowOn(dispatcher)
    }
}