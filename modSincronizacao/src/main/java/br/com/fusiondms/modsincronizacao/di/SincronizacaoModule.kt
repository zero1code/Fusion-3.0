package br.com.fusiondms.modsincronizacao.di

import br.com.fusiondms.modnetwork.repository.sincronizacao.SincronizacaoRepository
import br.com.fusiondms.modsincronizacao.domain.sincronizacaousecase.SincroniozacaoUseCase
import br.com.fusiondms.modsincronizacao.domain.sincronizacaousecase.SincronizacaoUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SincronizacaoModule {

    @Singleton
    @Provides
    fun provideSincronizacaoUseCase(
        sincronizacaoRepository: SincronizacaoRepository
    ) : SincroniozacaoUseCase = SincronizacaoUseCaseImpl(sincronizacaoRepository)
}