package com.android.oleksandrpriadko.recycler_decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ItemDecorationVerticalGridMargin : RecyclerView.ItemDecoration {
    private val spanCount: Int
    private var left: Int = 0
    private var top: Int = 0
    private var right: Int = 0
    private var bottom: Int = 0
    private var includeEdge: Boolean = false

    constructor(spanCount: Int, left: Int, top: Int, right: Int, bottom: Int, includeEdge: Boolean) {
        this.spanCount = spanCount
        this.left = left
        this.top = top
        this.right = right
        this.bottom = bottom
        this.includeEdge = includeEdge
    }

    constructor(spanCount: Int, space: Int, includeEdge: Boolean) {
        this.spanCount = spanCount
        this.left = space
        this.top = space
        this.right = space
        this.bottom = space
        this.includeEdge = includeEdge
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val column = position % spanCount
        val row = position / spanCount

        if (includeEdge) {
            outRect.left = left - column * left / spanCount
            outRect.right = (column + 1) * right / spanCount

            if (position < spanCount) {
                outRect.top = top
            }
            outRect.bottom = bottom
        } else {
            outRect.left = column * left / spanCount
            outRect.right = right - (column + 1) * right / spanCount
            if (position >= spanCount) {
                outRect.top = top
            }
        }
    }
}
