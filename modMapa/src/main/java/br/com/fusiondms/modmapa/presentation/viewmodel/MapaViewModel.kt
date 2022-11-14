package br.com.fusiondms.modmapa.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fusiondms.moddatastore.DataStoreChaves
import br.com.fusiondms.moddatastore.repository.DataStoreRepository
import br.com.fusiondms.modmapa.domain.entregasusecase.EntregasUseCase
import br.com.fusiondms.modmodel.EntregasPorCliente
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class MapaViewModel @Inject constructor(
    private val entregasUseCase: EntregasUseCase,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private var _listaEntrega = MutableStateFlow<List<EntregasPorCliente>>(listOf())
    val listaEntrega: StateFlow<List<EntregasPorCliente>> get() = _listaEntrega

    private var _cargaId = MutableStateFlow(0)
    val cargaId: StateFlow<Int> get() = _cargaId

    init {
        getListaEntrega(getIdCargaSelecionada()?: 0)
    }

    private fun getListaEntrega(cargaId: Int) {
        viewModelScope.launch {
            entregasUseCase.getListaEntrega(cargaId)
                .onStart {
                }
                .onCompletion {

                }
                .catch {
                    println(it)
                }
                .collect {
                    _cargaId.emit(cargaId)
                    _listaEntrega.emit(it)
                }
        }
    }

    private fun getIdCargaSelecionada() : Int? = runBlocking {
        dataStoreRepository.getInt(DataStoreChaves.ID_CARGA_SELECIONADA)
    }
}