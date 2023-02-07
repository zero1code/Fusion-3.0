package br.com.fusiondms.core.network.repository.recusarcarga

import android.content.Context
import br.com.fusiondms.core.database.AppDatabase
import br.com.fusiondms.core.database.model.romaneio.RomaneioEntity
import br.com.fusiondms.core.model.Resource
import br.com.fusiondms.core.model.romaneio.Romaneio
import br.com.fusiondms.core.network.api.FusionApi
import br.com.fusiondms.core.network.model.RomaneioDto
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class RecusarCargaRepositoryImpl @Inject constructor(
    private val fusionApi: FusionApi,
    private val appDatabase: AppDatabase,
    private val dispatcher: CoroutineDispatcher,
    private val context: Context
) : RecusarCargaRepository {
    override suspend fun recusarCarga(romaneio: Romaneio): Flow<Resource<Int>> {
        return flow<Resource<Int>>  {
            try {
                val result = fusionApi.postRecusarCarga(RomaneioDto().mapModelToDto(romaneio))
                if (result.isSuccessful) {
                    appDatabase.getRomaneioDao()
                        .deleteCarga(RomaneioEntity().mapModelToEntity(romaneio))

                    emit(Resource.Success(result.message(), result.code()))
                } else {
                    emit(Resource.Error(result.message(), result.code()))
                }
            } catch (e: Exception) {
                  emit(Resource.Error(e.message ?: "", -1))
            }
        }.flowOn(dispatcher)
    }
}