package br.com.fusiondms.feature.jornadatrabalho.di

import br.com.fusiondms.core.databasejornadatrabalho.repository.jornadatrabalho.JornadaTrabalhoRepository
import br.com.fusiondms.feature.jornadatrabalho.domain.jornadatrabalhousecase.JornadaTrabalhoUseCase
import br.com.fusiondms.feature.jornadatrabalho.domain.jornadatrabalhousecase.JornadaTrabalhoUseCaseimpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object JornadaTrabalhoModule {

    @Singleton
    @Provides
    fun provideJornadaTrabalhoUseCase(
        jornadaTrabalhoRepository: JornadaTrabalhoRepository,
    ) : JornadaTrabalhoUseCase = JornadaTrabalhoUseCaseimpl(jornadaTrabalhoRepository)
}