package br.com.fusiondms.core.network.repository.recusarromaneio

import android.content.Context
import br.com.fusiondms.core.database.dao.RomaneioDao
import br.com.fusiondms.core.database.model.romaneio.RomaneioEntity
import br.com.fusiondms.core.model.exceptions.ErrorApiRecusarRomaneio
import br.com.fusiondms.core.model.romaneio.Romaneio
import br.com.fusiondms.core.network.api.FusionApi
import br.com.fusiondms.core.network.model.RomaneioDto
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class RecusarRomaneioRepositoryImpl @Inject constructor(
    private val fusionApi: FusionApi,
    private val romaneioDao: RomaneioDao,
    private val dispatcher: CoroutineDispatcher,
    private val context: Context
) : RecusarRomaneioRepository {
    override suspend fun recusarRomaneio(romaneio: Romaneio): Flow<Int> {
        return flow {
            try {
                RomaneioDto().mapModelToDto(romaneio).let { romaneioDto ->
                    fusionApi.postRecusarCarga(romaneioDto).run {
                        if (isSuccessful) {
                            romaneioDao.deletarRomaneio(RomaneioEntity().mapModelToEntity(romaneio))
                            emit(code())
                        } else {
                            throw ErrorApiRecusarRomaneio("Não foi possível recusar esse romaneio.")
                        }
                    }
                }
            } catch (e: Exception) {
                  e.printStackTrace()
            }
        }.flowOn(dispatcher)
    }
}