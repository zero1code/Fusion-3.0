package br.com.fusiondms.modmapa.di

import br.com.fusiondms.moddatabase.repository.entregas.EntregasRepository
import br.com.fusiondms.modmapa.domain.entregasusecase.EntregasUseCase
import br.com.fusiondms.modmapa.domain.entregasusecase.EntregasUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MapaModule {

    @Singleton
    @Provides
    fun provideEntregasUseCase(
        entregasRepository: EntregasRepository
    ) : EntregasUseCase = EntregasUseCaseImpl(entregasRepository)
}