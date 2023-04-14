package br.com.fusiondms.core.database.repository.entregas

import androidx.sqlite.db.SimpleSQLiteQuery
import br.com.fusiondms.core.model.entrega.Entrega
import br.com.fusiondms.core.model.entrega.EntregasPorCliente
import br.com.fusiondms.core.model.entrega.Evento
import kotlinx.coroutines.flow.Flow

interface EntregasRepository {
    suspend fun getListaEntrega(idRomaneio: Int) : Flow<List<Entrega>>
    suspend fun updateStatusEntrega(entrega: Entrega) : Flow<Int>
    suspend fun updateAllStatusEntrega(idCliente: Int, idEvento: Int) : Flow<Int>
}