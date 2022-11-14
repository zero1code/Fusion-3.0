package br.com.fusiondms.moddatabase.repository.entregas

import br.com.fusiondms.moddatabase.dao.EntregaDao
import br.com.fusiondms.moddatabase.model.EntregaEntity
import br.com.fusiondms.modmodel.Entrega
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class EntregasRepositoryImpl @Inject constructor(
    private val entregaDao: EntregaDao,
    private val dispatcher: CoroutineDispatcher
) : EntregasRepository {
    override suspend fun getListaEntrega(idRomaneio: Int): Flow<List<Entrega>> {
        return flow {
            try {
                val lista = entregaDao.getListaEntrega(idRomaneio)
                emit(EntregaEntity().entityListToModel(lista))
            } catch (e: Exception) {
                emit(arrayListOf())
            }
        }.flowOn(dispatcher)
    }
}