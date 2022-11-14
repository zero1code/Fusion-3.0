package br.com.fusiondms.moddatabase.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.fusiondms.moddatabase.model.EntregaEntity

@Dao
interface EntregaDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun inserirEntregas(listaEntrega: List<EntregaEntity>)

    @Query("SELECT * FROM tb_entregas WHERE idRomaneio = :idRomaneio")
    suspend fun getListaEntrega(idRomaneio: Int) : List<EntregaEntity>
}