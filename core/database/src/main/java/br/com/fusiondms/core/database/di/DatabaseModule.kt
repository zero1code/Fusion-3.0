package br.com.fusiondms.moddatabase.di

import android.content.Context
import androidx.room.Room
import br.com.fusiondms.moddatabase.AppDatabase
import br.com.fusiondms.moddatabase.migrations.DatabaseMirgation
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides
    fun provideRomaneioDao(appDatabase: AppDatabase) = appDatabase.getRomaneioDao()

    @Provides
    fun provideEntregaDao(appDatabase: AppDatabase) = appDatabase.getEntregaDao()

    @Provides
    fun provideColaboradorDao(appDatabase: AppDatabase) = appDatabase.getColaboradorDto()

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appcontext: Context): AppDatabase {
        return Room.databaseBuilder(
            appcontext,
            AppDatabase::class.java,
            "db.fusion"
        ).addMigrations(
//            DatabaseMirgation.MIGRATION_3_TO_4
        ).build()
    }
}