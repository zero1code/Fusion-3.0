package br.com.fusiondms.feature.mapa.presentation.viewmodel

import android.icu.util.MeasureUnit.EM
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fusiondms.core.datastore.chaves.DataStoreChaves
import br.com.fusiondms.core.datastore.repository.DataStoreRepository
import br.com.fusiondms.core.model.Conteudo
import br.com.fusiondms.core.model.entrega.Entrega
import br.com.fusiondms.core.notification.EnumNotificacao
import br.com.fusiondms.core.notification.Notificacao
import br.com.fusiondms.feature.entregas.presentation.viewmodel.EntregaViewModel
import br.com.fusiondms.feature.entregas.util.EventoEntregaId
import br.com.fusiondms.feature.entregas.util.EventoEntregaId.ENTREGA_CHEGADA_CLIENTE
import br.com.fusiondms.feature.entregas.util.EventoEntregaId.ENTREGA_INICIADA
import br.com.fusiondms.feature.entregas.util.EventoEntregaId.ENTREGA_PENDENTE
import br.com.fusiondms.feature.mapa.presentation.calcularDistanciaMetros
import br.com.fusiondms.feature.mapa.presentation.toLatLng
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapaViewModel @Inject constructor(
    dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private var _proximoLocalEntrega = MutableStateFlow<LatLng?>(null)
    private var _ultimoLocalEntrega = MutableStateFlow<LatLng?>(null)
    private var _listaCliente = MutableSharedFlow<List<Conteudo>>()

    private var _progresso = MutableStateFlow(0)
    val progresso = _progresso.asStateFlow()

    val localizacaoAtual =
        dataStoreRepository.getCurrentLocation(DataStoreChaves.KEY_CURRENT_LOCATION)

    fun moverParaProximoCliente(localizacaoAtual: LatLng, listaCliente: List<Conteudo>) {

        if (_proximoLocalEntrega.value != null && _ultimoLocalEntrega.value != null) {
            val distanciaTotal = calcularDistanciaMetros(_ultimoLocalEntrega.value!!, _proximoLocalEntrega.value!!)
            val distanciaRestanteDoLocalAtual = calcularDistanciaMetros(localizacaoAtual, _proximoLocalEntrega.value!!)
            atualizarProgresso(distanciaRestanteDoLocalAtual, distanciaTotal)
        }
    }

    fun setListaCliente(listaCliente: List<Conteudo>) =
        viewModelScope.launch {
            _listaCliente.emit(listaCliente)
            proximaEntrega()
        }

    private fun atualizarProgresso(distanciaRestanteDoLocalAtual: Float, distanciaTotal: Float){
        val progresso = if (distanciaRestanteDoLocalAtual <= (distanciaTotal - 20f)) {
            (((distanciaTotal - distanciaRestanteDoLocalAtual) / distanciaTotal) * 100).toInt()
        } else 100
        _progresso.value = progresso

        Log.d("TAG", "atualizarProgressBar: $progresso - distancia restante local atual: $distanciaRestanteDoLocalAtual - distancia total $distanciaTotal")
    }

    private fun proximaEntrega() {
        viewModelScope.launch {
            val status = listOf(ENTREGA_PENDENTE, ENTREGA_CHEGADA_CLIENTE, ENTREGA_INICIADA)
            _listaCliente.collect { lista ->
                run loop@{
                    lista.forEachIndexed { index, item ->
                        val cliente = item as Conteudo.CarouselEntrega
                        val result = cliente.entregasPorCliente.entregas.any {
                            status.contains(it.statusEntrega.toInt())
                        }

                        if (result) {
                            _proximoLocalEntrega.emit(
                                LatLng(
                                    cliente.entregasPorCliente.latitude,
                                    cliente.entregasPorCliente.longitude
                                )
                            )
                            if (index != 0) {
                                val clienteAnterior = lista[index - 1] as Conteudo.CarouselEntrega
                                _ultimoLocalEntrega.emit(
                                    LatLng(
                                        clienteAnterior.entregasPorCliente.latitude,
                                        clienteAnterior.entregasPorCliente.longitude
                                    )
                                )
                            } else {
                                _ultimoLocalEntrega.emit(LatLng(-23.397754, -46.512062)) // ex referencia ultimo local
                            }
                            return@loop
                        }
                    }
                }
            }
        }
    }
}