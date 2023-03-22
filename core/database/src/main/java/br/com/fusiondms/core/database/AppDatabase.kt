package br.com.fusiondms.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import br.com.fusiondms.core.database.dao.DetalheEntregaDao
import br.com.fusiondms.core.database.dao.EntregaDao
import br.com.fusiondms.core.database.dao.RecebimentoDao
import br.com.fusiondms.core.database.dao.RomaneioDao
import br.com.fusiondms.core.database.model.entrega.EntregaEntity
import br.com.fusiondms.core.database.model.entrega.EntregaItemEntity
import br.com.fusiondms.core.database.model.recebimento.RecebimentoEntity
import br.com.fusiondms.core.database.model.recebimento.TipoPagamentoEntity
import br.com.fusiondms.core.database.model.romaneio.RomaneioEntity

@Database(
    entities = [RomaneioEntity::class, EntregaEntity::class, EntregaItemEntity::class, RecebimentoEntity::class, TipoPagamentoEntity::class],
    version = 1,
    autoMigrations = [
//        AutoMigration(from = 1, to = 2),
//        AutoMigration(from = 2, to = 3, spec = Migration2To3::class) // add spec caso tenha exclusão ou alteracao de nome de tabela/coluna
    ]
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getRomaneioDao() : RomaneioDao
    abstract fun getEntregaDao() : EntregaDao
    abstract fun getDetalheEntregaDao() : DetalheEntregaDao
    abstract fun getRecebimentoDao() : RecebimentoDao
}