package br.com.fusiondms.feature.jornadatrabalho.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fusiondms.core.model.jornadatrabalho.Colaborador
import br.com.fusiondms.core.model.jornadatrabalho.JornadaTrabalho
import br.com.fusiondms.core.model.jornadatrabalho.ReciboRegistroPonto
import br.com.fusiondms.core.model.jornadatrabalho.RegistroPonto
import br.com.fusiondms.feature.jornadatrabalho.domain.jornadatrabalhousecase.JornadaTrabalhoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class JornadaTrabalhoViewModel @Inject constructor(
    private val jornadaTrabalhoUseCase: JornadaTrabalhoUseCase
) : ViewModel() {

    sealed class JornadaStatus() {
        object Nothing : JornadaStatus()
        object Empty : JornadaStatus()
        class Selected(val colaborador: Colaborador) : JornadaStatus()
        object SuccessCadastro : JornadaStatus()
        object SuccessRegistroPonto : JornadaStatus()
        class SuccessListaRegistro(val listaRegistroPonto: List<JornadaTrabalho>) : JornadaStatus()
        class SuccessListaRecibo(val listaRecibo: List<ReciboRegistroPonto>) : JornadaStatus()
        class ErroRegistro(val message: String) : JornadaStatus()
        class ErroRecibo(val message: String) : JornadaStatus()
    }

    private var _horaAtual: MutableStateFlow<Long> = MutableStateFlow(Instant.now().epochSecond)
    val horaAtual: StateFlow<Long> get() = _horaAtual

    private var _registrosPonto: MutableStateFlow<JornadaStatus> = MutableStateFlow(JornadaStatus.Nothing)
    val registrosPonto: StateFlow<JornadaStatus> get() = _listaReccibo

    private var _colaboradorSelecionado: MutableStateFlow<JornadaStatus> = MutableStateFlow(
        JornadaStatus.Nothing
    )
    val colaboradorSelecionado : StateFlow<JornadaStatus> get() = _colaboradorSelecionado

    private var _cadastrarColaborador: MutableStateFlow<JornadaStatus> = MutableStateFlow(
        JornadaStatus.Nothing
    )
    val cadastrarColaborador : StateFlow<JornadaStatus> get() = _cadastrarColaborador

    private var _registrarPonto: MutableStateFlow<JornadaStatus> = MutableStateFlow(
        JornadaStatus.Nothing
    )
    val registrarPonto : StateFlow<JornadaStatus> get() = _registrarPonto

    private var _listaReccibo: MutableStateFlow<JornadaStatus> = MutableStateFlow(JornadaStatus.Nothing)
    val listaReccibo: StateFlow<JornadaStatus> get() = _listaReccibo

    fun getRegistroPonto() =
        viewModelScope.launch {
            jornadaTrabalhoUseCase.getRegistrosPonto(_horaAtual.value)
                .onStart {
                }
                .onCompletion {
                }
                .catch {
                }
                .collect {
                    _listaReccibo.emit(JornadaStatus.SuccessListaRegistro(it))
                }

        }

    fun setColaboradorSelecionado(colaborador: Colaborador) =
        viewModelScope.launch {
            _colaboradorSelecionado.emit(JornadaStatus.Selected(colaborador))
        }

    fun inserirColaborador(colaborador: Colaborador) =
        viewModelScope.launch {
            jornadaTrabalhoUseCase.inserirColaborador(colaborador)
                .onStart {

                }
                .onCompletion {

                }
                .catch {

                }
                .collect { result ->
                    if (result == -2L) {
                        _cadastrarColaborador.emit(JornadaStatus.ErroRegistro("Esse colaborador já está cadastrado."))
                    } else {
                        _cadastrarColaborador.emit(JornadaStatus.SuccessCadastro)
                    }
                }
        }

    fun inserirRegistroPonto(registroPonto: RegistroPonto) =
        viewModelScope.launch {
            jornadaTrabalhoUseCase.inserirRegistroPonto(registroPonto)
                .onStart {
                }
                .onCompletion {
                }
                .catch {
                }
                .collect { result ->
                    if (result > 0) {
                        _registrarPonto.emit(JornadaStatus.SuccessRegistroPonto)
                    }
                }
        }

    fun getListaRecibo() =
        viewModelScope.launch {
            jornadaTrabalhoUseCase.getRecibos()
                .onStart {
                }
                .onCompletion {
                }
                .catch {
                }
                .collect { lista ->
                    _listaReccibo.emit(JornadaStatus.SuccessListaRecibo(lista))
                }
        }

    fun atualizarData() =
        viewModelScope.launch {
        _horaAtual.emit(Instant.now().epochSecond)
//        _horaAtual.emit(Instant.now().toEpochMilli())
    }

    fun resetJornadaState() =
        viewModelScope.launch {
            _cadastrarColaborador.emit(JornadaStatus.Nothing)
            _registrarPonto.emit(JornadaStatus.Nothing)
        }
}