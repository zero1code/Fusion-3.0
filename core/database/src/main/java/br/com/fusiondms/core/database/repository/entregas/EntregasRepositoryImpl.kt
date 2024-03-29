package br.com.fusiondms.core.database.repository.entregas

import android.content.Context
import br.com.fusiondms.core.database.dao.EntregaDao
import br.com.fusiondms.core.database.model.entrega.EntregaEntity
import br.com.fusiondms.core.model.entrega.Entrega
import br.com.fusiondms.core.model.exceptions.ErrorAtualizarStatusEntrega
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class EntregasRepositoryImpl @Inject constructor(
    private val entregaDao: EntregaDao,
    private val dispatcher: CoroutineDispatcher,
    private val context: Context
) : EntregasRepository {
    override suspend fun getListaEntrega(idRomaneio: Int): Flow<List<Entrega>> {
        return flow {
            try {
                val entityList = entregaDao.getListaEntrega(idRomaneio)
                emit(EntregaEntity().entityListToModel(entityList))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.flowOn(dispatcher)
    }

    override suspend fun updateStatusEntrega(entrega: Entrega): Flow<Int> {
        return flow {
            try {
                val result = entregaDao.updateStatusEntrega(EntregaEntity().mapModelToEntity(entrega))
                if (result <= 0) {
                    throw ErrorAtualizarStatusEntrega("Não foi possível atualizar o status da entrega.")
                }
                emit(result)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }.flowOn(dispatcher)
    }

    override suspend fun updateAllStatusEntrega(idCliente: Int, idEvento: Int): Flow<Int> {
        return flow {
            try {
                val result = entregaDao.updateAllStatusEntrega(idCliente, idEvento)
                if (result <= 0) {
                    throw ErrorAtualizarStatusEntrega("Não foi possível atualizar os status das entregas.")
                }
                emit(result)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.flowOn(dispatcher)
    }
}