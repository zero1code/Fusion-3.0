package br.com.fusiondms.core.databasejornadatrabalho.repository.jornadatrabalho

import android.content.Context
import br.com.fusiondms.core.databasejornadatrabalho.dao.ColaboradorDao
import br.com.fusiondms.core.databasejornadatrabalho.model.jornadatrabalho.ColaboradorEntity
import br.com.fusiondms.core.databasejornadatrabalho.model.jornadatrabalho.ReciboRegistroPontoEntity
import br.com.fusiondms.core.databasejornadatrabalho.model.jornadatrabalho.RegistroPontoEntity
import br.com.fusiondms.core.model.jornadatrabalho.Colaborador
import br.com.fusiondms.core.model.jornadatrabalho.ReciboRegistroPonto
import br.com.fusiondms.core.model.jornadatrabalho.RegistroPonto
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class JornadaTrabalhoRepositoryImpl @Inject constructor(
    private val colaboradorDao: ColaboradorDao,
    private val dispatcher: CoroutineDispatcher,
    private val context: Context
) : JornadaTrabalhoRepository {
    override suspend fun inserirColaborador(colaborador: Colaborador): Flow<Long> {
        return flow {
            try {
                val result = colaboradorDao.inserirColaborador(ColaboradorEntity().mapModelToEntity(colaborador))
                emit(result)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override suspend fun inserirRegistroPonto(registroPonto: RegistroPonto): Flow<Long> {
        return flow {
            try {
                val result = colaboradorDao.inserirRegistroPonto(RegistroPontoEntity().mapModelToEntity(registroPonto))
                emit(result)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override suspend fun getColaboradores(): Flow<List<Colaborador>> {
        return flow {
            try {
                val lista = colaboradorDao.getListaColaborador()
                emit(ColaboradorEntity().entityListToModel(lista))
            } catch (e: Exception) {
                emit(arrayListOf())
            }
        }.flowOn(dispatcher)
    }

    override suspend fun getRecibos(): Flow<List<ReciboRegistroPonto>> {
        return flow {
            try {
                val lista = colaboradorDao.getRecibos()
                emit(ReciboRegistroPontoEntity().entityListToModel(lista))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override suspend fun getColaborador(id: Int): Flow<Colaborador> {
        return flow {
            try {
                val colaborador = colaboradorDao.getColaborador(id) ?: ColaboradorEntity()
                    emit(ColaboradorEntity().mapEntityToModel(colaborador))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override suspend fun getRegistrosPonto(matricula: Int): Flow<List<RegistroPonto>> {
        return flow {
            try {
                val lista = colaboradorDao.getRegistrosPonto(matricula)
                emit(RegistroPontoEntity().entityListToModel(lista))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}