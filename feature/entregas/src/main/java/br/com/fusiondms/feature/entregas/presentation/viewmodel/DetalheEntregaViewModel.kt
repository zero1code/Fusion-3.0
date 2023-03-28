package br.com.fusiondms.feature.entregas.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fusiondms.core.model.entrega.DetalheEntrega
import br.com.fusiondms.core.model.entrega.Entrega
import br.com.fusiondms.core.model.recebimento.Recebimento
import br.com.fusiondms.core.model.recebimento.TipoPagamento
import br.com.fusiondms.feature.entregas.domain.detalheentregausecase.DetalheEntregaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetalheEntregaViewModel @Inject constructor(
    private val detalheEntregaUseCase: DetalheEntregaUseCase
) : ViewModel() {

    sealed class Status() {
        object Nothing : Status()
        class Loading(val isLoading: Boolean) : Status()
        class Error(val message: String?) : Status()
        class Success(val detalheEntrega: DetalheEntrega) : Status()
        class SuccessTipoPagamento(val tipoPagamento: TipoPagamento) : Status()
        class SuccessListaRecebimento(val listaRecebimento: List<Recebimento>) : Status()
        object SuccessRecebimento : Status()
    }

    private var _detalheEntrega = MutableStateFlow<Status>(Status.Nothing)
    val detalheEntrega: StateFlow<Status> get() = _detalheEntrega

    private var _listaRecebimento = MutableStateFlow<Status>(Status.Nothing)
    val listaRecebimento: StateFlow<Status> get() = _listaRecebimento

    private var _recebimento = MutableStateFlow<Status>(Status.Nothing)
    val recebimento: StateFlow<Status> get() = _recebimento

    private var _tipoPagamento = MutableStateFlow<Status>(Status.Nothing)
    val tipoPagamento: StateFlow<Status> get() = _tipoPagamento

    fun getDetalheEntrega(entrega: Entrega) =
        viewModelScope.launch {
            detalheEntregaUseCase.getDetalheEntrega(entrega.idRomaneio, entrega.idEntrega)
                .onStart {

                }
                .onCompletion {

                }
                .catch {

                }
                .collect { detalheEntrega ->
                    _detalheEntrega.emit(Status.Success(detalheEntrega))
                }
        }

    fun getListaRecebimento(entrega: Entrega) =
        viewModelScope.launch {
            detalheEntregaUseCase.getListaRecebimento(entrega.idRomaneio, entrega.idEntrega)
                .onStart {
                    _recebimento.emit(Status.Nothing)
                }
                .onCompletion {

                }
                .catch {

                }
                .collect { lista ->
                    _listaRecebimento.emit(Status.SuccessListaRecebimento(lista))
                }
        }

    fun getFormaPagamento(formaPagamento: String) =
        viewModelScope.launch {
            detalheEntregaUseCase.getFormaPagamento(formaPagamento)
                .onStart {

                }
                .onCompletion {

                }
                .catch {

                }
                .collect { tipoPagamento ->
                    _tipoPagamento.emit(Status.SuccessTipoPagamento(tipoPagamento))
                }
        }
    fun inserirRecebimento(recebimento: Recebimento) =
        viewModelScope.launch {
            detalheEntregaUseCase.inserirRecebimento(recebimento)
                .onStart {

                }
                .onCompletion {

                }
                .catch { error ->
                    _recebimento.emit(Status.Error(error.message))
                }
                .collect {
                    _recebimento.emit(Status.SuccessRecebimento)
                }
        }

    fun resetViewModel() =
        viewModelScope.launch {
            _listaRecebimento.emit(Status.Nothing)
            _tipoPagamento.emit(Status.Nothing)
        }
}