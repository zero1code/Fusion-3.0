package br.com.fusiondms.core.databasejornadatrabalho.di

import android.content.Context
import androidx.room.Room
import br.com.fusiondms.core.databasejornadatrabalho.AppDatabase
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
    fun provideColaboradorDao(appDatabase: AppDatabase) = appDatabase.getColaboradorDto()

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "db.jornada_trabalho"
        ).addMigrations(

        ).build()
    }
}