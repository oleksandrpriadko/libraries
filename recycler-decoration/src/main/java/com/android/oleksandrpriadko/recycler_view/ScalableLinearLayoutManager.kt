package com.android.oleksandrpriadko.recycler_view

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs
import kotlin.math.sqrt

class ScalableLinearLayoutManager : LinearLayoutManager {

    constructor(context: Context?)
            : super(context)

    constructor(context: Context?, orientation: Int, reverseLayout: Boolean)
            : super(context, orientation, reverseLayout)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int)
            : super(context, attrs, defStyleAttr, defStyleRes)

    private lateinit var recyclerView: RecyclerView

    override fun onAttachedToWindow(view: RecyclerView?) {
        super.onAttachedToWindow(view)
        recyclerView = view!!

        LinearSnapHelper().attachToRecyclerView(recyclerView)
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State) {
        super.onLayoutChildren(recycler, state)
        scaleDownView()
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        return if (orientation == HORIZONTAL) {
            val scrolled = super.scrollHorizontallyBy(dx, recycler, state)
            scaleDownView()
            scrolled
        } else {
            0
        }
    }

    private fun scaleDownView() {
        val mid = width / 2.0f
        for (i in 0 until childCount) {

            val child = getChildAt(i)
            val childMid = (getDecoratedLeft(child!!) + getDecoratedRight(child)) / 2.0f
            val distanceFromCenter = abs(mid - childMid)

            val scale = 1 - sqrt((distanceFromCenter / width).toDouble()).toFloat() * 0.12f

            child.scaleX = scale
            child.scaleY = scale
        }
    }

//    override fun onScrollStateChanged(state: Int) {
//
//        if (state == RecyclerView.SCROLL_STATE_IDLE) {
//
//            val recyclerViewCenterX = getRecyclerViewCenterX()
//            var minDistance = recyclerView.width
//            var position = -1
//            for (i in 0 until recyclerView.childCount) {
//                val child = recyclerView.getChildAt(i)
//                val childCenterX = getDecoratedLeft(child) + (getDecoratedRight(child) - getDecoratedLeft(child)) / 2
//                val newDistance = abs(childCenterX - recyclerViewCenterX)
//                if (newDistance < minDistance) {
//                    minDistance = newDistance
//                    position = recyclerView.getChildLayoutPosition(child)
//                }
//            }
//        }
//    }

//    private fun getRecyclerViewCenterX(): Int {
//        return (recyclerView.right - recyclerView.left) / 2 + recyclerView.left
//    }
}