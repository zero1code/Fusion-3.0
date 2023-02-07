package br.com.fusiondms.feature.sincronizacao.di

import br.com.fusiondms.core.network.repository.sincronizacao.SincronizacaoRepository
import br.com.fusiondms.feature.sincronizacao.domain.sincronizacaousecase.SincroniozacaoUseCase
import br.com.fusiondms.feature.sincronizacao.domain.sincronizacaousecase.SincronizacaoUseCaseImpl
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