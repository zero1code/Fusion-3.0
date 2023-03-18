package br.com.fusiondms.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import br.com.fusiondms.core.database.model.entrega.EntregaItemEntity

@Dao
interface DetalheEntregaDao {

    @Query("SELECT * FROM tb_entrega_item WHERE idRomaneio = :idRomaneio AND idEntrega = :idEntrega")
    suspend fun getEntregaItem(idRomaneio: Int, idEntrega: Int): List<EntregaItemEntity>
}