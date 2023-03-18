package br.com.fusiondms.core.database.repository.romaneios

import br.com.fusiondms.core.database.dao.RomaneioDao
import br.com.fusiondms.core.database.model.romaneio.RomaneioEntity
import br.com.fusiondms.core.model.exceptions.ErrorDeletarRomaneio
import br.com.fusiondms.core.model.exceptions.ErrorGetListaRomaneio
import br.com.fusiondms.core.model.romaneio.Romaneio
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class RomaneiosRepositoryImpl @Inject constructor(
    private val romaneioDao: RomaneioDao,
    private val dispatcher: CoroutineDispatcher
) : RomaneiosRepository {
    override suspend fun deleteCarga(romaneio: Romaneio): Flow<Int> {
        return flow {
            try {
                val result = romaneioDao.deletarRomaneio(RomaneioEntity().mapModelToEntity(romaneio))
                if (result <= 0) {
                    throw ErrorDeletarRomaneio("Não foi possível remover esse romaneio.")
                }
                emit(result)
            }catch (e: Exception) {
                e.printStackTrace()
            }
        }.flowOn(dispatcher)
    }

    override suspend fun getListaCarga(): Flow<List<Romaneio>> {
        return flow {
            try {
                val lista = romaneioDao.getListaRomaneio()
                emit(RomaneioEntity().entityListToModel(lista))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.flowOn(dispatcher)
    }
}