package br.com.fusiondms.moddatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import br.com.fusiondms.moddatabase.dao.CargaDao
import br.com.fusiondms.moddatabase.dao.ColaboradorDao
import br.com.fusiondms.moddatabase.dao.EntregaDao
import br.com.fusiondms.moddatabase.model.entrega.EntregaEntity
import br.com.fusiondms.moddatabase.model.jornadatrabalho.ColaboradorEntity
import br.com.fusiondms.moddatabase.model.jornadatrabalho.RegistroPontoEntity
import br.com.fusiondms.moddatabase.model.romaneio.RomaneioEntity

@Database(
    entities = [RomaneioEntity::class, EntregaEntity::class, ColaboradorEntity::class, RegistroPontoEntity::class],
    version = 1,
    autoMigrations = [
//        AutoMigration(from = 1, to = 2),
//        AutoMigration(from = 2, to = 3, spec = Migration2To3::class) // add spec caso tenha exclusão ou alteracao de nome de tabela/coluna
    ]
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getRomaneioDao() : CargaDao
    abstract fun getEntregaDao() : EntregaDao
    abstract fun getColaboradorDto() : ColaboradorDao
}