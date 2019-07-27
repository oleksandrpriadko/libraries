package com.android.oleksandrpriadko.demo.cocktails

import android.animation.ValueAnimator
import android.animation.ValueAnimator.INFINITE
import android.animation.ValueAnimator.REVERSE
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.VectorDrawable
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import com.android.oleksandrpriadko.demo.R
import com.android.oleksandrpriadko.extension.windowService

class DrinksLoadingView : FrameLayout, PresenterView {

    private val presenter: DrinkLoadingPresenter

    private var animator: ValueAnimator? = null

    private val listOfDrawablesIds: List<Int> = listOf(
            R.drawable.ic_loading_drink_1,
            R.drawable.ic_loading_drink_2,
            R.drawable.ic_loading_drink_3,
            R.drawable.ic_loading_drink_8,
            R.drawable.ic_loading_drink_9,
            R.drawable.ic_loading_drink_10,
            R.drawable.ic_loading_drink_11,
            R.drawable.ic_loading_drink_12,
            R.drawable.ic_loading_drink_13,
            R.drawable.ic_loading_drink_14,
            R.drawable.ic_loading_drink_15,
            R.drawable.ic_loading_drink_16,
            R.drawable.ic_loading_drink_17,
            R.drawable.ic_loading_drink_18,
            R.drawable.ic_loading_drink_19,
            R.drawable.ic_loading_drink_20,
            R.drawable.ic_loading_drink_21,
            R.drawable.ic_loading_drink_22,
            R.drawable.ic_loading_drink_23,
            R.drawable.ic_loading_drink_24,
            R.drawable.ic_loading_drink_26,
            R.drawable.ic_loading_drink_27,
            R.drawable.ic_loading_drink_28,
            R.drawable.ic_loading_drink_29,
            R.drawable.ic_loading_drink_30,
            R.drawable.ic_loading_drink_32,
            R.drawable.ic_loading_drink_1,
            R.drawable.ic_loading_drink_2,
            R.drawable.ic_loading_drink_3,
            R.drawable.ic_loading_drink_8,
            R.drawable.ic_loading_drink_9,
            R.drawable.ic_loading_drink_10,
            R.drawable.ic_loading_drink_11,
            R.drawable.ic_loading_drink_12,
            R.drawable.ic_loading_drink_13,
            R.drawable.ic_loading_drink_14,
            R.drawable.ic_loading_drink_15,
            R.drawable.ic_loading_drink_16,
            R.drawable.ic_loading_drink_17,
            R.drawable.ic_loading_drink_18,
            R.drawable.ic_loading_drink_19,
            R.drawable.ic_loading_drink_20,
            R.drawable.ic_loading_drink_21,
            R.drawable.ic_loading_drink_22,
            R.drawable.ic_loading_drink_23,
            R.drawable.ic_loading_drink_24,
            R.drawable.ic_loading_drink_26,
            R.drawable.ic_loading_drink_27,
            R.drawable.ic_loading_drink_28,
            R.drawable.ic_loading_drink_29,
            R.drawable.ic_loading_drink_30,
            R.drawable.ic_loading_drink_32)
            .shuffled()

    private val listOfDrawables: MutableList<DrinkLoadingDrawable> = mutableListOf()

    constructor(context: Context)
            : super(context)

    constructor(context: Context, attrs: AttributeSet?)
            : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr)

    init {
        listOfDrawables.clear()
        for (drawableId in listOfDrawablesIds) {
            val loadedDrawable = context.getDrawable(drawableId)
            if (loadedDrawable != null) {
                listOfDrawables.add(DrinkLoadingDrawable(loadedDrawable as VectorDrawable))
            }
        }
        val displayMetrics = DisplayMetrics()
        context.windowService().defaultDisplay.getMetrics(displayMetrics)

        presenter = DrinkLoadingPresenter(this, displayMetrics.widthPixels, displayMetrics.heightPixels)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.let {
            listOfDrawables.forEachIndexed { index, drawable ->
                drawable.vectorDrawable.bounds = presenter.getPosition(index)
                it.save()
                it.translate(drawable.translationX, drawable.translationY)
                it.rotate(drawable.calculateRotation(presenter.animationProgress),
                        drawable.centerXF(presenter.animationProgress),
                        drawable.centerYF(presenter.animationProgress))
                it.scale(drawable.calculateScaleCoefficient(presenter.animationProgress),
                        drawable.calculateScaleCoefficient(presenter.animationProgress),
                        drawable.centerXF(presenter.animationProgress),
                        drawable.centerYF(presenter.animationProgress))
                drawable.vectorDrawable.draw(it)
                it.restore()
                it.save()
            }
        }
        requestAnimate(true)
    }

    override fun invalidate(progress: Float) {
        invalidate()
    }

    override fun getAvailableDrawablesCount(): Int = listOfDrawablesIds.size

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        requestAnimate(true)

    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        requestAnimate(false)
    }

    private fun requestAnimate(animate: Boolean) {
        var localAnimator: ValueAnimator? = animator
        if (localAnimator != null) {
            if (!localAnimator.isRunning && animate) {
                localAnimator.start()
            }
        } else {
            localAnimator = ValueAnimator.ofFloat(0f, 1f).setDuration(400)
                    .apply {
                        duration = 2000
                        repeatCount = INFINITE
                        repeatMode = REVERSE
                        addUpdateListener {
                            presenter.animationProgress = it.animatedValue as Float
                        }
                    }
            animator = localAnimator
            localAnimator.start()
        }
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        when (visibility) {
            View.VISIBLE -> requestAnimate(true)
            else -> requestAnimate(false)
        }
    }

    override fun getLifecycle(): Lifecycle {
        return (context as? FragmentActivity)?.lifecycle ?: object : Lifecycle() {
            override fun addObserver(observer: LifecycleObserver) {

            }

            override fun removeObserver(observer: LifecycleObserver) {

            }

            override fun getCurrentState(): State {
                return State.DESTROYED
            }
        }
    }
}
