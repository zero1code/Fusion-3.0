package br.com.fusiondms.moddatabase.repository.romaneios

import br.com.fusiondms.moddatabase.dao.RomaneioDao
import br.com.fusiondms.moddatabase.model.RomaneioEntity
import br.com.fusiondms.modmodel.Romaneio
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RomaneiosRepositoryImpl @Inject constructor(
    private val romaneioDao: RomaneioDao,
    private val dispatcher: CoroutineDispatcher
) : RomaneiosRepository {
    override suspend fun getListaRomaneio(): List<Romaneio> {
        return withContext(dispatcher) {
            val lista = romaneioDao.getListaRomaneio()
            RomaneioEntity().entityListToModel(lista)
        }
    }
}