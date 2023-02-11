package br.com.fusiondms.core.databasejornadatrabalho.di

import android.content.Context
import br.com.fusiondms.core.databasejornadatrabalho.dao.ColaboradorDao
import br.com.fusiondms.core.databasejornadatrabalho.repository.jornadatrabalho.JornadaTrabalhoRepository
import br.com.fusiondms.core.databasejornadatrabalho.repository.jornadatrabalho.JornadaTrabalhoRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {

    @Provides
    @Singleton
    fun provideJornadaTrabalhoRepository(
        colaboradorDao: ColaboradorDao,
        @ApplicationContext applicationContext: Context
    ) : JornadaTrabalhoRepository = JornadaTrabalhoRepositoryImpl(colaboradorDao, Dispatchers.IO, applicationContext)
}