package br.com.fusiondms.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.fusiondms.core.database.model.romaneio.RomaneioEntity

@Dao
interface RomaneioDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun inserirRomaneios(listaRomaneio: List<RomaneioEntity>)

    @Delete
    suspend fun deletarRomaneio(romaneio: RomaneioEntity) : Int

    @Query("SELECT * FROM tb_romaneio")
    suspend fun getListaRomaneio() : List<RomaneioEntity>
}