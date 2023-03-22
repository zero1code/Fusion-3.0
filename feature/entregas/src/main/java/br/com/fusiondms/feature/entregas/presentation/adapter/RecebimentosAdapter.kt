package br.com.fusiondms.feature.entregas.presentation.adapter


import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.fusiondms.core.common.converterDataParatextoLegivel
import br.com.fusiondms.core.common.toMoedaLocal
import br.com.fusiondms.core.model.recebimento.Recebimento
import br.com.fusiondms.feature.entregas.R
import br.com.fusiondms.feature.entregas.databinding.ItemRecebimentoBinding

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
                ivIconeCartao.setImageDrawable(iconeBandeira(context, recebimento.bandeira))
            }
        }

        private fun iconeBandeira(context: Context, tipo: String): Drawable? {
            return when(tipo) {
                "DINHEIRO" -> AppCompatResources.getDrawable(context, br.com.fusiondms.core.common.R.drawable.ic_dinheiro)
                "VISA" -> AppCompatResources.getDrawable(context, br.com.fusiondms.core.common.R.drawable.ic_cartao_visa)
                "MASTERCARD" -> AppCompatResources.getDrawable(context, br.com.fusiondms.core.common.R.drawable.ic_cartao_mastercard)
                "ELO" -> AppCompatResources.getDrawable(context, br.com.fusiondms.core.common.R.drawable.ic_cartao_elo)
                "PIX" -> AppCompatResources.getDrawable(context, br.com.fusiondms.core.common.R.drawable.ic_pix)
                else -> AppCompatResources.getDrawable(context, br.com.fusiondms.core.common.R.drawable.ic_dinheiro)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Recebimento>() {
        override fun areItemsTheSame(oldItem: Recebimento, newItem: Recebimento) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Recebimento, newItem: Recebimento) =
            oldItem == newItem
    }
}