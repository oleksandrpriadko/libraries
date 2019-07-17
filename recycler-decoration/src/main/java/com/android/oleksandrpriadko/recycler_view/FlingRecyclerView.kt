package com.android.oleksandrpriadko.recycler_view

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import com.android.oleksandrpriadko.recycler_decoration.R
import kotlin.math.roundToInt

class FlingRecyclerView : RecyclerView {

    var flingMode = FlingMode.NORMAL

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int)
            : super(context, attrs, defStyle) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        attrs?.let {
            context.theme.obtainStyledAttributes(attrs,
                    R.styleable.FlingRecyclerView,
                    0,
                    0).apply {
                flingMode = FlingMode.find(getInt(R.styleable.FlingRecyclerView_flingMode, 1))
                recycle()
            }
        }
    }

    override fun fling(velocityX: Int, velocityY: Int): Boolean {
        val localLayoutManager = layoutManager
        localLayoutManager?.let {
            if (localLayoutManager.canScrollVertically()) {
                val velocityYChanged = velocityY * flingMode.flingFactor
                return super.fling(velocityX, velocityYChanged.roundToInt())
            }

            if (localLayoutManager.canScrollHorizontally()) {
                val velocityXChanged = velocityX * flingMode.flingFactor
                return super.fling(velocityXChanged.roundToInt(), velocityY)
            }
        }
        return super.fling(velocityX, velocityY)
    }
}

enum class FlingMode(val flingFactor: Float) {
    LOW(0.2f),
    NORMAL(1f),
    FAST(1.2f);

    companion object {
        fun find(enumFromAttr: Int): FlingMode {
            return when (enumFromAttr) {
                0 -> LOW
                1 -> NORMAL
                2 -> FAST
                else -> NORMAL
            }
        }
    }
}