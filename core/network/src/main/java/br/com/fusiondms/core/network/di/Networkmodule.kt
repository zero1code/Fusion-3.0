package br.com.fusiondms.modnetwork.di

import android.app.Application
import android.content.Context
import br.com.fusiondms.moddatabase.AppDatabase
import br.com.fusiondms.modnetwork.api.FusionApi
import br.com.fusiondms.modnetwork.repository.recusarcarga.RecusarCargaRepositoryImpl
import br.com.fusiondms.modnetwork.repository.recusarcarga.RecusarCargaRepository
import br.com.fusiondms.modnetwork.repository.sincronizacao.SincronizacaoRepository
import br.com.fusiondms.modnetwork.repository.sincronizacao.SincronizacaoRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

private const val BASE_URL = "http://192.168.1.6:3002/"

@Module
@InstallIn(SingletonComponent::class)
object Networkmodule {

    @Singleton
    @Provides
    fun provideContext(application: Application): Context = application.applicationContext

    @Singleton
    @Provides
    fun provideFusionApi(): FusionApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(FusionApi::class.java)

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
        appDatabase: AppDatabase,
        context: Context
    ) : RecusarCargaRepository = RecusarCargaRepositoryImpl(fusionApi, appDatabase, Dispatchers.IO, context)
}