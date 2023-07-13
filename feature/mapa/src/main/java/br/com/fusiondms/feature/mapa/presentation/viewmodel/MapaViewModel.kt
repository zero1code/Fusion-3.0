package br.com.fusiondms.feature.mapa.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fusiondms.core.common.toDataLegivel
import br.com.fusiondms.core.common.toHorasMinutos
import br.com.fusiondms.core.datastore.chaves.DataStoreChaves
import br.com.fusiondms.core.datastore.repository.DataStoreRepository
import br.com.fusiondms.core.model.Conteudo
import br.com.fusiondms.core.notification.model.NotificacaoViagem
import br.com.fusiondms.feature.entregas.util.EventoEntregaId.ENTREGA_CHEGADA_CLIENTE
import br.com.fusiondms.feature.entregas.util.EventoEntregaId.ENTREGA_INICIADA
import br.com.fusiondms.feature.entregas.util.EventoEntregaId.ENTREGA_PENDENTE
import br.com.fusiondms.feature.mapa.presentation.calcularDistanciaMetros
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class MapaViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private var _proximoLocalEntrega = MutableStateFlow<LatLng?>(null)
    private var _ultimoLocalEntrega = MutableStateFlow<LatLng?>(null)
    private var _listaCliente = MutableSharedFlow<List<Conteudo>>()

    private val _progresso = MutableStateFlow(-1)

    val localizacaoAtual =
        dataStoreRepository.getCurrentLocation(DataStoreChaves.KEY_CURRENT_LOCATION)

    private val _velocidadeAtual = MutableStateFlow(0)
    val velocidadeAtual = _velocidadeAtual.asStateFlow()

    private val _velocidadeMedia = MutableStateFlow(emptyList<Int>())

    private val _distanciaRestanteDoLocalAtual = MutableStateFlow(0F)

    private val _notificacaoViagem = MutableSharedFlow<NotificacaoViagem>()
    val notificacaoViagem = _notificacaoViagem.asSharedFlow()

    private val _horarioChegadaAproximado = MutableStateFlow(Instant.now())

    init {
        getVelocidade()
    }

    fun moverParaProximoCliente(localizacaoAtual: LatLng, listaCliente: List<Conteudo>) =
        viewModelScope.launch {
        if (_proximoLocalEntrega.value != null && _ultimoLocalEntrega.value != null) {
            val distanciaTotal = calcularDistanciaMetros(_ultimoLocalEntrega.value!!, _proximoLocalEntrega.value!!)
            _distanciaRestanteDoLocalAtual.emit(calcularDistanciaMetros(localizacaoAtual, _proximoLocalEntrega.value!!))
            atualizarProgresso(_distanciaRestanteDoLocalAtual.value, distanciaTotal)
        }
    }

    fun setListaCliente(listaCliente: List<Conteudo>) =
        viewModelScope.launch {
            _listaCliente.emit(listaCliente)
            proximaEntrega()
        }

    private fun getVelocidade() =
        viewModelScope.launch {
            dataStoreRepository.getCurrentVelocidade(DataStoreChaves.KEY_CURRENT_SPEED).collect {
                val velocidade = it ?: 0
                _velocidadeAtual.emit(velocidade)

                val listaVelocidade = _velocidadeMedia.value.toMutableList()
                if (listaVelocidade.none { vel -> vel == velocidade }) {
                    if (listaVelocidade.size >= 10) listaVelocidade.removeAt(0)
                    listaVelocidade.add(velocidade)
                }
                _velocidadeMedia.emit(listaVelocidade)
            }
        }

    private fun atualizarProgresso(distanciaRestanteDoLocalAtual: Float, distanciaTotal: Float) =
        viewModelScope.launch {
            val progresso = if (distanciaRestanteDoLocalAtual <= (distanciaTotal - 20f)) {
                (((distanciaTotal - distanciaRestanteDoLocalAtual) / distanciaTotal) * 100).toInt()
            } else -1

            val progessoParaExibir = when (progresso) {
                in 0..12 -> 0
                in 13..24 -> 13
                in 25..37 -> 25
                in 38..49 -> 38
                in 50..62 -> 50
                in 63..75 -> 63
                in 76..88 -> 76
                in 89..94 -> 88
                in 95..100 -> 100
                else -> 0
            }

            val progressoAnterior = _progresso.value
            if (progressoAnterior != progessoParaExibir) calcularHorarioAproximadoChegada()
            else checarHorarioChegadaProximoHorarioAtual()

            _progresso.emit(progessoParaExibir)
        }

    private fun calcularHorarioAproximadoChegada() =
        viewModelScope.launch {
            try {
                val horaAtual = Instant.now()

                val distanciaRestante = _distanciaRestanteDoLocalAtual.value
                val velocidadeMedia = _velocidadeMedia.value.average()
                val velocidade =
                    if (velocidadeMedia <= MEDIA_CAMINHADA_HUMANA) MEDIA_CAMINHADA_HUMANA
                    else velocidadeMedia

                val tempoParaChegada =
                    Duration.ofSeconds((distanciaRestante / (velocidade / KM_TO_METROS)).toLong())
                val horarioChegadaAproximado = horaAtual.plus(tempoParaChegada)

                val mediaDuracaoViagem = Duration.between(horaAtual, horarioChegadaAproximado)

                val segundos = mediaDuracaoViagem.seconds
                val minutos = mediaDuracaoViagem.toMinutes()
                val horas = mediaDuracaoViagem.toHours()
                val dias = mediaDuracaoViagem.toDays()

                val textoHoraChegada = when {
                    dias > 0 -> horarioChegadaAproximado.epochSecond.toDataLegivel()
                    else -> horarioChegadaAproximado.epochSecond.toHorasMinutos()
                }

                _notificacaoViagem.emit(
                    NotificacaoViagem(
                        progresso = _progresso.value,
                        distanciaRestanteDoLocalAtual = _distanciaRestanteDoLocalAtual.value,
                        velocidade = _velocidadeAtual.value,
                        horarioAproximadoChegada = textoHoraChegada
                    )
                )

                _horarioChegadaAproximado.emit(horarioChegadaAproximado)

                Log.d("TAG", "Diferença em segundos: $segundos")
                Log.d("TAG", "Diferença em minutos: $minutos")
                Log.d("TAG", "Diferença em horas: $horas")
                Log.d("TAG", "Diferença em dias: $dias")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    private fun checarHorarioChegadaProximoHorarioAtual() {
        val horaAtual = Instant.now()
        val horarioChegadaAproximado = _horarioChegadaAproximado.value
        if (horaAtual >= horarioChegadaAproximado) calcularHorarioAproximadoChegada()
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
                                _ultimoLocalEntrega.emit(LatLng(-23.3972543, -46.5160081)) // ex referencia ultimo local
                            }
                            return@loop
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val MEDIA_CAMINHADA_HUMANA = 3.6
        const val KM_TO_METROS = 3.6
    }
}