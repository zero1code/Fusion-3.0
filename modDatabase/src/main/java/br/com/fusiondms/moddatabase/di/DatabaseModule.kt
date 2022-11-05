package br.com.fusiondms.moddatabase.di

import android.content.Context
import androidx.room.Room
import br.com.fusiondms.moddatabase.AppDatabase
import br.com.fusiondms.moddatabase.dao.EntregaDao
import br.com.fusiondms.moddatabase.dao.CargaDao
import br.com.fusiondms.moddatabase.repository.entregas.EntregasRepository
import br.com.fusiondms.moddatabase.repository.entregas.EntregasRepositoryImpl
import br.com.fusiondms.moddatabase.repository.romaneios.CargasRepository
import br.com.fusiondms.moddatabase.repository.romaneios.CargasRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
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
    fun provideEntregasRepository(
        entregaDao: EntregaDao
    ) : EntregasRepository = EntregasRepositoryImpl(entregaDao, Dispatchers.IO)

    @Provides
    @Singleton
    fun provideCargasRepository(
        cargaDao: CargaDao
    ) : CargasRepository = CargasRepositoryImpl(cargaDao, Dispatchers.IO)

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