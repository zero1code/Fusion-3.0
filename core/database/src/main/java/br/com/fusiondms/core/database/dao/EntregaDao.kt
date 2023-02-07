package br.com.fusiondms.moddatabase.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import br.com.fusiondms.moddatabase.model.entrega.EntregaEntity

@Dao
interface EntregaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirEntregas(listaEntrega: List<EntregaEntity>)

    @Query("SELECT * FROM tb_entregas WHERE idRomaneio = :idRomaneio")
    suspend fun getListaEntrega(idRomaneio: Int) : List<EntregaEntity>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateStatusEntrega(entregaEntity: EntregaEntity): Int
}