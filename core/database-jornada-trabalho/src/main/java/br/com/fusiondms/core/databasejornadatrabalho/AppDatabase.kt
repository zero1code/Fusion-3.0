package br.com.fusiondms.core.databasejornadatrabalho

import androidx.room.Database
import androidx.room.RoomDatabase
import br.com.fusiondms.core.databasejornadatrabalho.dao.ColaboradorDao
import br.com.fusiondms.core.databasejornadatrabalho.model.jornadatrabalho.ColaboradorEntity
import br.com.fusiondms.core.databasejornadatrabalho.model.jornadatrabalho.RegistroPontoEntity

@Database(
    entities = [ColaboradorEntity::class, RegistroPontoEntity::class],
    version = 1,
    autoMigrations = []
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getColaboradorDto() : ColaboradorDao
}