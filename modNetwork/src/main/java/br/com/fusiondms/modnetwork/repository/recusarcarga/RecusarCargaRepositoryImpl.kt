package br.com.fusiondms.modnetwork.repository.recusarcarga

import android.content.Context
import br.com.fusiondms.moddatabase.AppDatabase
import br.com.fusiondms.moddatabase.model.RomaneioEntity
import br.com.fusiondms.modmodel.Romaneio
import br.com.fusiondms.modnetwork.api.FusionApi
import br.com.fusiondms.modnetwork.model.RomaneioDto
import br.com.fusiondms.modnetwork.util.Resource
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