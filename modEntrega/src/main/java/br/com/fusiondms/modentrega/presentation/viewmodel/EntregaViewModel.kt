package br.com.fusiondms.modentrega.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fusiondms.moddatastore.DataStoreChaves
import br.com.fusiondms.moddatastore.repository.DataStoreRepository
import br.com.fusiondms.modentrega.domain.entregasusecase.EntregasUseCase
import br.com.fusiondms.modmodel.Entrega
import br.com.fusiondms.modmodel.Conteudo
import br.com.fusiondms.modmodel.Resource
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
        class SuccessUpdate(val message: String, responseCode: Int?) : EntregaResult()
        class ErrorUpdate(val message: String) : EntregaResult()
        class Selected(val entrega: Entrega): EntregaResult()
    }

    private var _listaEntrega = MutableStateFlow<List<Conteudo>>(listOf())
    val listaEntrega: StateFlow<List<Conteudo>> get() = _listaEntrega

    private var _entregaSelecionada = MutableStateFlow<EntregaResult>(EntregaResult.Nothing)
    val entregaSelecionada: StateFlow<EntregaResult> get() = _entregaSelecionada

    private var _cargaId = MutableStateFlow(0)
    val cargaId: StateFlow<Int> get() = _cargaId

    private var _statusEntrega = MutableStateFlow<EntregaResult>(EntregaResult.Nothing)
    val statusEntrega: StateFlow<EntregaResult> get() = _statusEntrega

    private var _bottomSheetState = MutableStateFlow(BottomSheetBehavior.STATE_COLLAPSED)
    val bottomSheetState: StateFlow<Int> get() = _bottomSheetState

    private fun getIdCargaSelecionada(): Int? = runBlocking {
        dataStoreRepository.getInt(DataStoreChaves.ID_CARGA_SELECIONADA)
    }

    fun getListaEntrega() {
        viewModelScope.launch {
            val cargaId = getIdCargaSelecionada() ?: 0
            entregasUseCase.getListaEntrega(cargaId)
                .onStart {
                }
                .onCompletion {

                }
                .catch {
                    println(it)
                }
                .collect { lista ->
                    _cargaId.emit(cargaId)
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
                    when (result) {
                        is Resource.Error -> _statusEntrega.emit(EntregaResult.ErrorUpdate(result.message))
                        is Resource.Success -> {
                            getListaEntrega()
                            _statusEntrega.emit(
                                EntregaResult.SuccessUpdate(result.message, result.responseCode)
                            )
                        }
                    }
                }
        }
    }

    fun resetViewModel() =
        viewModelScope.launch {
            _statusEntrega.emit(EntregaResult.Nothing)
            _bottomSheetState.emit(BottomSheetBehavior.STATE_COLLAPSED)
            _listaEntrega.emit(arrayListOf())
            _entregaSelecionada.emit(EntregaResult.Nothing)
            _cargaId.emit(0)
        }

    fun resetStatusEntrega() =
        viewModelScope.launch {
            _statusEntrega.emit(EntregaResult.Nothing)
        }
}