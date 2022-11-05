package br.com.fusiondms.modmapa.adapter

import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.fusiondms.modmapa.util.CustomHorizontalLayoutManager
import br.com.fusiondms.modmapa.databinding.ItemEntregaParentBinding
import br.com.fusiondms.modmodel.Entrega

class EntregasParentAdapter :
    ListAdapter<Entrega, EntregasParentAdapter.EntregasViewHolder>(EntregasChildAdapter) {

    var onClienteClickListener: (cliente: String) -> Unit = {}
    var onEntregaClickListener: (entrega: String) -> Unit = {}


    private val childAdapter by lazy { EntregasChildAdapter() }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntregasViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return EntregasViewHolder(
            ItemEntregaParentBinding.inflate(layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: EntregasViewHolder, position: Int) {
        val cliente = getItem(position)
        holder.bind(cliente)
    }

    inner class EntregasViewHolder(
        private var binding: ItemEntregaParentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val viewPool = RecyclerView.RecycledViewPool()
        val listaEntregas =
            arrayListOf(
                Entrega(),
                Entrega(),
                Entrega(),
                Entrega(),
                Entrega(),
                Entrega()
            )

        fun bind(cliente: Entrega) {
            binding.tvCliente.text = Html.fromHtml(
                "<b>Cliente:</b> 5535<br>" +
                        "<b>Razão Social:</b> CAZAJEIRAS LTDA", 0
            )
            binding.tvLocal.text = Html.fromHtml(
                "<b>Bairro:</b> Centro<br>" +
                        "<b>Cidade:</b> CAJZEIRAS/PB<br>" +
                        "<b>Referência de entrega:</b> Próx. ao Armazém paraíba", 0
            )

            val childLayoutManager =
                CustomHorizontalLayoutManager(
                    binding.root.context,
                    RecyclerView.HORIZONTAL,
                    false
                )
            childLayoutManager.initialPrefetchItemCount = 4
            binding.rvEntregasChild.apply {
                layoutManager = childLayoutManager
                adapter = childAdapter
                setRecycledViewPool(viewPool)
            }

            childAdapter.submitList(listaEntregas)

            childAdapter.onEntregaClickListener = { entrega ->
                onEntregaClickListener(entrega)
            }

            itemView.setOnClickListener {
                onClienteClickListener("Clicou no cliente")
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Entrega>() {
        override fun areItemsTheSame(oldItem: Entrega, newItem: Entrega) =
            oldItem.idEntrega == newItem.idEntrega

        override fun areContentsTheSame(oldItem: br.com.fusiondms.modmodel.Entrega, newItem: br.com.fusiondms.modmodel.Entrega) =
            oldItem == newItem
    }
}