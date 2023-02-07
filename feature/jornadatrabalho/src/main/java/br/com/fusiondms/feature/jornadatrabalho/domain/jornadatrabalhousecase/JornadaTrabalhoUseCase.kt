package br.com.fusiondms.feature.jornadatrabalho.domain.jornadatrabalhousecase

import br.com.fusiondms.core.model.jornadatrabalho.Colaborador
import br.com.fusiondms.core.model.jornadatrabalho.JornadaTrabalho
import br.com.fusiondms.core.model.jornadatrabalho.ReciboRegistroPonto
import br.com.fusiondms.core.model.jornadatrabalho.RegistroPonto
import kotlinx.coroutines.flow.Flow

interface JornadaTrabalhoUseCase {
    suspend fun inserirColaborador(colaborador: Colaborador): Flow<Long>
    suspend fun inserirRegistroPonto(registroPonto: RegistroPonto): Flow<Long>
    suspend fun getColaboradores(): Flow<List<Colaborador>>
    suspend fun getRecibos(): Flow<List<ReciboRegistroPonto>>
    suspend fun getRegistrosPonto(dataAtual: Long): Flow<List<JornadaTrabalho>>
}