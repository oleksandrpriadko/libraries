package com.android.oleksandrpriadko.demo.cocktails

import android.content.Context
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
import com.android.oleksandrpriadko.demo.R
import com.android.oleksandrpriadko.extension.dimenPixelSize

class CocktailLoadingView : ConstraintLayout {

    private val itemSize: Float

    private val positionPointsFloat: List<PointF>

    private val animationSet: AnimationSet

    constructor(context: Context?)
            : super(context)

    constructor(context: Context?, attrs: AttributeSet?)
            : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int)
            : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        View.inflate(context, R.layout.layout_cocktail_loading, this)
        itemSize = context.dimenPixelSize(R.dimen.cocktail_size_loading_drink).toFloat()
        positionPointsFloat = listOf(
                PointF(0f, 0f),
                PointF(itemSize * 2, 0f),
                PointF(itemSize * 4, 0f),
                PointF(itemSize * 6, 0f),
                PointF(itemSize * 8, 0f),

                PointF(0f, itemSize * 2),
                PointF(itemSize * 2, itemSize * 2),
                PointF(itemSize * 4, itemSize * 2),
                PointF(itemSize * 6, itemSize * 2),
                PointF(itemSize * 8, itemSize * 2),

                PointF(0f, itemSize * 4),
                PointF(itemSize * 2, itemSize * 4),
                PointF(itemSize * 4, itemSize * 4),
                PointF(itemSize * 6, itemSize * 4),
                PointF(itemSize * 8, itemSize * 4),

                PointF(0f, itemSize * 6),
                PointF(itemSize * 2, itemSize * 6),
                PointF(itemSize * 4, itemSize * 6),
                PointF(itemSize * 6, itemSize * 6),
                PointF(itemSize * 8, itemSize * 6),

                PointF(0f, itemSize * 8),
                PointF(itemSize * 2, itemSize * 8),
                PointF(itemSize * 4, itemSize * 8),
                PointF(itemSize * 6, itemSize * 8),
                PointF(itemSize * 8, itemSize * 8),

                PointF(0f, itemSize * 10),
                PointF(itemSize * 2, itemSize * 10),
                PointF(itemSize * 4, itemSize * 10),
                PointF(itemSize * 6, itemSize * 10),
                PointF(itemSize * 8, itemSize * 10)
        )
        animationSet = AnimationUtils.loadAnimation(context, R.anim.rotation_infinite_reversed) as AnimationSet
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        requestAnimate(true)

    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        requestAnimate(false)
    }

    private fun requestAnimate(animate: Boolean) {
        if (animate) {
            for (i in 0 until childCount) {
                val view = getChildAt(i)

                if (view.animation == null) {
                    view.startAnimation(animationSet)
                } else {
                    if (!view.animation.hasStarted()) {
                        view.animation.start()
                    }
                }
            }

        } else {
            for (i in 0 until childCount) {
                val view = getChildAt(i)
                if (view.animation != null) {
                    if (!view.animation.hasStarted()) {
                        view.animation.cancel()
                    }
                }
            }
        }
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        when (visibility) {
            View.VISIBLE -> requestAnimate(true)
            else -> requestAnimate(false)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        for (i in 0 until if (childCount < positionPointsFloat.size) childCount else positionPointsFloat.size) {
            val view = getChildAt(i)
            val marginX = view.left
            val marginY = view.top
            view.translationX = positionPointsFloat[i].x - marginX
            view.translationY = positionPointsFloat[i].y - marginY
        }
    }
}
