package br.com.fusiondms.moddatabase.di

import android.content.Context
import br.com.fusiondms.moddatabase.dao.CargaDao
import br.com.fusiondms.moddatabase.dao.ColaboradorDao
import br.com.fusiondms.moddatabase.dao.EntregaDao
import br.com.fusiondms.moddatabase.repository.entregas.EntregasRepository
import br.com.fusiondms.moddatabase.repository.entregas.EntregasRepositoryImpl
import br.com.fusiondms.moddatabase.repository.jornadatrabalho.JornadaTrabalhoRepository
import br.com.fusiondms.moddatabase.repository.jornadatrabalho.JornadaTrabalhoRepositoryImpl
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
object RepositoryModule {

    @Provides
    @Singleton
    fun provideEntregasRepository(
        entregaDao: EntregaDao,
        @ApplicationContext appcontext: Context
    ) : EntregasRepository = EntregasRepositoryImpl(entregaDao, Dispatchers.IO, appcontext)

    @Provides
    @Singleton
    fun provideCargasRepository(
        cargaDao: CargaDao
    ) : CargasRepository = CargasRepositoryImpl(cargaDao, Dispatchers.IO)

    @Provides
    @Singleton
    fun provideJornadaTrabalhoRepository(
        colaboradorDao: ColaboradorDao,
        @ApplicationContext applicationContext: Context
    ) : JornadaTrabalhoRepository = JornadaTrabalhoRepositoryImpl(colaboradorDao, Dispatchers.IO, applicationContext)
}