package br.com.fusiondms.modcommon.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CommonModule {

    @Singleton
    @Provides
    fun providerDispatchers() : DispatcherProvider = object : DispatcherProvider {
        override val MAIN: CoroutineDispatcher
            get() = Dispatchers.Main
        override val IO: CoroutineDispatcher
            get() = Dispatchers.IO
        override val DEFAULT: CoroutineDispatcher
            get() = Dispatchers.Default
        override val UNCONFINED: CoroutineDispatcher
            get() = Dispatchers.Unconfined

    }
}