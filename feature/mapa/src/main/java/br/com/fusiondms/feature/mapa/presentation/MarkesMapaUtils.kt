package br.com.fusiondms.feature.mapa.presentation

import android.content.Context
import android.graphics.Color
import android.location.Location
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import br.com.fusiondms.core.common.R
import br.com.fusiondms.core.model.Conteudo
import br.com.fusiondms.feature.mapa.databinding.LayoutInfoLocalClienteBinding
import br.com.fusiondms.feature.mapa.databinding.MarkerLocalClienteBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.ui.IconGenerator
import java.util.Random

fun LayoutInflater.criarIconeLocalCliente(index: Int): BitmapDescriptor {
    val icone = IconGenerator(this.context)
    icone.setBackground(null)
    val bindingIcone: MarkerLocalClienteBinding = MarkerLocalClienteBinding.inflate(this)
    bindingIcone.apply {
        tvOrdemCliente.text = String.format("%02d", index + 1)
    }

    icone.setContentView(bindingIcone.root)
    val markerBitmap = icone.makeIcon()

    return BitmapDescriptorFactory.fromBitmap(markerBitmap)
}

fun LayoutInflater.bindInfoWindowLocalCliente(cliente: Conteudo.CarouselEntrega): GoogleMap.InfoWindowAdapter {
    val context = this.context
    return object : GoogleMap.InfoWindowAdapter {
        override fun getInfoContents(p0: Marker): View? {
            return null
        }

        override fun getInfoWindow(p0: Marker): View {
            val infoBinding: LayoutInfoLocalClienteBinding = LayoutInfoLocalClienteBinding.inflate(this@bindInfoWindowLocalCliente)

            infoBinding.apply {
                tvTitulo.text = context.resources.getQuantityString(
                    R.plurals.label_quantidade_entregas,
                    cliente.entregasPorCliente.entregas.size,
                    cliente.entregasPorCliente.entregas.size

                )
                tvCliente.text = Html.fromHtml(cliente.entregasPorCliente.cliente, 0)
            }

            return infoBinding.root
        }

    }
}

fun Context.bindLinhaRotaEntrePontos(listaCliente: List<Conteudo>): PolylineOptions {
    val planOptions = PolylineOptions()
        .color(ContextCompat.getColor(this, R.color.brand_celtic_blue))
        .width(5f)

    listaCliente.forEach { item ->
        val cliente = item as Conteudo.CarouselEntrega
        planOptions.add(LatLng(cliente.entregasPorCliente.latitude, cliente.entregasPorCliente.longitude))
    }

    return planOptions
}

fun bindCirculoRaioCliente(cliente: Conteudo.CarouselEntrega): CircleOptions {
    val centroRaio   = LatLng(cliente.entregasPorCliente.latitude, cliente.entregasPorCliente.longitude)
    val raioEmMetros = 150.0

    val random = Random()
    val alpha = (255 * 0.53).toInt()
    val color = Color.argb(
        alpha,
        random.nextInt(128),
        random.nextInt(128),
        random.nextInt(128)
    )

    return CircleOptions()
        .center(centroRaio)
        .radius(raioEmMetros)
        .strokeColor(Color.WHITE)
        .strokeWidth(5f)
        .fillColor(color)
}

fun checarSeEstaDentroRaioCliente(localizacaoAtual: String, raioMarkerMap: MutableMap<Circle, Marker>): Marker? {
    var result: Marker? = null
    for ((raio, marker) in raioMarkerMap.entries) {
        val latlng = localizacaoAtual.split(", ")
        val latitudeAtual = latlng[0].toDouble()
        val longitudeAtual = latlng[1].toDouble()

        val localAtual = Location("").apply {
            latitude = latitudeAtual
            longitude = longitudeAtual
        }

        val tamanhoRaio = 150.0
        val centroRaio = Location("").apply {
            latitude = raio.center.latitude
            longitude = raio.center.longitude
        }

        val distanciaEmMetros = localAtual.distanceTo(centroRaio)
        Log.d("TAG", "checarSeEstaDentroRaioCliente: $distanciaEmMetros")
        if (distanciaEmMetros <= tamanhoRaio) {
            result = marker
            break
        }
    }
    return result
}