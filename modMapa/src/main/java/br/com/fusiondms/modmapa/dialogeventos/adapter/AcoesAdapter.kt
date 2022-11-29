package br.com.fusiondms.modmapa.dialogeventos.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.fusiondms.modmapa.databinding.ItemDialogEventoBinding
import br.com.fusiondms.modmodel.Acao

class AcoesAdapter : ListAdapter<Acao, AcoesAdapter.AcoesViewHolder>(DiffCallback) {

    var onAcaoClickListener: (idAcao: Int) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AcoesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return AcoesViewHolder(
            ItemDialogEventoBinding.inflate(layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: AcoesViewHolder, position: Int) {
        val acao = getItem(position)
        holder.bind(acao)
    }

    inner class AcoesViewHolder(private var binding: ItemDialogEventoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(acao: Acao) {
            with(binding) {
                ivIcone.setImageResource(acao.drawable)
                tvTitulo.text = acao.name
            }

            itemView.setOnClickListener {
                onAcaoClickListener(acao.idAcao)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Acao>() {
        override fun areItemsTheSame(oldItem: Acao, newItem: Acao) =
            oldItem.idAcao == newItem.idAcao

        override fun areContentsTheSame(oldItem: Acao, newItem: Acao) =
            oldItem == newItem
    }
}