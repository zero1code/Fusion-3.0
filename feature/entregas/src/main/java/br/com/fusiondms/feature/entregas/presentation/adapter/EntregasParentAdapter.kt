package br.com.fusiondms.feature.entregas.presentation.adapter

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.fusiondms.core.common.R
import br.com.fusiondms.core.model.Conteudo
import br.com.fusiondms.feature.entregas.interfaces.NestedRecyclerViewViewHolder
import br.com.fusiondms.feature.entregas.util.CustomHorizontalLayoutManager
import br.com.fusiondms.feature.entregas.util.NestedRecyclerViewStateRecoverAdapter
import br.com.fusiondms.core.model.entrega.Entrega
import br.com.fusiondms.feature.entregas.databinding.ItemEntregaChildBinding
import br.com.fusiondms.feature.entregas.databinding.ItemEntregaParentBinding
import java.util.concurrent.TimeUnit

private enum class ViewType {
    CAROUSEL_ENTREGA,
}

class EntregasParentAdapter() : NestedRecyclerViewStateRecoverAdapter<Conteudo, EntregasParentAdapter.ViewHolder>(
    ContentAdapterDiffUtil()
) {
    var onEntregaClickListener: (binding: ItemEntregaChildBinding, entrega: Entrega) -> Unit = { _: ItemEntregaChildBinding, _: Entrega -> }
    var onEntregaLongClickListener: (entrega: Entrega, position: Int) -> Unit = { _: Entrega, _: Int -> }

    var itemDestacado = RecyclerView.NO_POSITION

    private var listaCompleta = listOf<Conteudo.CarouselEntrega>()
    private var filtroEntrega = arrayListOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            ViewType.CAROUSEL_ENTREGA.ordinal ->
                ViewHolder.CarouselEntregaViewHolder(
                    binding = ItemEntregaParentBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    listener = onEntregaClickListener,
                    longListener = onEntregaLongClickListener
                )
            else -> throw ClassNotFoundException()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Conteudo.CarouselEntrega -> ViewType.CAROUSEL_ENTREGA.ordinal
            else -> throw ClassNotFoundException()
        }
    }

    override fun getItemId(position: Int) = currentList[position].id.toLong()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ViewType.CAROUSEL_ENTREGA.ordinal -> (holder as ViewHolder.CarouselEntregaViewHolder)
                .bind(getItem(position) as Conteudo.CarouselEntrega, itemDestacado)
        }
        super.onBindViewHolder(holder, position)
    }

    sealed class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        class CarouselEntregaViewHolder(
            private val binding: ItemEntregaParentBinding,
            private val listener: (binding: ItemEntregaChildBinding, entrega: Entrega) -> Unit,
            private val longListener: (entrega: Entrega, position: Int) -> Unit
        ) : ViewHolder(binding.root), NestedRecyclerViewViewHolder {

            private lateinit var content: Conteudo.CarouselEntrega
            fun bind(content: Conteudo.CarouselEntrega, itemDestacado: Int) {
                bindChildAdapter()

                val context = binding.root.context
                this.content = content
                with(binding) {
                    if (adapterPosition == itemDestacado) {
                        binding.root.strokeColor = Color.parseColor("#f3f402")
                        Handler(Looper.getMainLooper()).postDelayed({
                            binding.root.strokeColor = Color.TRANSPARENT
                        }, TimeUnit.SECONDS.toMillis(4))
                    } else {
                        binding.root.strokeColor = Color.TRANSPARENT
                    }
//                    tvCliente.text = Html.fromHtml(context.getString(string.label_dados_cliente), 0)
//                    tvLocal.text = Html.fromHtml(context.getString(string.label_localizacao_cliente), 0)
                    tvCliente.text = Html.fromHtml(content.entregasPorCliente.cliente, 0)
                    tvLocal.text = Html.fromHtml(content.entregasPorCliente.local, 0)

                    rvEntregasChild.layoutManager =
                        if (content.entregasPorCliente.entregas.size > 1) CustomHorizontalLayoutManager(context, RecyclerView.HORIZONTAL)
                        else LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                    (rvEntregasChild.adapter as EntregasChildAdapter).submitList(content.entregasPorCliente.entregas)
                }
            }

            private fun bindChildAdapter() {
                binding.rvEntregasChild.apply {
                    val adapterEntregas = EntregasChildAdapter()
//                    adapterEntregas.setHasStableIds(true)
                    adapter = adapterEntregas
                    adapterEntregas.onEntregaClickListener = { binding, entrega ->
                        listener(binding, entrega)
                    }

                    adapterEntregas.onEntregaLongClickListener = { entrega, position ->
                        longListener(entrega, adapterPosition)
                    }
                }
            }

            override fun getId() = content.idCliente

            override fun getLayoutManager() = binding.rvEntregasChild.layoutManager
        }
    }

    fun filtroAvancado(context: Context, textoPesquisado: String, filtrarPor: String) {
        val lista = listaCompleta.map { it.copy() }.toList()
        val listaFiltrada = if (textoPesquisado.isNotBlank()) {
            lista.filter { item ->
                when (filtrarPor) {
                    context.getString(R.string.label_codigo_cliente),
                    context.getString(R.string.label_razao_social),
                    context.getString(R.string.label_nome_fantasia) ->
                        item.entregasPorCliente.cliente.contains(textoPesquisado, true)
                    context.getString(R.string.label_bairro),
                    context.getString(R.string.label_cidade) ->
                        item.entregasPorCliente.local.contains(textoPesquisado, true)
                    context.getString(R.string.label_nota_fiscal) -> {
                        buscarTextoNasEntregas(item, textoPesquisado)
                        item.entregasPorCliente.entregas.isNotEmpty()
                    }
                    else -> return
                }
            }
        } else {
            lista
        }

        listaFiltrada.forEach { item ->
            when (filtrarPor) {
                context.getString(R.string.label_codigo_cliente),
                context.getString(R.string.label_razao_social),
                context.getString(R.string.label_nome_fantasia) -> {
                    val textoMarcado =
                        pintarTextoPesquisado(item.entregasPorCliente.cliente, textoPesquisado)
                    item.entregasPorCliente.cliente = textoMarcado
                }
                context.getString(R.string.label_bairro),
                context.getString(R.string.label_cidade) -> {
                    val textoMarcado =
                        pintarTextoPesquisado(item.entregasPorCliente.local, textoPesquisado)
                    item.entregasPorCliente.local = textoMarcado
                }
                context.getString(R.string.label_nota_fiscal) -> {
                    item.entregasPorCliente.entregas.forEach { entrega ->
                        val textoMarcado =
                            pintarTextoPesquisado(entrega.numeroNotaFiscal, textoPesquisado)
                        entrega.numeroNotaFiscal = textoMarcado
                    }
                }
            }
        }
        submitList(listaFiltrada)
    }

    private fun buscarTextoNasEntregas(item: Conteudo.CarouselEntrega, textoPesquisado: String) : Boolean {
        val lista = item.entregasPorCliente.entregas.map { it.copy() }.toList()
        val listaFiltrada = lista.filter { entrega ->
            entrega.numeroNotaFiscal.contains(textoPesquisado, true)
        }
        item.entregasPorCliente.entregas = listaFiltrada
        return true
    }

    private fun pintarTextoPesquisado(textoCompleto: String, textoPesquisado: String): String {
        val startIndex = textoCompleto.indexOf(textoPesquisado, ignoreCase = true)
        val textoMarcado = if (textoPesquisado.isNotBlank() && startIndex != -1) {
            StringBuilder(textoCompleto)
                .insert(startIndex + textoPesquisado.length, "</span>") //final do texto
                .insert(startIndex, "<span style=\"background-color:#f3f402;\">") // inicio do texto
                .toString()
        } else {
            textoCompleto
        }
        return textoMarcado
    }

    fun filtroPorStatus(vararg statusEntrega: Int, adicionarFiltro: Boolean) {
        statusEntrega.forEach { status ->
            if (adicionarFiltro) filtroEntrega.add(status)
            else filtroEntrega.remove(status)
        }

        val lista = listaCompleta.map { it.copy() }.toList()
        val listaFiltrada = if (filtroEntrega.isNotEmpty()) {
            lista.filter { item ->
                buscarStatusNasEntregas(item)
                item.entregasPorCliente.entregas.isNotEmpty()
            }
        } else {
            listaCompleta
        }

        submitList(listaFiltrada)
    }

    private fun buscarStatusNasEntregas(item: Conteudo.CarouselEntrega) {
        val lista = item.entregasPorCliente.entregas.map { it.copy() }.toList()
        val listaFiltrada = lista.filter { entrega ->
            filtroEntrega.toString().contains(entrega.statusEntrega)
        }
        item.entregasPorCliente.entregas = listaFiltrada
    }

    fun atualizarLista(listaCliente: List<Conteudo>) {
        val lista = mutableListOf<Conteudo.CarouselEntrega>()
        listaCliente.forEach { cliente ->
            lista.add(cliente as Conteudo.CarouselEntrega)
        }
        listaCompleta = lista.toList()
        submitList(listaCliente)
    }

    fun destacarPosicao(position: Int) {
        itemDestacado = position
        notifyItemChanged(position)
    }

    private class ContentAdapterDiffUtil : DiffUtil.ItemCallback<Conteudo>() {
        override fun areContentsTheSame(oldItem: Conteudo, newItem: Conteudo) =
            oldItem.id == newItem.id

        override fun areItemsTheSame(oldItem: Conteudo, newItem: Conteudo) =
            oldItem == newItem
    }
}