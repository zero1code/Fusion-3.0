package br.com.fusiondms.feature.jornadatrabalho.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.fusiondms.core.common.converterDataParaHorasMinutos
import br.com.fusiondms.core.common.getColorFromAttr
import br.com.fusiondms.core.model.jornadatrabalho.Colaborador
import br.com.fusiondms.core.model.jornadatrabalho.JornadaTrabalho
import br.com.fusiondms.core.model.jornadatrabalho.RegistroPonto
import br.com.fusiondms.feature.jornadatrabalho.databinding.ItemJornadaColaboradorBinding
import br.com.fusiondms.feature.jornadatrabalho.databinding.ItemJornadaHorarioRegistroBinding
import br.com.fusiondms.core.common.R

class JornadaColaboradoresAdapter() :
    ListAdapter<JornadaTrabalho, JornadaColaboradoresAdapter.JornadaColaboradoresViewHolder>(
        DiffCallback
    ) {

    var onColaboradorClickListener : (colaborador: Colaborador) -> Unit = {}

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): JornadaColaboradoresViewHolder {
        return JornadaColaboradoresViewHolder(
            ItemJornadaColaboradorBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: JornadaColaboradoresViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class JornadaColaboradoresViewHolder(private var binding: ItemJornadaColaboradorBinding) :
        RecyclerView.ViewHolder(binding.root) {

            fun bind(jornadaTrabalho: JornadaTrabalho) {
                val colaborador = jornadaTrabalho.colaborador
                binding.apply {
                    tvNome.text = colaborador.nome
                    tvFuncao.text = colaborador.funcao

                    if (jornadaTrabalho.registroPonto.isEmpty()) {
                        tvSemRegistro.visibility = View.VISIBLE
                        gridLayoutRegistrosPonto.visibility = View.INVISIBLE
                    } else {
                        tvSemRegistro.visibility = View.INVISIBLE
                        gridLayoutRegistrosPonto.visibility = View.VISIBLE
                        inserirHorarioRegistro(jornadaTrabalho.registroPonto)
                    }
                }

                itemView.setOnClickListener {
                    onColaboradorClickListener(colaborador)
                }
            }

        private fun inserirHorarioRegistro(registroPonto: List<RegistroPonto>) {
            val batidas = arrayListOf("08:00", "12:02", "13:03", "17:01")

            registroPonto.mapIndexed { position, registro ->
                val context = binding.root.context
                val layoutInflater = LayoutInflater.from(context)
                val registroPontoBinding = ItemJornadaHorarioRegistroBinding.inflate(layoutInflater)
                if (position == 0) {
                    registroPontoBinding.root.setPadding(0, 4, 8, 4)
                } else {
                    registroPontoBinding.root.setPadding(8, 4, 8, 4)
                }

                registroPontoBinding.tvHorarioBatida.text = converterDataParaHorasMinutos(registro.dataRegistro)
                if (position == 3) {
                    with(registroPontoBinding) {
                        tvHorarioBatida.setTextColor(context.getColor(R.color.brand_color_on_surface_riple))
                        cvHorario.setCardBackgroundColor(context.getColorFromAttr(com.google.android.material.R.attr.colorSurface))
                        cvHorario.strokeColor = context.getColor(R.color.brand_color_on_surface_riple)
                    }
                }
                binding.gridLayoutRegistrosPonto.addView(registroPontoBinding.root)
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<JornadaTrabalho>() {
            override fun areItemsTheSame(oldItem: JornadaTrabalho, newItem: JornadaTrabalho) =
                oldItem.colaborador.matricula == newItem.colaborador.matricula

            override fun areContentsTheSame(oldItem: JornadaTrabalho, newItem: JornadaTrabalho) =
                oldItem == newItem
        }
    }
}