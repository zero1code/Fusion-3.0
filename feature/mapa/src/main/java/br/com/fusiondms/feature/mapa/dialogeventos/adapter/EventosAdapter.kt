package br.com.fusiondms.modmapa.dialogeventos.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.fusiondms.modcommon.getColorFromAttr
import br.com.fusiondms.modmapa.databinding.ItemDialogEventoBinding
import br.com.fusiondms.modmodel.entrega.Evento

class EventosAdapter : ListAdapter<Evento, EventosAdapter.EventosViewHolder>(DiffCallback) {

    var onEventoClickListener: (idEvento: Int) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventosViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return EventosViewHolder(
            ItemDialogEventoBinding.inflate(layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: EventosViewHolder, position: Int) {
        val evento = getItem(position)
        holder.bind(evento)
    }

    inner class EventosViewHolder(
        private var binding: ItemDialogEventoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(evento: Evento) {
            val context = binding.root.context
            with(binding) {
                ivIcone.setImageResource(evento.drawable)
                tvTitulo.text = evento.name

                binding.root.isEnabled = !evento.isCliked
                if (evento.isCliked) {
                    tvTitulo.setTextColor(context.getColorFromAttr(com.google.android.material.R.attr.colorSurface))
                    ivIcone.setColorFilter(context.getColorFromAttr(com.google.android.material.R.attr.colorSurface))
                    when(evento.idEvento) {
                        4 -> binding.root.setCardBackgroundColor(context.getColor(br.com.fusiondms.modcommon.R.color.brand_red))
                        5 -> binding.root.setCardBackgroundColor(context.getColor(br.com.fusiondms.modcommon.R.color.brand_selective_yellow))
                        else -> binding.root.setCardBackgroundColor(context.getColor(br.com.fusiondms.modcommon.R.color.brand_green_success))
                    }

                } else {
                    tvTitulo.setTextColor(context.getColorFromAttr(com.google.android.material.R.attr.colorOnSurface))
                    ivIcone.setColorFilter(context.getColorFromAttr(com.google.android.material.R.attr.colorOnSurface))
                    binding.root.setCardBackgroundColor(context.getColorFromAttr(com.google.android.material.R.attr.colorSurface))
                }
            }

            itemView.setOnClickListener {
                onEventoClickListener(evento.idEvento)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Evento>() {
        override fun areItemsTheSame(oldItem: Evento, newItem: Evento) =
            oldItem.idEvento == newItem.idEvento

        override fun areContentsTheSame(oldItem: Evento, newItem: Evento) =
            oldItem == newItem
    }


}