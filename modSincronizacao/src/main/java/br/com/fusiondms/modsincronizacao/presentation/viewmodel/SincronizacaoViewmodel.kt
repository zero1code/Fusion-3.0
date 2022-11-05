package br.com.fusiondms.modsincronizacao.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fusiondms.modcommon.di.DispatcherProvider
import br.com.fusiondms.modnetwork.util.Resource
import br.com.fusiondms.modsincronizacao.domain.sincronizacaousecase.SincroniozacaoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SincronizacaoViewmodel @Inject constructor(
    private val sincroniozacaoUseCase: SincroniozacaoUseCase,
    private val dispatcher: DispatcherProvider
) : ViewModel() {

    sealed class SincronizacaoStatus() {
        object Nothing : SincronizacaoStatus()
        class Loading(val isLoading: Boolean) : SincronizacaoStatus()
        class Success(val message: String?) : SincronizacaoStatus()
        class Error(val message: String?) : SincronizacaoStatus()
    }

    private var _sincronizacaoCompleta =
        MutableStateFlow<SincronizacaoStatus>(SincronizacaoStatus.Nothing)
    val sincronizacaoCompleta: StateFlow<SincronizacaoStatus> get() = _sincronizacaoCompleta

    private var _horarioDia = MutableStateFlow("")
    val horarioDia: StateFlow<String> get() = _horarioDia

    init {
        getHoraDoDia()
    }

    fun getSincronizacao() = viewModelScope.launch {
        sincroniozacaoUseCase.getSincronizacao()
            .onStart {
                _sincronizacaoCompleta.emit(SincronizacaoStatus.Loading(true))
            }
            .onCompletion {
                _sincronizacaoCompleta.emit(SincronizacaoStatus.Loading(false))
            }
            .catch { error ->
                _sincronizacaoCompleta.emit(SincronizacaoStatus.Error(error.message))
            }
            .collect { result ->
                when (result) {
                    is Resource.Error -> _sincronizacaoCompleta.emit(
                        SincronizacaoStatus.Error(
                            result.message
                        )
                    )
                    is Resource.Success -> _sincronizacaoCompleta.emit(
                        SincronizacaoStatus.Success(
                            result.message
                        )
                    )
                }
            }
    }

    fun resetSincronizacaoState() {
        viewModelScope.launch {
            _sincronizacaoCompleta.emit(SincronizacaoStatus.Nothing)
        }
    }

    private fun getHoraDoDia() = viewModelScope.launch {
        val calendar = Calendar.getInstance()
        when (calendar.get(Calendar.HOUR_OF_DAY)) {
            in 0..12 -> {
                _horarioDia.emit("MANHA")
            }
            in 13..18 -> {
                _horarioDia.emit("TARDE")
            }
            else -> {
                _horarioDia.emit("NOITE")
            }
        }
    }
}