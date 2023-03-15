package br.com.fusiondms.core.network.di

import br.com.fusiondms.core.network.api.FusionApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

private const val BASE_URL = "http://192.168.1.6:3002/"

@Module
@InstallIn(SingletonComponent::class)
object Networkmodule {

    @Singleton
    @Provides
    fun provideFusionApi(): FusionApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(FusionApi::class.java)
}