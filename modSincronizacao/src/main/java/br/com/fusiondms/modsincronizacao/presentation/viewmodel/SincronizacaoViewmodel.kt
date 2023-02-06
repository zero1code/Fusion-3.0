package br.com.fusiondms.modsincronizacao.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fusiondms.modcommon.di.DispatcherProvider
import br.com.fusiondms.modmodel.Resource
import br.com.fusiondms.modsincronizacao.domain.sincronizacaousecase.SincroniozacaoUseCase
import br.com.fusiondms.modsincronizacao.presentation.SincronizacaoFragment.Companion.MANHA
import br.com.fusiondms.modsincronizacao.presentation.SincronizacaoFragment.Companion.NOITE
import br.com.fusiondms.modsincronizacao.presentation.SincronizacaoFragment.Companion.TARDE
import dagger.hilt.android.lifecycle.HiltViewModel
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
        class Success(val resposeCode: Int) : SincronizacaoStatus()
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
            .collect {
                _sincronizacaoCompleta.emit(SincronizacaoStatus.Success(it))
            }
    }

    fun resetSincronizacaoState() = viewModelScope.launch {
        _sincronizacaoCompleta.emit(SincronizacaoStatus.Nothing)
    }

    private fun getHoraDoDia() = viewModelScope.launch {
        val calendar = Calendar.getInstance()
        when (calendar.get(Calendar.HOUR_OF_DAY)) {
            in 0..12 -> {
                _horarioDia.emit(MANHA)
            }
            in 13..18 -> {
                _horarioDia.emit(TARDE)
            }
            else -> {
                _horarioDia.emit(NOITE)
            }
        }
    }
}