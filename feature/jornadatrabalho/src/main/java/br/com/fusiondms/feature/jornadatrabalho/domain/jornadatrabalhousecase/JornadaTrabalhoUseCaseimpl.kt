package br.com.fusiondms.feature.jornadatrabalho.domain.jornadatrabalhousecase

import br.com.fusiondms.core.common.converterDataParaDiaMesAno
import br.com.fusiondms.core.databasejornadatrabalho.repository.jornadatrabalho.JornadaTrabalhoRepository
import br.com.fusiondms.core.model.jornadatrabalho.Colaborador
import br.com.fusiondms.core.model.jornadatrabalho.JornadaTrabalho
import br.com.fusiondms.core.model.jornadatrabalho.ReciboRegistroPonto
import br.com.fusiondms.core.model.jornadatrabalho.RegistroPonto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class JornadaTrabalhoUseCaseimpl @Inject constructor(
    private val jornadaTrabalhoRepository: JornadaTrabalhoRepository

) : JornadaTrabalhoUseCase {
    override suspend fun inserirColaborador(colaborador: Colaborador): Flow<Long> {
        return flow {
            try {
                jornadaTrabalhoRepository.getColaborador(colaborador.matricula).collect { colaboradorExiste ->
                    if (colaboradorExiste.matricula == 0) {
                        jornadaTrabalhoRepository.inserirColaborador(colaborador).collect {result ->
                            emit(result)
                        }
                    } else {
                        emit(-2)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override suspend fun inserirRegistroPonto(registroPonto: RegistroPonto): Flow<Long> {
        return flow {
            try {
                jornadaTrabalhoRepository.inserirRegistroPonto(registroPonto).collect { result ->
                    emit(result)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override suspend fun getColaboradores(): Flow<List<Colaborador>> {
        return flow {
            try {
                jornadaTrabalhoRepository.getColaboradores().collect { listaColaborador ->
                    emit(listaColaborador)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override suspend fun getRecibos(): Flow<List<ReciboRegistroPonto>> {
        return flow {
            try {
                jornadaTrabalhoRepository.getRecibos().collect { listaRecibo ->
                    emit(listaRecibo)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override suspend fun getRegistrosPonto(dataAtual: Long): Flow<List<JornadaTrabalho>> {
        return flow {
            val dataHoje = converterDataParaDiaMesAno(dataAtual)
            jornadaTrabalhoRepository.getColaboradores().collect { listaColaborador ->
                val listaJornada = arrayListOf<JornadaTrabalho>()
                    listaColaborador.forEach { colaborador ->
                        jornadaTrabalhoRepository.getRegistrosPonto(colaborador.matricula).collect { listaRegistrosPonto ->
                            val lista = listaRegistrosPonto.toMutableList()
                            lista.removeAll {
                                val dataRegistro = converterDataParaDiaMesAno(it.dataRegistro)
                                dataRegistro != dataHoje
                            }
                            listaJornada.add(JornadaTrabalho(colaborador, lista))
                        }
                }
                emit(listaJornada)
            }
        }
    }
}