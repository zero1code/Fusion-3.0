package br.com.fusiondms.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.fusiondms.core.database.model.romaneio.RomaneioEntity

@Dao
interface CargaDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun inserirCargas(listaCarga: List<RomaneioEntity>)

    @Delete
    suspend fun deleteCarga(romaneio: RomaneioEntity) : Int

    @Query("SELECT * FROM tb_romaneios")
    suspend fun getListaCarga() : List<RomaneioEntity>
}