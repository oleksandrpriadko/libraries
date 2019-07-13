package com.android.oleksandrpriadko.recycler_decoration

import android.graphics.Rect
import android.view.View

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ItemDecorationMargin(private val mLeftMargin: Int,
                           private val mTopMargin: Int,
                           private val mRightMargin: Int,
                           private val mBottomMargin: Int,
                           private val mFirstItemMargin: Boolean,
                           private val mLastItemMargin: Boolean,
                           private val mLinearLayoutManagerOrientation: Int)
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

        if (mLinearLayoutManagerOrientation == LinearLayoutManager.HORIZONTAL) {
            defineOffsetHorizontalOrientation(outRect, isFirstItem, isLastItem)
        } else if (mLinearLayoutManagerOrientation == LinearLayoutManager.VERTICAL) {
            defineOffsetVerticalOrientation(outRect, isFirstItem, isLastItem)
        }
    }

    private fun defineOffsetVerticalOrientation(outRect: Rect,
                                                isFirstItem: Boolean,
                                                isLastItem: Boolean) {
        outRect.left = mLeftMargin
        outRect.right = mRightMargin

        if (isFirstItem) {
            if (mFirstItemMargin) {
                outRect.top = mTopMargin
            }
            outRect.bottom = mBottomMargin
        } else if (isLastItem) {
            if (mLastItemMargin) {
                outRect.bottom = mBottomMargin
            }
        } else {
            outRect.bottom = mBottomMargin
        }
    }

    private fun defineOffsetHorizontalOrientation(outRect: Rect,
                                                  isFirstItem: Boolean,
                                                  isLastItem: Boolean) {
        outRect.top = mTopMargin
        outRect.bottom = mBottomMargin

        if (isFirstItem) {
            if (mFirstItemMargin) {
                outRect.left = mLeftMargin
            }
            outRect.right = mRightMargin
        } else if (isLastItem) {
            if (mLastItemMargin) {
                outRect.right = mRightMargin
            }
        } else {
            outRect.right = mRightMargin
        }
    }
}
