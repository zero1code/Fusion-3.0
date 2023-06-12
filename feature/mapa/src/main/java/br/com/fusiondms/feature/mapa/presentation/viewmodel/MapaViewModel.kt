package br.com.fusiondms.feature.mapa.presentation.viewmodel

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fusiondms.core.datastore.chaves.DataStoreChaves
import br.com.fusiondms.core.datastore.repository.DataStoreRepository
import br.com.fusiondms.core.services.location.ForegroundLocationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class MapaViewModel @Inject constructor(
    dataStoreRepository: DataStoreRepository
) : ViewModel() {


    val localizacaoAtual =
        dataStoreRepository.getCurrentLocation(DataStoreChaves.KEY_CURRENT_LOCATION)
}