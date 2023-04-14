package br.com.fusiondms.feature.entregas.domain.entregasusecase

import android.content.Context
import androidx.sqlite.db.SimpleSQLiteQuery
import br.com.fusiondms.core.database.repository.entregas.EntregasRepository
import br.com.fusiondms.core.model.Conteudo
import br.com.fusiondms.core.model.entrega.Entrega
import br.com.fusiondms.core.model.entrega.EntregasPorCliente
import br.com.fusiondms.core.model.exceptions.ErrorAtualizarStatusEntrega
import br.com.fusiondms.core.model.parametros.Parametros
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class EntregasUseCaseImpl @Inject constructor(
    private val entregasRepository: EntregasRepository,
    private val context: Context
) : EntregasUseCase {

    override suspend fun getListaEntrega(idRomaneio: Int): Flow<List<Conteudo>> {
        return flow { //Buscar todas as entregas
            try {
                entregasRepository.getListaEntrega(idRomaneio).collect { listaEntrega ->
                    val entregasPorCliente = montarListaEntrega(listaEntrega)
                    emit(entregasPorCliente)
                }
            } catch (e: Exception) {
                emit(arrayListOf())
            }
        }
    }

    override suspend fun updateStatusEntrega(entrega: Entrega, idEvento: Int, parametros: Parametros): Flow<Int> {
        return if (entrega.statusEntrega != idEvento.toString()) {
            if (parametros.isEntregasPorCliente())
                entregasRepository.updateAllStatusEntrega(entrega.idCliente, idEvento)
            else {
                val newEntrega = entrega.copy(statusEntrega = idEvento.toString())
                entregasRepository.updateStatusEntrega(newEntrega)
            }
        } else {
            throw ErrorAtualizarStatusEntrega("A entrega já está com esse status.")
        }
    }

    private fun montarListaEntrega(listaEntrega: List<Entrega>) : ArrayList<Conteudo> {
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
                entregasPorCliente.add(
                    Conteudo.CarouselEntrega(
                        cliente.key,
                        dadosCliente
                    )
                )
            }
        return entregasPorCliente
    }
}