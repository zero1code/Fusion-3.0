package br.com.fusiondms.feature.jornadatrabalho.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.fusiondms.core.common.converterDataParaDiaMesAnoHoraMinuto
import br.com.fusiondms.core.model.jornadatrabalho.ReciboRegistroPonto
import br.com.fusiondms.feature.jornadatrabalho.databinding.ItemJornadaReciboBinding

class RecibosAdapter() :
    ListAdapter<ReciboRegistroPonto, RecibosAdapter.RecibosViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecibosViewHolder {
        return RecibosViewHolder(
            ItemJornadaReciboBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecibosViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RecibosViewHolder(private var binding: ItemJornadaReciboBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(reciboRegistroPonto: ReciboRegistroPonto) {
            with(binding) {
                tvData.text = converterDataParaDiaMesAnoHoraMinuto(reciboRegistroPonto.dataRegistro)
                tvNome.text = reciboRegistroPonto.nome
//                tvStatus.text = ""
//                tvEmpresa.text = ""
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<ReciboRegistroPonto>() {
            override fun areItemsTheSame(oldItem: ReciboRegistroPonto, newItem: ReciboRegistroPonto) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: ReciboRegistroPonto,
                newItem: ReciboRegistroPonto
            ) = oldItem == newItem
        }
    }
}