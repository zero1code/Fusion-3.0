package br.com.fusiondms.feature.entregas.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fusiondms.core.model.entrega.DetalheEntrega
import br.com.fusiondms.core.model.entrega.Entrega
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
        class Error(val message: String) : Status()
        class Success(val detalheEntrega: DetalheEntrega) : Status()
    }

    private var _detalheEntrega = MutableStateFlow<Status>(Status.Nothing)
    val detalheEntrega: StateFlow<Status> get() = _detalheEntrega

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
}