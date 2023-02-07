package br.com.fusiondms.moddatabase.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMirgation {
    val MIGRATION_3_TO_4 = object : Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            //Query SQL
        }

    }
}