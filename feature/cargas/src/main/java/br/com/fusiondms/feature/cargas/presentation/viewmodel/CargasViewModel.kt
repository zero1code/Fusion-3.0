package br.com.fusiondms.feature.cargas.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fusiondms.core.datastore.chaves.DataStoreChaves
import br.com.fusiondms.core.datastore.repository.DataStoreRepository
import br.com.fusiondms.core.model.romaneio.Romaneio
import br.com.fusiondms.feature.cargas.domain.cargasusecase.CargasUseCase
import br.com.fusiondms.feature.cargas.domain.recusarcargausecase.RecusarCargaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class CargasViewModel @Inject constructor(
    private val cargasUseCase: CargasUseCase,
    private val recusarCargaUseCase: RecusarCargaUseCase,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    sealed class CargaStatus() {
        object Nothing : CargaStatus()
        object Empty : CargaStatus()
        class Loading(val isLoading: Boolean) : CargaStatus()
        class Error(val message: String?) : CargaStatus()
        class Selected(val romaneio: Romaneio) : CargaStatus()
        class Deleted(val position: Int) : CargaStatus()
        class Success(val lista: List<Romaneio>) : CargaStatus()
    }

    private var _cargaSelecionada: MutableStateFlow<CargaStatus> = MutableStateFlow(CargaStatus.Nothing)
    val cargaSelecionada: StateFlow<CargaStatus> get() = _cargaSelecionada

    private var _listaCargaStatus: MutableStateFlow<CargaStatus> = MutableStateFlow(CargaStatus.Nothing)
    val listaCargaStatus: StateFlow<CargaStatus> get() = _listaCargaStatus

    private var _posicao = -1
    private var _listaCarga = mutableListOf<Romaneio>()

     fun getListaCarga() =
        viewModelScope.launch {
            cargasUseCase.getListaCarga()
                .onStart {
                    _listaCargaStatus.emit(CargaStatus.Loading(true))
                }
                .onCompletion {
                    _listaCargaStatus.emit(CargaStatus.Loading(false))
                }
                .catch {
                    _listaCargaStatus.emit(CargaStatus.Error(it.message))
                }
                .collect {
                    delay(1500)
                    if (it.isNotEmpty()) {
                        _listaCarga = it.toMutableList()
                        _listaCargaStatus.emit(CargaStatus.Success(_listaCarga))
                    } else {
                        _listaCargaStatus.emit(CargaStatus.Empty)
                    }
                }

        }

    fun recusarCarga(romaneio: Romaneio) =
        viewModelScope.launch {
            recusarCargaUseCase.recusarCarga(romaneio)
                .onStart {
                    _cargaSelecionada.emit(CargaStatus.Loading(true))
                }
                .onCompletion {
                    _cargaSelecionada.emit(CargaStatus.Loading(false))
                }
                .catch {
                    _cargaSelecionada.emit(CargaStatus.Error(it.message))
                }
                .collect { result ->
                    _listaCarga.remove(romaneio)
                    _cargaSelecionada.emit(CargaStatus.Deleted(_posicao))
                    _listaCargaStatus.emit(CargaStatus.Deleted(_posicao))

                    if (_listaCarga.size == 0) {
                        _listaCargaStatus.emit(CargaStatus.Empty)
                    }
                }
        }

    fun setCargaSelecionada(romaneio: Romaneio, posicao: Int) =
        viewModelScope.launch {
            _cargaSelecionada.emit(CargasViewModel.CargaStatus.Selected(romaneio))
            _posicao = posicao
        }

    fun resetCargaState() =
        viewModelScope.launch {
            _cargaSelecionada.emit(CargasViewModel.CargaStatus.Nothing)
            _listaCargaStatus.emit(CargasViewModel.CargaStatus.Nothing)
            _listaCarga = mutableListOf()
        }

    fun salvarIdCargaSelecionada(cargaId: Int) = runBlocking {
            dataStoreRepository.putInt(DataStoreChaves.ID_ROMANEIO_SELECIONADO, cargaId)
        }
}
