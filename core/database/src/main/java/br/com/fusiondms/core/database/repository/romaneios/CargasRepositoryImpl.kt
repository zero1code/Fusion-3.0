package br.com.fusiondms.core.database.repository.romaneios

import br.com.fusiondms.core.database.dao.CargaDao
import br.com.fusiondms.core.database.model.romaneio.RomaneioEntity
import br.com.fusiondms.core.model.romaneio.Romaneio
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class CargasRepositoryImpl @Inject constructor(
    private val cargaDao: CargaDao,
    private val dispatcher: CoroutineDispatcher
) : CargasRepository {
    override suspend fun deleteCarga(romaneio: Romaneio): Flow<Int> {
        return flow {
            try {
                val result = cargaDao.deleteCarga(RomaneioEntity().mapModelToEntity(romaneio))
                emit(result)
            }catch (e: Exception) {
                emit(-1)
            }
        }.flowOn(dispatcher)
    }

    override suspend fun getListaCarga(): Flow<List<Romaneio>> {
        return flow {
            try {
                val lista = cargaDao.getListaCarga()
                emit(RomaneioEntity().entityListToModel(lista))
            } catch (e: Exception) {
                emit(listOf())
            }
        }.flowOn(dispatcher)
    }
}