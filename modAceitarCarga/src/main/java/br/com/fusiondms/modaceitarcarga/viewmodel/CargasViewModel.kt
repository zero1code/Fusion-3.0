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

package br.com.fusiondms.modaceitarcarga.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.fusiondms.modaceitarcarga.CargasData


class CargasViewModel : ViewModel() {

    private var _currentSport: MutableLiveData<br.com.fusiondms.modmodel.Romaneio> = MutableLiveData<br.com.fusiondms.modmodel.Romaneio>()
    val currentSport: LiveData<br.com.fusiondms.modmodel.Romaneio>
        get() = _currentSport

    private var _sportsData: ArrayList<br.com.fusiondms.modmodel.Romaneio> = ArrayList()
    val sportsData: ArrayList<br.com.fusiondms.modmodel.Romaneio>
        get() = _sportsData

    init {
        // Initialize the sports data.
        _sportsData = CargasData.getSportsData()
        _currentSport.value = _sportsData[0]
    }

    fun updateCurrentSport(romaneio: br.com.fusiondms.modmodel.Romaneio) {
        _currentSport.value = romaneio
    }
}
