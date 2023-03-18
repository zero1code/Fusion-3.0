package br.com.fusiondms.feature.entregas.domain.entregasusecase

import android.content.Context
import br.com.fusiondms.core.database.repository.entregas.EntregasRepository
import br.com.fusiondms.core.model.Conteudo
import br.com.fusiondms.core.model.entrega.Entrega
import br.com.fusiondms.core.model.entrega.EntregasPorCliente
import br.com.fusiondms.core.model.exceptions.ErrorAtualizarStatusEntrega
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class EntregasUseCaseImpl @Inject constructor(
    private val entregasRepository: EntregasRepository,
    private val context: Context
) : EntregasUseCase {

    override suspend fun getListaEntrega(idRomaneio: Int): Flow<List<Conteudo>> {
        return flow {
            try {
                entregasRepository.getListaEntrega(idRomaneio).collect { listaEntrega ->
                    val entregasPorCliente: ArrayList<Conteudo> = arrayListOf()
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
                            entregasPorCliente.add(Conteudo.CarouselEntrega(cliente.key, dadosCliente))
                        }
                    emit(entregasPorCliente)
                }
            } catch (e: Exception) {
                emit(arrayListOf())
            }
        }
    }

    override suspend fun updateStatusEntrega(entrega: Entrega, idEvento: Int): Flow<Int> {
        return if (entrega.statusEntrega != idEvento.toString()) {
            val newEntrega = entrega.copy(statusEntrega = idEvento.toString())
            entregasRepository.updateStatusEntrega(newEntrega)
        } else {
            throw ErrorAtualizarStatusEntrega("A entrega já está com esse status.")
        }
    }
}