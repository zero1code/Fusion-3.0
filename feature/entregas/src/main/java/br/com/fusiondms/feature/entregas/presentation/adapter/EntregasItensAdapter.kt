package br.com.fusiondms.feature.entregas.presentation.adapter


import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.fusiondms.core.common.R.*
import br.com.fusiondms.core.common.toMoedaLocal
import br.com.fusiondms.core.model.entrega.EntregaItem
import br.com.fusiondms.feature.entregas.databinding.ItemEntregaItemBinding

class EntregasItensAdapter() : ListAdapter<EntregaItem, EntregasItensAdapter.EntregaItemViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntregaItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return EntregaItemViewHolder(
            ItemEntregaItemBinding.inflate(layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: EntregaItemViewHolder, position: Int) {
        val entregaItem = getItem(position)
        holder.bind(entregaItem)
    }

    inner class EntregaItemViewHolder(private var binding: ItemEntregaItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(entregaItem: EntregaItem) {
            val context = binding.root.context
            with(binding) {
                tvCodigoDescricao.text =
                    Html.fromHtml("${entregaItem.codigoMercadoria} - ${entregaItem.descricao}", 0)
                tvPrecoUnitario.text = Html.fromHtml(
                    "<b>${context.getString(string.label_preco_unitario)}:</b><br>${entregaItem.preco.toMoedaLocal()}",
                    0
                )
                tvQuantidade.text = Html.fromHtml(
                    "<b>${context.getString(string.label_quantidade)}:</b><br>${entregaItem.quantidade}",
                    0
                )
                tvUnidade.text = Html.fromHtml(
                    "<b>${context.getString(string.label_unidade)}:</b><br>${entregaItem.unidade}",
                    0
                )
                tvPeso.text =
                    Html.fromHtml("<b>${context.getString(string.label_peso)}:</b><br>${entregaItem.peso}", 0)
                tvSt.text = Html.fromHtml(
                    "<b>${context.getString(string.label_sub_total)}:</b><br>${entregaItem.valorST.toMoedaLocal()}",
                    0
                )
                tvTotal.text = Html.fromHtml(
                    "${context.getString(string.label_total)}:<br>${entregaItem.subTotal.toMoedaLocal()}",
                    0
                )
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<EntregaItem>() {
        override fun areItemsTheSame(oldItem: EntregaItem, newItem: EntregaItem) =
            oldItem.codigoMercadoria == newItem.codigoMercadoria

        override fun areContentsTheSame(oldItem: EntregaItem, newItem: EntregaItem) =
            oldItem == newItem
    }
}