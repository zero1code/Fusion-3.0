package br.com.fusiondms.moddatabase.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.fusiondms.moddatabase.model.RomaneioEntity

@Dao
interface RomaneioDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun inserirRomaneios(listaRomaneio: List<RomaneioEntity>)

    @Query("SELECT * FROM tb_romaneios")
    suspend fun getListaRomaneio() : List<RomaneioEntity>
}