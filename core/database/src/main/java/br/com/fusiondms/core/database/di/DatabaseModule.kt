package br.com.fusiondms.core.database.di

import android.content.Context
import androidx.room.Room
import br.com.fusiondms.core.database.AppDatabase
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
    fun provideDetalheEntregaDao(appDatabase: AppDatabase) = appDatabase.getDetalheEntregaDao()
    @Provides
    fun provideRecebimentoDao(appDatabase: AppDatabase) = appDatabase.getRecebimentoDao()

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appcontext: Context): AppDatabase {
        return Room.databaseBuilder(
            appcontext,
            AppDatabase::class.java,
            "db.fusion"
        ).addMigrations(
//            DatabaseMirgation.MIGRATION_1_TO_2
        ).build()
    }
}