package br.com.fusiondms.feature.entregas.presentation.adapter

import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.fusiondms.core.common.getColorFromAttr
import br.com.fusiondms.core.common.R
import br.com.fusiondms.core.model.entrega.Entrega
import br.com.fusiondms.feature.entregas.databinding.ItemEntregaChildBinding
import br.com.fusiondms.feature.entregas.util.EventoEntregaId.ENTREGA_ADIADA
import br.com.fusiondms.feature.entregas.util.EventoEntregaId.ENTREGA_CHEGADA_CLIENTE
import br.com.fusiondms.feature.entregas.util.EventoEntregaId.ENTREGA_DEVOLVIDA_PARCIAL
import br.com.fusiondms.feature.entregas.util.EventoEntregaId.ENTREGA_DEVOLVIDA_TOTAL
import br.com.fusiondms.feature.entregas.util.EventoEntregaId.ENTREGA_INICIADA
import br.com.fusiondms.feature.entregas.util.EventoEntregaId.ENTREGA_PENDENTE
import br.com.fusiondms.feature.entregas.util.EventoEntregaId.ENTREGA_REALIZADA

class EntregasChildAdapter()
    : ListAdapter<Entrega, EntregasChildAdapter.EntregasViewHolder>(DiffCallback) {

    var onEntregaClickListener: (binding: ItemEntregaChildBinding, entrega: Entrega) -> Unit = { _:ItemEntregaChildBinding, _: Entrega -> }
    var onEntregaLongClickListener: (entrega: Entrega, position: Int) -> Unit = { _: Entrega, _: Int ->}

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

    override fun getItemId(position: Int) = position.toLong()

    inner class EntregasViewHolder(
        private var binding: ItemEntregaChildBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(entrega: Entrega) {
            val context = binding.root.context
            //Transition
//            ViewCompat.setTransitionName(binding.tvStatus, "status_${entrega.idEntrega}")
//            ViewCompat.setTransitionName(binding.tvOrdemEntrega, "ordem_entrega_${entrega.idEntrega}")
            ViewCompat.setTransitionName(binding.root, "card_${entrega.idEntrega}")
            //Transition

            when(entrega.statusEntrega.toInt()) {
                ENTREGA_PENDENTE -> {
                    binding.root.setCardBackgroundColor(context.getColorFromAttr(com.google.android.material.R.attr.colorSurface))
                    binding.tvStatus.text = Html.fromHtml("<b>${context.getString(R.string.label_pendente)}</b>", 0)
                    binding.tvStatus.setTextColor(context.getColorFromAttr(com.google.android.material.R.attr.colorOnSurface))
                }
                ENTREGA_CHEGADA_CLIENTE -> {
                    binding.root.setCardBackgroundColor(context.getColor(R.color.brand_gomboje_orange_ripple))
                    binding.tvStatus.text = Html.fromHtml("<b>${context.getString(R.string.label_aguardando)}</b>", 0)
                    binding.tvStatus.setTextColor(context.getColor(R.color.brand_gomboje_orange))
                }
                ENTREGA_INICIADA -> {
                    binding.root.setCardBackgroundColor(context.getColor(R.color.brand_celtic_blue_ripple))
                    binding.tvStatus.text = Html.fromHtml("<b>${context.getString(R.string.label_em_andamento)}</b>", 0)
                    binding.tvStatus.setTextColor(context.getColor(R.color.brand_celtic_blue))
                }
                ENTREGA_REALIZADA -> {
                    binding.root.setCardBackgroundColor(context.getColor(R.color.brand_green_success_ripple))
                    binding.tvStatus.text = Html.fromHtml("<b>${context.getString(R.string.label_entregue)}</b>", 0)
                    binding.tvStatus.setTextColor(context.getColor(R.color.brand_green_success))
                }
                ENTREGA_DEVOLVIDA_PARCIAL, ENTREGA_DEVOLVIDA_TOTAL -> {
                    binding.root.setCardBackgroundColor(context.getColor(R.color.brand_red_ripple))
                    binding.tvStatus.text = Html.fromHtml("<b>${context.getString(R.string.label_devolvida)}</b>", 0)
                    binding.tvStatus.setTextColor(context.getColor(R.color.brand_red))
                }
                ENTREGA_ADIADA -> {
                    binding.root.setCardBackgroundColor(context.getColor(R.color.brand_selective_yellow_ripple))
                    binding.tvStatus.text = Html.fromHtml("<b>${context.getString(R.string.label_adiada)}</b>", 0)
                    binding.tvStatus.setTextColor(context.getColor(R.color.brand_selective_yellow))
                }
                else -> {
                    binding.root.setCardBackgroundColor(context.getColorFromAttr(com.google.android.material.R.attr.colorSurface))
                    binding.tvStatus.setTextColor(context.getColorFromAttr(com.google.android.material.R.attr.colorOnSurface))
                }
            }

            binding.tvOrdemEntrega.text = Html.fromHtml("<b>${entrega.idEntrega}</b>", 0)
            binding.tvNotaFiscal.text = Html.fromHtml(context.getString(R.string.label_nf_pernota, entrega.numeroNotaFiscal), 0)
            binding.tvValor.text = Html.fromHtml(context.getString(R.string.label_valor, entrega.valor), 0)

            itemView.setOnClickListener {
                onEntregaClickListener(binding, entrega)
            }

            itemView.setOnLongClickListener {
                onEntregaLongClickListener(entrega, adapterPosition)
                return@setOnLongClickListener true
            }

//            setIsRecyclable(false)
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Entrega>() {
        override fun areItemsTheSame(oldItem: Entrega, newItem: Entrega) =
            oldItem.statusEntrega.toInt() == newItem.statusEntrega.toInt() && oldItem.idEntrega == newItem.idEntrega

        override fun areContentsTheSame(oldItem: Entrega, newItem: Entrega) =
            oldItem == newItem
    }
}