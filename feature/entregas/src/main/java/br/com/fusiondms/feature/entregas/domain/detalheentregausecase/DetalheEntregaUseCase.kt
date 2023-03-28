package br.com.fusiondms.feature.entregas.domain.detalheentregausecase

import br.com.fusiondms.core.model.Conteudo
import br.com.fusiondms.core.model.entrega.DetalheEntrega
import br.com.fusiondms.core.model.recebimento.Recebimento
import br.com.fusiondms.core.model.recebimento.TipoPagamento
import kotlinx.coroutines.flow.Flow

interface DetalheEntregaUseCase {
    suspend fun getDetalheEntrega(idRomaneio: Int, idEntrega: Int) : Flow<DetalheEntrega>
    suspend fun getListaRecebimento(idRomaneio: Int, idEntrega: Int) : Flow<List<Recebimento>>
    suspend fun inserirRecebimento(recebimento: Recebimento) : Flow<Long>
    suspend fun getFormaPagamento(formaPagamento: String) : Flow<TipoPagamento>
}