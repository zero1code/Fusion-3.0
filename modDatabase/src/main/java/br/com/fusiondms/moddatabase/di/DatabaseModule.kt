package br.com.fusiondms.moddatabase.di

import android.content.Context
import androidx.room.Room
import br.com.fusiondms.moddatabase.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Provides
    fun provideRomaneioDao(appDatabase: AppDatabase) = appDatabase.getRomaneioDao()

    @Provides
    fun provideEntregaDao(appDatabase: AppDatabase) = appDatabase.getEntregaDao()

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appcontext: Context): AppDatabase {
        return Room.databaseBuilder(
            appcontext,
            AppDatabase::class.java,
            "db.fusion"
        ).build()
    }
}