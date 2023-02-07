package br.com.fusiondms.modjornadatrabalho.di

import android.content.Context
import br.com.fusiondms.moddatabase.repository.jornadatrabalho.JornadaTrabalhoRepository
import br.com.fusiondms.modjornadatrabalho.domain.jornadatrabalhousecase.JornadaTrabalhoUseCase
import br.com.fusiondms.modjornadatrabalho.domain.jornadatrabalhousecase.JornadaTrabalhoUseCaseimpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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