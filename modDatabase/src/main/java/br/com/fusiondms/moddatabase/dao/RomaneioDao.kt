package br.com.fusiondms.moddatabase.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import br.com.fusiondms.moddatabase.model.RomaneioEntity

@Dao
interface RomaneioDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun inserirRomaneio(listaRomaneio: List<RomaneioEntity>)
}