package br.com.fusiondms.modmapa.adapter

import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.fusiondms.modmapa.databinding.ItemEntregaChildBinding
import br.com.fusiondms.modmodel.Entrega

class EntregasChildAdapter :
    ListAdapter<Entrega, EntregasChildAdapter.EntregasViewHolder>(DiffCallback) {

    var onEntregaClickListener: (entrega: String) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntregasViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return EntregasViewHolder(
            ItemEntregaChildBinding.inflate(layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: EntregasViewHolder, position: Int) {
        val entrega = getItem(position)
        holder.bind(entrega)
    }

    inner class EntregasViewHolder(
        private var binding: ItemEntregaChildBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(entrega: Entrega) {
            binding.tvStatus.text = Html.fromHtml("<b>Pendente</b>", 0)
            binding.tvOrdemEntrega.text = Html.fromHtml("<b>01</b>", 0)
            binding.tvNotaFiscal.text = Html.fromHtml(
                "<b>NF/Pernota:</b><br>" +
                        "123456/23453222", 0
            )
            binding.tvValor.text = Html.fromHtml(
                "<b>Valor:</b><br>" +
                        "R$1.452,00", 0
            )

            itemView.setOnClickListener {
                onEntregaClickListener("Clicou em uma entrega")
            }

            setIsRecyclable(false)
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Entrega>() {
        override fun areItemsTheSame(oldItem: Entrega, newItem: Entrega) =
            oldItem.idEntrega == newItem.idEntrega

        override fun areContentsTheSame(oldItem: Entrega, newItem: Entrega) =
            oldItem == newItem
    }
}