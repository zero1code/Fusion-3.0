/*
 * Copyright (c) 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.fusiondms.modaceitarcarga.presentation.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.fusiondms.modaceitarcarga.databinding.ItemCargaListBinding
import br.com.fusiondms.modmodel.Romaneio
import java.util.*

class CargasAdapter() :
    ListAdapter<Romaneio, CargasAdapter.CargasViewHolder>(DiffCallback) {

    var onRomaneioClickListener: (carga: Romaneio, position: Int) -> Unit = { _: Romaneio, _: Int -> }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CargasViewHolder {
        val context = parent.context
        return CargasViewHolder(
            ItemCargaListBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: CargasViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CargasViewHolder(private var binding: ItemCargaListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(carga: Romaneio) {
            binding.tvRomaneioId.text = carga.idRomaneio.toString()
            binding.tvDestino.text = carga.destino
            binding.tvKm.text = "${carga.kmTotal}  KM"

            val random = Random()
            val color = Color.rgb(
                random.nextInt(256),
                random.nextInt(256),
                random.nextInt(256)
            )
            binding.viewColorIdentifier.setBackgroundColor(color)
            carga.corIdentificador = color

            itemView.setOnClickListener {
                onRomaneioClickListener(carga, adapterPosition)
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Romaneio>() {
            override fun areItemsTheSame(oldItem: Romaneio, newItem: Romaneio): Boolean {
                return (oldItem.id == newItem.id ||
                        oldItem.destino == newItem.destino)
            }

            override fun areContentsTheSame(oldItem: Romaneio, newItem: Romaneio): Boolean {
                return oldItem == newItem
            }
        }
    }
}
