package br.com.fusiondms.feature.entregas.interfaces

import androidx.recyclerview.widget.RecyclerView

interface NestedRecyclerViewViewHolder {
    fun getId(): Int
    fun getLayoutManager(): RecyclerView.LayoutManager?
}