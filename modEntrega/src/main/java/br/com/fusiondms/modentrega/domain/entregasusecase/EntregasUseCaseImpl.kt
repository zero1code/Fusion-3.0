package br.com.fusiondms.modentrega.domain.entregasusecase

import android.content.Context
import br.com.fusiondms.modcommon.R.*
import br.com.fusiondms.moddatabase.repository.entregas.EntregasRepository
import br.com.fusiondms.modentrega.R
import br.com.fusiondms.modmodel.Conteudo
import br.com.fusiondms.modmodel.Entrega
import br.com.fusiondms.modmodel.EntregasPorCliente
import br.com.fusiondms.modmodel.Resource
import kotlinx.coroutines.flow.*
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

    override suspend fun updateStatusEntrega(entrega: Entrega, idEvento: Int): Flow<Resource<Int>> {
        return flow {
            if (entrega.statusEntrega != idEvento.toString()) {
                val newEntrega = entrega.copy(statusEntrega = idEvento.toString())
                entregasRepository.updateStatusEntrega(newEntrega).collect { result ->
                    emit(result)
                }

            } else emit(Resource.Error(context.getString(string.label_entrega_mesmo_status), -2))
        }
    }
}