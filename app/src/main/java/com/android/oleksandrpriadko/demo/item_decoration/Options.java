package com.android.oleksandrpriadko.demo.item_decoration;

import android.support.annotation.IntDef;
import android.support.v7.widget.RecyclerView;

public class Options {

    public static final int LINEAR = 1;
    public static final int GRID = 2;

    @Manager private int mManagerType = LINEAR;
    @RecyclerView.Orientation private int mOrientation = RecyclerView.VERTICAL;
    private boolean mFirstMargin = false;
    private boolean mLastMargin = false;
    private boolean mIncludeEdge = false;
    private int mMargin = 0;

    public void setManagerType(@Manager int managerType) {
        mManagerType = managerType;
    }

    public int getMargin() {
        return mMargin;
    }

    public void setMargin(int margin) {
        mMargin = margin;
    }

    @Manager
    public int getManagerType() {
        return mManagerType;
    }

    public void setOrientation(@RecyclerView.Orientation int orientation) {
        mOrientation = orientation;
    }

    @RecyclerView.Orientation
    public int getOrientation() {
        return mOrientation;
    }

    public void setFirstMargin(boolean firstMargin) {
        mFirstMargin = firstMargin;
    }

    public boolean isFirstMargin() {
        return mFirstMargin;
    }

    public void setLastMargin(boolean lastMargin) {
        mLastMargin = lastMargin;
    }

    public boolean isLastMargin() {
        return mLastMargin;
    }

    public boolean isIncludeEdge() {
        return mIncludeEdge;
    }

    public void setIncludeEdge(boolean includeEdge) {
        mIncludeEdge = includeEdge;
    }

    @IntDef({LINEAR, GRID})
    private  @interface Manager{
    }

}
