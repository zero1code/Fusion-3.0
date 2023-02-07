package br.com.fusiondms.feature.entregas.util

import android.os.Parcelable
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.fusiondms.feature.entregas.interfaces.NestedRecyclerViewViewHolder
import java.lang.ref.WeakReference

abstract class NestedRecyclerViewStateRecoverAdapter<T, VH : RecyclerView.ViewHolder>(
    diffUtil: DiffUtil.ItemCallback<T>
) : ListAdapter<T, VH>(diffUtil) {
    private val layoutManagerStates = hashMapOf<Int, Parcelable?>()
    private val visibleScrollableViews = hashMapOf<Int, ViewHolderRef>()

    @CallSuper
    override fun onViewRecycled(holder: VH) {
        if (holder is NestedRecyclerViewViewHolder) {
            // Save the scroll position state (LayoutManager state)
            val state = holder.getLayoutManager()?.onSaveInstanceState()
            layoutManagerStates[holder.getId()] = state

            // State saved and view is not visible
            visibleScrollableViews.remove(holder.hashCode())
        }
        super.onViewRecycled(holder)
    }

    @CallSuper
    override fun onBindViewHolder(holder: VH, position: Int) {
        if (holder is NestedRecyclerViewViewHolder) {
            holder.getLayoutManager()?.let {
                // Retrieve and set the saved LayoutManager state
                val state: Parcelable? = layoutManagerStates[holder.getId()]
                if (state != null) {
                    it.onRestoreInstanceState(state)
                } else {
                    // We need to reset the scroll position when no state needs to be restored
                    it.scrollToPosition(0)
                }
            }
            visibleScrollableViews[holder.hashCode()] = ViewHolderRef(
                holder.getId(),
                WeakReference(holder as NestedRecyclerViewViewHolder)
            )
        }
    }

    @CallSuper
    override fun submitList(list: List<T>?) {
        // We need to save the state from visible views before updating/setting the list to preserve
        // their states
        saveState()
        super.submitList(list)
    }

    private fun saveState() {
        visibleScrollableViews.values.forEach { item ->
            item.reference.get()?.let {
                layoutManagerStates[item.id] = it.getLayoutManager()?.onSaveInstanceState()
            }
        }
        visibleScrollableViews.clear()
    }

    fun clearState() {
        layoutManagerStates.clear()
        visibleScrollableViews.clear()
    }

    private data class ViewHolderRef(
        val id: Int,
        val reference: WeakReference<NestedRecyclerViewViewHolder>
    )
}