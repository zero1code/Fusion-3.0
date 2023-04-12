package br.com.fusiondms.core.network.di

import android.content.Context
import br.com.fusiondms.core.database.AppDatabase
import br.com.fusiondms.core.database.dao.RomaneioDao
import br.com.fusiondms.core.datastore.repository.DataStoreRepository
import br.com.fusiondms.core.network.api.FusionApi
import br.com.fusiondms.core.network.repository.recusarromaneio.RecusarRomaneioRepository
import br.com.fusiondms.core.network.repository.recusarromaneio.RecusarRomaneioRepositoryImpl
import br.com.fusiondms.core.network.repository.sincronizacao.SincronizacaoRepository
import br.com.fusiondms.core.network.repository.sincronizacao.SincronizacaoRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideSincronizacaoRepository(
        fusionApi: FusionApi,
        appDatabase: AppDatabase,
        datastoreRepository: DataStoreRepository
    ): SincronizacaoRepository = SincronizacaoRepositoryImpl(fusionApi, appDatabase, datastoreRepository, Dispatchers.IO)

    @Singleton
    @Provides
    fun provideRecusarCarga(
        fusionApi: FusionApi,
        romaneioDao: RomaneioDao,
        @ApplicationContext context: Context
    ) : RecusarRomaneioRepository = RecusarRomaneioRepositoryImpl(fusionApi, romaneioDao, Dispatchers.IO, context)
}