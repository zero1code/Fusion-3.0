package br.com.fusiondms.feature.entregas.domain.detalheentregausecase

import br.com.fusiondms.core.database.repository.entregas.detalheentrega.DetalheEntregaRepository
import br.com.fusiondms.core.model.Conteudo
import br.com.fusiondms.core.model.entrega.DetalheEntrega
import br.com.fusiondms.core.model.recebimento.Recebimento
import br.com.fusiondms.core.model.recebimento.TipoPagamento
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class DetalheEntregaUseCaseImpl @Inject constructor(
    private val detalheEntregaRepository: DetalheEntregaRepository
) : DetalheEntregaUseCase {
    override suspend fun getDetalheEntrega(idRomaneio: Int, idEntrega: Int): Flow<DetalheEntrega> {
        return detalheEntregaRepository.getDetalheEntrega(idRomaneio, idEntrega)
    }

    override suspend fun getListaRecebimento(
        idRomaneio: Int,
        idEntrega: Int
    ): Flow<List<Recebimento>> {
        return detalheEntregaRepository.getListaRecebimento(idRomaneio, idEntrega)
    }

    override suspend fun inserirRecebimento(recebimento: Recebimento): Flow<Long> {
        return detalheEntregaRepository.inserirRecebimento(recebimento)
    }

    override suspend fun getFormaPagamento(formaPagamento: String): Flow<TipoPagamento> {
        return detalheEntregaRepository.getFormaPagamento(formaPagamento)
    }

}