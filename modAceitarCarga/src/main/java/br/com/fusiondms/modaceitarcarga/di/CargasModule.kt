package br.com.fusiondms.modaceitarcarga.di

import br.com.fusiondms.modaceitarcarga.domain.cargasusecase.CargasUseCase
import br.com.fusiondms.modaceitarcarga.domain.cargasusecase.CargasUseCaseImpl
import br.com.fusiondms.modaceitarcarga.domain.recusarcargausecase.RecusarCargaUseCase
import br.com.fusiondms.modaceitarcarga.domain.recusarcargausecase.RecusarCargaUseCaseImpl
import br.com.fusiondms.moddatabase.repository.romaneios.CargasRepository
import br.com.fusiondms.modnetwork.repository.recusarcarga.RecusarCargaRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CargasModule {

    @Singleton
    @Provides
    fun provideCargasUseCase(
        cargasRepository: CargasRepository
    ) : CargasUseCase = CargasUseCaseImpl(cargasRepository)

    @Singleton
    @Provides
    fun provideRecusarCargaUseCase(
        recusarCargaRepository: RecusarCargaRepository
    ) : RecusarCargaUseCase = RecusarCargaUseCaseImpl(recusarCargaRepository)
}