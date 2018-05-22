package com.android.oleksandrpriadko.recycler_decoration;

import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class ItemDecorationMargin extends RecyclerView.ItemDecoration {

    private int mLeftMargin;
    private int mTopMargin;
    private int mRightMargin;
    private int mBottomMargin;
    private boolean mFirstItemMargin;
    private boolean mLastItemMargin;
    private int mLinearLayoutManagerOrientation;

    public ItemDecorationMargin(int leftMargin,
                                int topMargin,
                                int rightMargin,
                                int bottomMargin,
                                boolean firstItemMargin,
                                boolean lastItemMargin,
                                int linearLayoutManagerOrientation) {

        this.mLeftMargin = leftMargin;
        this.mTopMargin = topMargin;
        this.mRightMargin = rightMargin;
        this.mBottomMargin = bottomMargin;
        this.mFirstItemMargin = firstItemMargin;
        this.mLastItemMargin = lastItemMargin;
        this.mLinearLayoutManagerOrientation = linearLayoutManagerOrientation;
    }

    public ItemDecorationMargin(int margin,
                                boolean firstItemMargin,
                                boolean lastItemMargin,
                                int linearLayoutManagerOrientation) {

        this(margin,
            margin,
            margin,
            margin,
            firstItemMargin,
            lastItemMargin,
            linearLayoutManagerOrientation);
    }

    @Override
    public void getItemOffsets(Rect outRect,
                               View view,
                               RecyclerView parent,
                               RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        boolean isFirstItem = position == 0;
        boolean isLastItem = position == position - 1;

        if (mLinearLayoutManagerOrientation == LinearLayoutManager.HORIZONTAL) {
            defineOffsetHorizontalOrientation(outRect, isFirstItem, isLastItem);
        } else if (mLinearLayoutManagerOrientation == LinearLayoutManager.VERTICAL) {
            defineOffsetVerticalOrientation(outRect, isFirstItem, isLastItem);
        }
    }

    private void defineOffsetVerticalOrientation(Rect outRect,
                                                 boolean isFirstItem,
                                                 boolean isLastItem) {
        outRect.left = mLeftMargin;
        outRect.right = mRightMargin;

        if (isFirstItem) {
            if (mFirstItemMargin) {
                outRect.top = mTopMargin;
            }
            outRect.bottom = mBottomMargin;
        } else if (isLastItem) {
            if (mLastItemMargin) {
                outRect.bottom = mBottomMargin;
            }
        } else {
            outRect.bottom = mBottomMargin;
        }
    }

    private void defineOffsetHorizontalOrientation(Rect outRect,
                                                   boolean isFirstItem,
                                                   boolean isLastItem) {
        outRect.top = mTopMargin;
        outRect.bottom = mBottomMargin;

        if (isFirstItem) {
            if (mFirstItemMargin) {
                outRect.left = mLeftMargin;
            }
            outRect.right = mRightMargin;
        } else if (isLastItem) {
            if (mLastItemMargin) {
                outRect.right = mRightMargin;
            }
        }
    }
}
