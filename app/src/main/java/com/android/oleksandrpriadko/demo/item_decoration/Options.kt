package com.android.oleksandrpriadko.demo.item_decoration

import androidx.annotation.IntDef
import androidx.recyclerview.widget.RecyclerView

class Options {

    @Manager
    var managerType = LINEAR
    @RecyclerView.Orientation
    @get:RecyclerView.Orientation
    var orientation = RecyclerView.VERTICAL
    var isFirstMargin = false
    var isLastMargin = false
    var isIncludeEdge = false
    var margin = 0

    @IntDef(LINEAR, GRID)
    private annotation class Manager

    companion object {

        const val LINEAR = 1
        const val GRID = 2
    }

}
