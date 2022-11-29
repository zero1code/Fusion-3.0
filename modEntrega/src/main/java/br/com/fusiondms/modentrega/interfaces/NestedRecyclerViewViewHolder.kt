package br.com.fusiondms.modentrega.interfaces

import androidx.recyclerview.widget.RecyclerView

interface NestedRecyclerViewViewHolder {
    fun getId(): Int
    fun getLayoutManager(): RecyclerView.LayoutManager?
}