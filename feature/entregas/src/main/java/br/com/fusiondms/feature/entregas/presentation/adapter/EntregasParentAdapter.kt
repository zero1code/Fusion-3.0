package br.com.fusiondms.feature.entregas.presentation.adapter

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.fusiondms.core.model.Conteudo
import br.com.fusiondms.feature.entregas.interfaces.NestedRecyclerViewViewHolder
import br.com.fusiondms.feature.entregas.util.CustomHorizontalLayoutManager
import br.com.fusiondms.feature.entregas.util.NestedRecyclerViewStateRecoverAdapter
import br.com.fusiondms.core.model.entrega.Entrega
import br.com.fusiondms.feature.entregas.databinding.ItemEntregaChildBinding
import br.com.fusiondms.feature.entregas.databinding.ItemEntregaParentBinding

private enum class ViewType {
    CAROUSEL_ENTREGA,
}

class EntregasParentAdapter : NestedRecyclerViewStateRecoverAdapter<Conteudo, EntregasParentAdapter.ViewHolder>(
    ContentAdapterDiffUtil()
) {
    var onEntregaClickListener: (binding: ItemEntregaChildBinding, entrega: Entrega) -> Unit = { _: ItemEntregaChildBinding, _: Entrega -> }
    var onEntregaLongClickListener: (entrega: Entrega, position: Int) -> Unit = { _: Entrega, _: Int -> }

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

    override fun getItemId(position: Int) = position.toLong()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ViewType.CAROUSEL_ENTREGA.ordinal -> (holder as ViewHolder.CarouselEntregaViewHolder)
                .bind(getItem(position) as Conteudo.CarouselEntrega)
        }
        super.onBindViewHolder(holder, position)
    }

    sealed class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        class CarouselEntregaViewHolder(
            private val binding: ItemEntregaParentBinding,
            listener: (binding: ItemEntregaChildBinding, entrega: Entrega) -> Unit,
            longListener: (entrega: Entrega, position: Int) -> Unit
        ) : ViewHolder(binding.root), NestedRecyclerViewViewHolder {

            private lateinit var content: Conteudo.CarouselEntrega

            init {
                binding.rvEntregasChild.apply {
                    val adapterEntregas = EntregasChildAdapter()
                    adapterEntregas.setHasStableIds(true)
                    adapter = adapterEntregas
                    adapterEntregas.onEntregaClickListener = { binding, entrega ->
                        listener(binding, entrega)
                    }

                    adapterEntregas.onEntregaLongClickListener = { entrega, position ->
                        longListener(entrega, adapterPosition)
                    }
                }
            }

            fun bind(content: Conteudo.CarouselEntrega) {
                val context = binding.root.context
                this.content = content
                with(binding) {
//                    tvCliente.text = Html.fromHtml(context.getString(string.label_dados_cliente), 0)
//                    tvLocal.text = Html.fromHtml(context.getString(string.label_localizacao_cliente), 0)
                    tvCliente.text = Html.fromHtml(content.cliente.cliente, 0)
                    tvLocal.text = Html.fromHtml(content.cliente.local, 0)

                    rvEntregasChild.layoutManager =
                        if (content.cliente.entregas.size > 1) CustomHorizontalLayoutManager(context, RecyclerView.HORIZONTAL)
                        else LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                    (rvEntregasChild.adapter as EntregasChildAdapter).submitList(content.cliente.entregas)
                }
            }

            override fun getId() = content.idCliente

            override fun getLayoutManager() = binding.rvEntregasChild.layoutManager
        }
    }

    private class ContentAdapterDiffUtil : DiffUtil.ItemCallback<Conteudo>() {
        override fun areContentsTheSame(oldItem: Conteudo, newItem: Conteudo) =
            oldItem.id == newItem.id

        override fun areItemsTheSame(oldItem: Conteudo, newItem: Conteudo) =
            oldItem == newItem
    }
}