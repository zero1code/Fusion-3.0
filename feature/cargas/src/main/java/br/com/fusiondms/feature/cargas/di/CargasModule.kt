package br.com.fusiondms.feature.cargas.di

import br.com.fusiondms.core.database.repository.romaneios.RomaneiosRepository
import br.com.fusiondms.core.network.repository.recusarromaneio.RecusarRomaneioRepository
import br.com.fusiondms.feature.cargas.domain.cargasusecase.CargasUseCase
import br.com.fusiondms.feature.cargas.domain.cargasusecase.CargasUseCaseImpl
import br.com.fusiondms.feature.cargas.domain.recusarcargausecase.RecusarCargaUseCase
import br.com.fusiondms.feature.cargas.domain.recusarcargausecase.RecusarCargaUseCaseImpl
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
        romaneiosRepository: RomaneiosRepository
    ) : CargasUseCase = CargasUseCaseImpl(romaneiosRepository)

    @Singleton
    @Provides
    fun provideRecusarCargaUseCase(
        recusarRomaneioRepository: RecusarRomaneioRepository
    ) : RecusarCargaUseCase = RecusarCargaUseCaseImpl(recusarRomaneioRepository)
}