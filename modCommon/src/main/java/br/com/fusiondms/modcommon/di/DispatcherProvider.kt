package br.com.fusiondms.di

import kotlinx.coroutines.CoroutineDispatcher

interface DispatcherProvider {
    val MAIN: CoroutineDispatcher
    val IO: CoroutineDispatcher
    val DEFAULT: CoroutineDispatcher
    val UNCONFINED: CoroutineDispatcher
}