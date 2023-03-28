package br.com.fusiondms.feature.entregas.presentation.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.fusiondms.core.common.converterDataParatextoLegivel
import br.com.fusiondms.core.common.toMoedaLocal
import br.com.fusiondms.core.model.recebimento.Recebimento
import br.com.fusiondms.feature.entregas.R
import br.com.fusiondms.feature.entregas.databinding.ItemRecebimentoBinding
import br.com.fusiondms.feature.pagamentos.utils.ETipoPagamento

class RecebimentosAdapter() : ListAdapter<Recebimento, RecebimentosAdapter.RecebimentoViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecebimentoViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return RecebimentoViewHolder(
            ItemRecebimentoBinding.inflate(layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecebimentoViewHolder, position: Int) {
        val recebimento = getItem(position)
        holder.bind(recebimento)
    }

    inner class RecebimentoViewHolder(private var binding: ItemRecebimentoBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(recebimento: Recebimento) {
            val context = binding.root.context
            with(binding) {
                tvFormaPagamento.text = recebimento.formaPagamento
                tvDataPagamento.text = converterDataParatextoLegivel(recebimento.dataRecebimento)
                tvValor.text = recebimento.valor.toMoedaLocal()
                ivIconeCartao.setImageResource(iconeBandeira(recebimento.tipo, recebimento.bandeira))
            }
        }

        private fun iconeBandeira(tipo: String, bandeira: String): Int {
            var iconeSelecionado = br.com.fusiondms.core.common.R.drawable.ic_dinheiro
            for (tipoErp in ETipoPagamento.values()) {
                if (tipoErp.toString() == tipo) {
                    iconeSelecionado = when (bandeira) {
                        "VISA" -> br.com.fusiondms.core.common.R.drawable.ic_cartao_visa
                        "MASTERCARD" -> br.com.fusiondms.core.common.R.drawable.ic_cartao_mastercard
                        "ELO" -> br.com.fusiondms.core.common.R.drawable.ic_cartao_elo
                        else -> tipoErp.icone
                    }
                    break
                }
            }
            return iconeSelecionado
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Recebimento>() {
        override fun areItemsTheSame(oldItem: Recebimento, newItem: Recebimento) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Recebimento, newItem: Recebimento) =
            oldItem == newItem
    }
}