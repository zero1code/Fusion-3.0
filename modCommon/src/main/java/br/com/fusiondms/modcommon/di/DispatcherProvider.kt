package br.com.fusiondms.modcommon.di

import kotlinx.coroutines.CoroutineDispatcher

interface DispatcherProvider {
    val MAIN: CoroutineDispatcher
    val IO: CoroutineDispatcher
    val DEFAULT: CoroutineDispatcher
    val UNCONFINED: CoroutineDispatcher
}