package br.com.fusiondms.moddatabase.repository.entregas

import android.content.Context
import br.com.fusiondms.moddatabase.R
import br.com.fusiondms.moddatabase.dao.EntregaDao
import br.com.fusiondms.moddatabase.model.entrega.EntregaEntity
import br.com.fusiondms.modmodel.entrega.Entrega
import br.com.fusiondms.modmodel.Resource
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
                val lista = entregaDao.getListaEntrega(idRomaneio)
                emit(EntregaEntity().entityListToModel(lista))
            } catch (e: Exception) {
                emit(arrayListOf())
            }
        }.flowOn(dispatcher)
    }

    override suspend fun updateStatusEntrega(entrega: Entrega): Flow<Resource<Int>> {
        return flow<Resource<Int>> {
            try {
                val result = entregaDao.updateStatusEntrega(EntregaEntity().mapModelToEntity(entrega))
                if (result > 0) {
                    emit(Resource.Success(context.getString(R.string.label_sucesso_atualizar_entrega), result))
                } else {
                    emit(Resource.Error(context.getString(R.string.label_erro_atualizar_entrega), result))
                }
            } catch (e: Exception) {
                emit(Resource.Error(context.getString(R.string.label_erro_atualizar_entrega), -1))
            }

        }.flowOn(dispatcher)
    }
}