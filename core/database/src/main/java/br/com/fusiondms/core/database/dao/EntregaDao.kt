package br.com.fusiondms.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import br.com.fusiondms.core.database.model.entrega.EntregaEntity
import br.com.fusiondms.core.database.model.entrega.EntregaItemEntity

@Dao
interface EntregaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirEntregas(listaEntrega: List<EntregaEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirEntregasItens(listaEntregaItem: List<EntregaItemEntity>)

    @Query("SELECT * FROM tb_entrega WHERE idRomaneio = :idRomaneio")
    suspend fun getListaEntrega(idRomaneio: Int) : List<EntregaEntity>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateStatusEntrega(entregaEntity: EntregaEntity): Int
}