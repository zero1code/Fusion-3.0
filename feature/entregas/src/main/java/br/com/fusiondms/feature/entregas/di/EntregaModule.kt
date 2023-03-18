package br.com.fusiondms.feature.entregas.di

import android.content.Context
import br.com.fusiondms.core.database.repository.entregas.EntregasRepository
import br.com.fusiondms.core.database.repository.entregas.detalheentrega.DetalheEntregaRepository
import br.com.fusiondms.feature.entregas.domain.detalheentregausecase.DetalheEntregaUseCase
import br.com.fusiondms.feature.entregas.domain.detalheentregausecase.DetalheEntregaUseCaseImpl
import br.com.fusiondms.feature.entregas.domain.entregasusecase.EntregasUseCase
import br.com.fusiondms.feature.entregas.domain.entregasusecase.EntregasUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EntregaModule {

    @Singleton
    @Provides
    fun provideEntregasUseCase(
        entregasRepository: EntregasRepository,
        @ApplicationContext applicationContext: Context
    ) : EntregasUseCase = EntregasUseCaseImpl(entregasRepository, applicationContext)

    @Singleton
    @Provides
    fun provideDetalheEntregaUseCase(
        detalheEntregaRepository: DetalheEntregaRepository
    ) : DetalheEntregaUseCase = DetalheEntregaUseCaseImpl(detalheEntregaRepository)
}