package br.com.fusiondms.modentrega.di

import android.content.Context
import br.com.fusiondms.moddatabase.repository.entregas.EntregasRepository
import br.com.fusiondms.modentrega.domain.entregasusecase.EntregasUseCase
import br.com.fusiondms.modentrega.domain.entregasusecase.EntregasUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MapaModule {

    @Singleton
    @Provides
    fun provideEntregasUseCase(
        entregasRepository: EntregasRepository,
        @ApplicationContext applicationContext: Context
    ) : EntregasUseCase = EntregasUseCaseImpl(entregasRepository, applicationContext)
}