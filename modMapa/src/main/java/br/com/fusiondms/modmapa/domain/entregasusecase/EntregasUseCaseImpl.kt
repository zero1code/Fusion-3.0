package br.com.fusiondms.modmapa.domain.entregasusecase

import br.com.fusiondms.moddatabase.repository.entregas.EntregasRepository
import br.com.fusiondms.modmodel.Entrega
import br.com.fusiondms.modmodel.EntregasPorCliente
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class EntregasUseCaseImpl @Inject constructor(
    private val entregasRepository: EntregasRepository
) : EntregasUseCase {

    override suspend fun getListaEntrega(idRomaneio: Int): Flow<List<EntregasPorCliente>> {
        return flow {
            try {
                entregasRepository.getListaEntrega(idRomaneio).collect { listaEntrega ->
                    val entregasPorCliente: ArrayList<EntregasPorCliente> = arrayListOf()
                    listaEntrega
                        .groupBy { it.idCliente }
                        .forEach { cliente ->
                            val dadosCliente = EntregasPorCliente(
                                idRomaneio = cliente.value[0].idRomaneio,
                                idCliente = cliente.key,
                                cliente = cliente.value[0].dadosCliente,
                                local = cliente.value[0].localCliente,
                                entregas = cliente.value
                            )
                            entregasPorCliente.add(dadosCliente)
                        }
                    emit(entregasPorCliente)
                }
            } catch (e: Exception) {
                emit(arrayListOf())
            }
        }
    }
}