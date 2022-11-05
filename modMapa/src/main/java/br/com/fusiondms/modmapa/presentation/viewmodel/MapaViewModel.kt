package br.com.fusiondms.modmapa.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fusiondms.modmapa.domain.entregasusecase.EntregasUseCase
import br.com.fusiondms.modmodel.Entrega
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapaViewModel @Inject constructor(
    private val entregasUseCase: EntregasUseCase
): ViewModel() {

    private var _listaEntrega = MutableStateFlow<List<Entrega>>(listOf())
    val listaEntrega: StateFlow<List<Entrega>> get() = _listaEntrega

    init {
        getListaEntrega()
    }

    private fun getListaEntrega() {
        viewModelScope.launch {
            _listaEntrega.emit(entregasUseCase.getListaEntrega())
        }
    }

}