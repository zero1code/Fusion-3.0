package br.com.fusiondms.moddatabase.migrations

import androidx.room.RenameColumn
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec

abstract class AutoMigration: RoomDatabase() {

    @RenameColumn(tableName = "tb_colaboradores", fromColumnName = "funcao", toColumnName = "cargo")
    class Migration2To3: AutoMigrationSpec
}