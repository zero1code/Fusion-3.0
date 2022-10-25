package br.com.fusiondms.moddatabase.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import br.com.fusiondms.moddatabase.model.EntregaEntity

@Dao
interface EntregaDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun inserirEntregas(listaEntrega: List<EntregaEntity>)
}