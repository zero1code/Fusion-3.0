package br.com.fusiondms.moddatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import br.com.fusiondms.moddatabase.dao.EntregaDao
import br.com.fusiondms.moddatabase.dao.CargaDao
import br.com.fusiondms.moddatabase.model.EntregaEntity
import br.com.fusiondms.moddatabase.model.RomaneioEntity

@Database(entities = [RomaneioEntity::class, EntregaEntity::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getRomaneioDao() : CargaDao
    abstract fun getEntregaDao() : EntregaDao
}