package br.com.fusiondms.feature.entregas.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fusiondms.core.datastore.chaves.DataStoreChaves
import br.com.fusiondms.core.datastore.repository.DataStoreRepository
import br.com.fusiondms.core.model.Conteudo
import br.com.fusiondms.core.model.entrega.Entrega
import br.com.fusiondms.core.model.parametros.Parametros
import br.com.fusiondms.feature.entregas.domain.entregasusecase.EntregasUseCase
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class EntregaViewModel @Inject constructor(
    private val entregasUseCase: EntregasUseCase,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    sealed class EntregaResult() {
        object Nothing : EntregaResult()
        class SuccessUpdate(responseCode: Int?) : EntregaResult()
        class ErrorUpdate(val message: String) : EntregaResult()
        class Selected(val entrega: Entrega): EntregaResult()
    }

    private var _listaEntrega = MutableStateFlow<List<Conteudo>>(listOf())
    val listaEntrega: StateFlow<List<Conteudo>> get() = _listaEntrega

    private var _entregaSelecionada = MutableStateFlow<EntregaResult>(EntregaResult.Nothing)
    val entregaSelecionada: StateFlow<EntregaResult> get() = _entregaSelecionada

    private var _romaneioId = MutableStateFlow(0)
    val romaneioId: StateFlow<Int> get() = _romaneioId

    private var _statusEntrega = MutableStateFlow<EntregaResult>(EntregaResult.Nothing)
    val statusEntrega: StateFlow<EntregaResult> get() = _statusEntrega

    private var _bottomSheetState = MutableStateFlow(BottomSheetBehavior.STATE_COLLAPSED)
    val bottomSheetState: StateFlow<Int> get() = _bottomSheetState

    private var _parametros = MutableStateFlow(Parametros())
    val parametros: StateFlow<Parametros> get() = _parametros

    private fun getIdRomaneioSelecionado(): Int = runBlocking {
        dataStoreRepository.getInt(DataStoreChaves.ID_ROMANEIO_SELECIONADO) ?: 0
    }

    init {
        getParametros()
    }

    fun getListaEntrega() {
        viewModelScope.launch {
            val romaneioId = getIdRomaneioSelecionado()
            entregasUseCase.getListaEntrega(romaneioId)
                .onStart {
                }
                .onCompletion {

                }
                .catch {
                    println(it)
                }
                .collect { lista ->
                    _romaneioId.emit(romaneioId)
                    _listaEntrega.emit(ArrayList(lista))
                }
        }
    }

    fun saveBottomSheetEntregasState(state: Int) {
        _bottomSheetState.value = state
    }

    fun setEntregaSelecionada(entrega: Entrega) =
        viewModelScope.launch {
            _entregaSelecionada.emit(EntregaResult.Selected(entrega))
        }

    fun updateStatusEntrega(entrega: Entrega, idEvento: Int) {
        viewModelScope.launch {
            entregasUseCase.updateStatusEntrega(entrega, idEvento)
                .catch { result ->
                    _statusEntrega.emit(EntregaResult.ErrorUpdate(result.message ?: ""))
                }
                .collect { result ->
                    getListaEntrega()
                    _statusEntrega.emit(EntregaResult.SuccessUpdate(result))
                }
        }
    }

    private fun getParametros() = viewModelScope.launch {
        val params = dataStoreRepository.getParametros() ?: Parametros()
        _parametros.emit(params)
    }

    fun resetViewModel() =
        viewModelScope.launch {
            _statusEntrega.emit(EntregaViewModel.EntregaResult.Nothing)
            _bottomSheetState.emit(BottomSheetBehavior.STATE_COLLAPSED)
            _listaEntrega.emit(arrayListOf())
            _entregaSelecionada.emit(EntregaResult.Nothing)
            _romaneioId.emit(0)
        }

    fun resetStatusEntrega() =
        viewModelScope.launch {
            _statusEntrega.emit(EntregaResult.Nothing)
        }
}