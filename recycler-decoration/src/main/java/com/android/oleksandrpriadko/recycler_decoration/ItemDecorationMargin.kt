package com.android.oleksandrpriadko.recycler_decoration

import android.graphics.Rect
import android.view.View

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ItemDecorationMargin(private val leftMargin: Int,
                           private val topMargin: Int,
                           private val rightMargin: Int,
                           private val bottomMargin: Int,
                           private val firstItemMargin: Boolean,
                           private val lastItemMargin: Boolean,
                           private val linearLayoutManagerOrientation: Int)
    : RecyclerView.ItemDecoration() {

    constructor(margin: Int,
                firstItemMargin: Boolean,
                lastItemMargin: Boolean,
                linearLayoutManagerOrientation: Int)
            : this(
            margin,
            margin,
            margin,
            margin,
            firstItemMargin,
            lastItemMargin,
            linearLayoutManagerOrientation)

    override fun getItemOffsets(outRect: Rect,
                                view: View,
                                parent: RecyclerView,
                                state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val isFirstItem = position == 0
        val isLastItem = position == parent.adapter!!.itemCount - 1

        if (linearLayoutManagerOrientation == LinearLayoutManager.HORIZONTAL) {
            defineOffsetHorizontalOrientation(outRect, isFirstItem, isLastItem)
        } else if (linearLayoutManagerOrientation == LinearLayoutManager.VERTICAL) {
            defineOffsetVerticalOrientation(outRect, isFirstItem, isLastItem)
        }
    }

    private fun defineOffsetVerticalOrientation(outRect: Rect,
                                                isFirstItem: Boolean,
                                                isLastItem: Boolean) {
        outRect.left = leftMargin
        outRect.right = rightMargin

        if (isFirstItem) {
            if (firstItemMargin) {
                outRect.top = topMargin
            }
            outRect.bottom = bottomMargin
        } else if (isLastItem) {
            if (lastItemMargin) {
                outRect.bottom = bottomMargin
            }
        } else {
            outRect.bottom = bottomMargin
        }
    }

    private fun defineOffsetHorizontalOrientation(outRect: Rect,
                                                  isFirstItem: Boolean,
                                                  isLastItem: Boolean) {
        outRect.top = topMargin
        outRect.bottom = bottomMargin

        if (isFirstItem) {
            if (firstItemMargin) {
                outRect.left = leftMargin
            }
            outRect.right = rightMargin
        } else if (isLastItem) {
            if (lastItemMargin) {
                outRect.right = rightMargin
            }
        } else {
            outRect.right = rightMargin
        }
    }
}
