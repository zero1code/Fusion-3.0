package br.com.fusiondms.core.database.repository.entregas.detalheentrega

import android.content.Context
import br.com.fusiondms.core.database.dao.DetalheEntregaDao
import br.com.fusiondms.core.database.model.entrega.EntregaItemEntity
import br.com.fusiondms.core.model.entrega.DetalheEntrega
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class DetalheEntregaRepositoryImpl @Inject constructor(
    private val detalheEntregaDao: DetalheEntregaDao,
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

}