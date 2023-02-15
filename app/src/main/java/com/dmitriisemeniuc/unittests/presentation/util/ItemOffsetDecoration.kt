package com.dmitriisemeniuc.unittests.presentation.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.dmitriisemeniuc.unittests.common.extensions.dpToPixel

class ItemOffsetDecoration(
    private val itemOffset: Int = 4.dpToPixel()
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val itemPosition = (view.layoutParams as RecyclerView.LayoutParams).viewAdapterPosition

        val top = if (itemPosition == 0) itemOffset * 2 else itemOffset
        val bottom = if (itemPosition == state.itemCount - 1) itemOffset * 2 else itemOffset

        outRect.set(0, top, 0, bottom)
    }
}