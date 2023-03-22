package br.com.fusiondms.core.database.di

import android.content.Context
import br.com.fusiondms.core.database.dao.RomaneioDao
import br.com.fusiondms.core.database.dao.DetalheEntregaDao
import br.com.fusiondms.core.database.dao.EntregaDao
import br.com.fusiondms.core.database.dao.RecebimentoDao
import br.com.fusiondms.core.database.repository.entregas.EntregasRepository
import br.com.fusiondms.core.database.repository.entregas.EntregasRepositoryImpl
import br.com.fusiondms.core.database.repository.entregas.detalheentrega.DetalheEntregaRepository
import br.com.fusiondms.core.database.repository.entregas.detalheentrega.DetalheEntregaRepositoryImpl
import br.com.fusiondms.core.database.repository.romaneios.RomaneiosRepository
import br.com.fusiondms.core.database.repository.romaneios.RomaneiosRepositoryImpl
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
    fun provideDetalheEntregasRepository(
        detalheEntregaDao: DetalheEntregaDao,
        recebimentoDao: RecebimentoDao,
        @ApplicationContext appcontext: Context
    ) : DetalheEntregaRepository = DetalheEntregaRepositoryImpl(detalheEntregaDao, recebimentoDao, Dispatchers.IO, appcontext)

    @Provides
    @Singleton
    fun provideRomaneiosRepository(
        romaneioDao: RomaneioDao
    ) : RomaneiosRepository = RomaneiosRepositoryImpl(romaneioDao, Dispatchers.IO)
}