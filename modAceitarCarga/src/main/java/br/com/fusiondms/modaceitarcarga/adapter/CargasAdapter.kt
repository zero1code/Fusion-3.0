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

package br.com.fusiondms.modaceitarcarga.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.fusiondms.modaceitarcarga.databinding.ItemCargaListBinding
import java.util.*

class CargasAdapter(private val onItemClicked: (br.com.fusiondms.modmodel.Romaneio) -> Unit) :
    ListAdapter<br.com.fusiondms.modmodel.Romaneio, CargasAdapter.SportsViewHolder>(DiffCallback) {

    private lateinit var context: Context

    class SportsViewHolder(private var binding: ItemCargaListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(romaneio: br.com.fusiondms.modmodel.Romaneio, context:Context) {
            binding.tvRomaneioId.text = romaneio.id.toString()
            binding.tvDestino.text = romaneio.destino
            binding.tvKm.text = "${romaneio.kmTotal}  KM"

            val random = Random()
            val color = Color.rgb(
                random.nextInt(256),
                random.nextInt(256),
                random.nextInt(256)
            )
            binding.viewColorIdentifier.setBackgroundColor(color)
            romaneio.corIdentificador = color
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SportsViewHolder {
        context = parent.context
        return SportsViewHolder(
            ItemCargaListBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: SportsViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClicked(current)
        }
        holder.bind(current, context)
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<br.com.fusiondms.modmodel.Romaneio>() {
            override fun areItemsTheSame(oldItem: br.com.fusiondms.modmodel.Romaneio, newItem: br.com.fusiondms.modmodel.Romaneio): Boolean {
                return (oldItem.id == newItem.id ||
                        oldItem.destino == newItem.destino)
            }

            override fun areContentsTheSame(oldItem: br.com.fusiondms.modmodel.Romaneio, newItem: br.com.fusiondms.modmodel.Romaneio): Boolean {
                return oldItem == newItem
            }
        }
    }
}
