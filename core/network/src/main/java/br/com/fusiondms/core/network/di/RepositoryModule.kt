package br.com.fusiondms.core.network.di

import android.content.Context
import br.com.fusiondms.core.database.AppDatabase
import br.com.fusiondms.core.database.dao.CargaDao
import br.com.fusiondms.core.network.api.FusionApi
import br.com.fusiondms.core.network.repository.recusarcarga.RecusarCargaRepository
import br.com.fusiondms.core.network.repository.recusarcarga.RecusarCargaRepositoryImpl
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
        appDatabase: AppDatabase
    ): SincronizacaoRepository = SincronizacaoRepositoryImpl(fusionApi, appDatabase, Dispatchers.IO)

    @Singleton
    @Provides
    fun provideRecusarCarga(
        fusionApi: FusionApi,
        cargaDao: CargaDao,
        @ApplicationContext context: Context
    ) : RecusarCargaRepository = RecusarCargaRepositoryImpl(fusionApi, cargaDao, Dispatchers.IO, context)
}