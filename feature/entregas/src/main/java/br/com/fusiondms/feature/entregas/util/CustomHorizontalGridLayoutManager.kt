package br.com.fusiondms.feature.entregas.util

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CustomHorizontalGridLayoutManager @JvmOverloads constructor(
    context: Context?,
    @RecyclerView.Orientation orientation: Int = RecyclerView.HORIZONTAL,
    spanCount: Int = 2,
    reverseLayout: Boolean = false
) : GridLayoutManager(context, spanCount, orientation, reverseLayout) {

    override fun generateDefaultLayoutParams() =
        scaledLayoutParams(super.generateDefaultLayoutParams())

    override fun generateLayoutParams(lp: ViewGroup.LayoutParams?) =
        scaledLayoutParams(super.generateLayoutParams(lp))

    override fun generateLayoutParams(c: Context?, attrs: AttributeSet?) =
        scaledLayoutParams(super.generateLayoutParams(c, attrs))

    private fun scaledLayoutParams(layoutParams: RecyclerView.LayoutParams) =
        layoutParams.apply {
            when (orientation) {
                RecyclerView.HORIZONTAL -> width = (horizontalSpace * ratio).toInt()
                RecyclerView.VERTICAL -> height = (verticalSpace * ratio).toInt()
            }
        }

    private val horizontalSpace get() = width - paddingStart - paddingEnd

    private val verticalSpace get() = height - paddingTop - paddingBottom

    private val ratio = 0.8f // change to 0.7f for 70%
}