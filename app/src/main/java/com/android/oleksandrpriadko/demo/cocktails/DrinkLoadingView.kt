package com.android.oleksandrpriadko.demo.cocktails

import android.animation.ValueAnimator
import android.animation.ValueAnimator.INFINITE
import android.animation.ValueAnimator.REVERSE
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.VectorDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.android.oleksandrpriadko.demo.R
import com.android.oleksandrpriadko.demo.cocktails.managers.CocktailManagerFinder
import com.android.oleksandrpriadko.extension.dimenPixelSize

class DrinkLoadingView : FrameLayout {

    private var drawable: VectorDrawable = context.getDrawable(
            CocktailManagerFinder.randomPlaceholderManager.pickPlaceHolder()) as VectorDrawable

    private var animator: ValueAnimator? = null

    private var progress: Float = 0f

    private val scale = 0.3f
    private var drawableSize: Int = context.dimenPixelSize(R.dimen.cocktail_size_loading_drink)
    private var drawableRect: Rect = Rect()

    private var widthF = -1
    private var heightF = -1

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet?)
            : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int)
            : super(context, attrs, defStyleAttr, defStyleRes) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        attrs?.let {
            context.theme.obtainStyledAttributes(attrs,
                    R.styleable.DrinkLoadingView,
                    0,
                    0).apply {
                drawableSize = getDimensionPixelSize(R.styleable.DrinkLoadingView_drawableSize, drawableSize)
                recycle()
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        widthF = measuredWidth
        heightF = measuredHeight
        requestAnimate(visibility == View.VISIBLE)
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        if (visibility == View.VISIBLE) {
            drawable = context.getDrawable(
                    CocktailManagerFinder.randomPlaceholderManager.pickPlaceHolder()) as VectorDrawable
        }
        requestAnimate(visibility == View.VISIBLE)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.let {
            val centerX = widthF / 2
            val centerY = heightF / 2
            drawable.bounds = drawableRect.apply {
                left = centerX - drawableSize / 2
                top = centerY - drawableSize / 2
                right = centerX + drawableSize / 2
                bottom = centerY + drawableSize / 2
            }
            it.save()
            it.scale(1f + scale * progress,
                    1f + scale * progress,
                    drawable.bounds.centerX().toFloat(),
                    drawable.bounds.centerY().toFloat())
            it.rotate(-45f + 90f * progress,
                    drawable.bounds.centerX().toFloat(),
                    drawable.bounds.centerY().toFloat())
            drawable.draw(it)
            it.restore()
        }
    }

    private fun requestAnimate(show: Boolean) {
        if (show) {
            if (animator == null) {
                animator = ValueAnimator.ofFloat(0f, 1f).apply {
                    duration = 1000
                    repeatCount = INFINITE
                    repeatMode = REVERSE
                    addUpdateListener {
                        progress = it.animatedValue as Float
                        invalidate()
                    }
                    this.start()
                }
            } else {
                animator?.let {
                    if (!it.isRunning) {
                        it.start()
                    }
                }
            }
        } else {
            animator?.cancel()
        }
    }
}