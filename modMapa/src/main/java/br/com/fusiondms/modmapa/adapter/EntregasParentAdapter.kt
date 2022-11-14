package br.com.fusiondms.modmapa.adapter

import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.fusiondms.modmapa.util.CustomHorizontalLayoutManager
import br.com.fusiondms.modmapa.databinding.ItemEntregaParentBinding
import br.com.fusiondms.modmodel.Entrega
import br.com.fusiondms.modmodel.EntregasPorCliente

class EntregasParentAdapter :
    ListAdapter<EntregasPorCliente, EntregasParentAdapter.EntregasViewHolder>(DiffCallback) {

    var onClienteClickListener: (cliente: String) -> Unit = {}
    var onEntregaClickListener: (entrega: Entrega) -> Unit = {}

    private val viewPool = RecyclerView.RecycledViewPool()

    private lateinit var childAdapter: EntregasChildAdapter

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

        fun bind(cliente: EntregasPorCliente) {
            childAdapter = EntregasChildAdapter(cliente.entregas)

            binding.tvCliente.text = Html.fromHtml(cliente.cliente, 0)
            binding.tvLocal.text = Html.fromHtml(cliente.local, 0)

            val childLayoutManager = if (cliente.entregas.size == 1) {
                LinearLayoutManager(
                    binding.root.context,
                    RecyclerView.HORIZONTAL,
                    false
                )
            } else {
                CustomHorizontalLayoutManager(
                    binding.root.context,
                    RecyclerView.HORIZONTAL,
                    false
                )
            }
            childLayoutManager.initialPrefetchItemCount = 4
            binding.rvEntregasChild.apply {
                layoutManager = childLayoutManager
                adapter = childAdapter
                setRecycledViewPool(viewPool)
            }

            childAdapter.onEntregaClickListener = { entrega ->
                onEntregaClickListener(entrega)
            }

//            itemView.setOnClickListener {
//                onClienteClickListener("Clicou no cliente")
//            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<EntregasPorCliente>() {
        override fun areItemsTheSame(oldItem: EntregasPorCliente, newItem: EntregasPorCliente) =
            oldItem.idCliente == newItem.idCliente

        override fun areContentsTheSame(oldItem: EntregasPorCliente, newItem: EntregasPorCliente) =
            oldItem == newItem
    }
}