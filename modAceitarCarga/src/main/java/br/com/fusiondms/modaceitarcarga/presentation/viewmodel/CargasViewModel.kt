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
import br.com.fusiondms.moddatabase.repository.romaneios.RomaneiosRepository
import br.com.fusiondms.modmodel.Romaneio
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RomaneiosViewModel @Inject constructor(
    private val romaneiosRepository: RomaneiosRepository
) : ViewModel() {

    sealed class RomaneioStatus() {
        object Nothing: RomaneioStatus()
        object Empty: RomaneioStatus()
        object Loading: RomaneioStatus()
        object Error: RomaneioStatus()
        class Success(val lista: List<Romaneio>): RomaneioStatus()
    }

    private var _romaneioSelecionado: MutableStateFlow<Romaneio> = MutableStateFlow(Romaneio())
    val romaneioSelecionado: StateFlow<Romaneio>
        get() = _romaneioSelecionado

    private var _listaRomaneio: MutableStateFlow<RomaneioStatus> = MutableStateFlow(RomaneioStatus.Nothing)
    val listaRomaneio: StateFlow<RomaneioStatus>
        get() = _listaRomaneio

    init {
        getListaRomaneio()
    }

    private fun getListaRomaneio() {
        viewModelScope.launch {
            _listaRomaneio.emit(RomaneioStatus.Loading)
            delay(3000)
            val lista = romaneiosRepository.getListaRomaneio()
            if (lista.isEmpty()) {
                _listaRomaneio.emit(RomaneioStatus.Empty)
            } else {
                _listaRomaneio.emit(RomaneioStatus.Success(lista))
            }
        }
    }

    fun setRomaneioSelecionado(romaneio: Romaneio) {
        _romaneioSelecionado.value = romaneio
    }
}
