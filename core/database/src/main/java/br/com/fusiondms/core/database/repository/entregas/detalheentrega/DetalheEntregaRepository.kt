package br.com.fusiondms.core.database.repository.entregas.detalheentrega

import br.com.fusiondms.core.model.entrega.DetalheEntrega
import br.com.fusiondms.core.model.recebimento.Recebimento
import br.com.fusiondms.core.model.recebimento.TipoPagamento
import kotlinx.coroutines.flow.Flow

interface DetalheEntregaRepository {
    suspend fun getDetalheEntrega(idRomaneio: Int, idEntrega: Int) : Flow<DetalheEntrega>
    suspend fun getListaRecebimento(idRomaneio: Int, idEntrega: Int) : Flow<List<Recebimento>>
    suspend fun inserirRecebimento(recebimento: Recebimento) : Flow<Long>
    suspend fun getFormaPagamento(formaPagamento: String) : Flow<TipoPagamento>
}