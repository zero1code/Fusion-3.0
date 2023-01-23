package br.com.fusiondms.moddatabase.migrations

import androidx.room.RoomDatabase

abstract class DatabaseAutoMigration: RoomDatabase() {

//    @RenameColumn(tableName = "tb_colaboradores", fromColumnName = "funcao", toColumnName = "cargo")
//    class Migration2To3: AutoMigrationSpec
}