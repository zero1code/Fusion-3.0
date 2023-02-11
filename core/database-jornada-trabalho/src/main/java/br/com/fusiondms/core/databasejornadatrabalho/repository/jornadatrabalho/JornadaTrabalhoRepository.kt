package br.com.fusiondms.core.databasejornadatrabalho.repository.jornadatrabalho

import br.com.fusiondms.core.model.jornadatrabalho.Colaborador
import br.com.fusiondms.core.model.jornadatrabalho.ReciboRegistroPonto
import br.com.fusiondms.core.model.jornadatrabalho.RegistroPonto
import kotlinx.coroutines.flow.Flow

interface JornadaTrabalhoRepository {
    suspend fun inserirColaborador(colaborador: Colaborador): Flow<Long>
    suspend fun inserirRegistroPonto(registroPonto: RegistroPonto): Flow<Long>
    suspend fun getColaboradores(): Flow<List<Colaborador>>
    suspend fun getRecibos(): Flow<List<ReciboRegistroPonto>>
    suspend fun getColaborador(id: Int): Flow<Colaborador>
    suspend fun getRegistrosPonto(matricula: Int): Flow<List<RegistroPonto>>
}