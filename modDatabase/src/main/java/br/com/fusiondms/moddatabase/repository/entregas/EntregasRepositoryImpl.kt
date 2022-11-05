package br.com.fusiondms.moddatabase.repository.entregas

import br.com.fusiondms.moddatabase.dao.EntregaDao
import br.com.fusiondms.moddatabase.model.EntregaEntity
import br.com.fusiondms.modmodel.Entrega
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EntregasRepositoryImpl @Inject constructor(
    private val entregaDao: EntregaDao,
    private val dispatcher: CoroutineDispatcher
) : EntregasRepository {
    override suspend fun getListaEntrega(): List<Entrega> {
        return withContext(dispatcher) {

            EntregaEntity().entityListToModel(
                arrayListOf(
                    EntregaEntity(),
                    EntregaEntity(),
                    EntregaEntity(),
                    EntregaEntity(),
                    EntregaEntity(),
                    EntregaEntity()
                )
            )
//            entregaDao.getListaEntrega()
        }
    }
}