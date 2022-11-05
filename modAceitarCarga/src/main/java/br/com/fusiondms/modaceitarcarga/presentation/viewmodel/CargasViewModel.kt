/*
 * Copyright (c) 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.fusiondms.modaceitarcarga.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fusiondms.modaceitarcarga.domain.cargasusecase.CargasUseCase
import br.com.fusiondms.modaceitarcarga.domain.recusarcargausecase.RecusarCargaUseCase
import br.com.fusiondms.modmodel.Romaneio
import br.com.fusiondms.modnetwork.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CargasViewModel @Inject constructor(
    private val cargasUseCase: CargasUseCase,
    private val recusarCargaUseCase: RecusarCargaUseCase
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
                    when (result) {
                        is Resource.Error -> _cargaSelecionada.emit(CargaStatus.Error(result.message))
                        is Resource.Success -> {
                            _listaCarga.remove(romaneio)
                            _cargaSelecionada.emit(CargaStatus.Deleted(_posicao))
                            _listaCargaStatus.emit(CargaStatus.Deleted(_posicao))

                            if (_listaCarga.size == 0) {
                                _listaCargaStatus.emit(CargaStatus.Empty)
                            }
                        }
                    }
                }
        }

    fun setCargaSelecionada(romaneio: Romaneio, posicao: Int) =
        viewModelScope.launch {
            _cargaSelecionada.emit(CargaStatus.Selected(romaneio))
            _posicao = posicao
        }

    fun resetCargaState() =
        viewModelScope.launch {
            _cargaSelecionada.emit(CargaStatus.Nothing)
            _listaCargaStatus.emit(CargaStatus.Nothing)
            _listaCarga = mutableListOf()
        }

}
