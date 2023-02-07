package br.com.fusiondms.moddatastore.di

import android.content.Context
import br.com.fusiondms.moddatastore.repository.DataStoreRepository
import br.com.fusiondms.moddatastore.repository.DataStoreRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Singleton
    @Provides
    fun provideDataStoreRepository(
        @ApplicationContext applicationContext: Context
    ) : DataStoreRepository = DataStoreRepositoryImpl(applicationContext)
}