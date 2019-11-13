package com.android.oleksandrpriadko.ui.attachedtabs

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import com.android.oleksandrpriadko.ui.R

class AttachedTabsView : ConstraintLayout, PresenterView {

    var selectedItemBackground: Drawable? = null
        set(value) {
            field = value
            invalidate()
        }

    var areTabsOnTop = false
        set(value) {
            field = value
            invalidate()
        }
    var curveColor = Color.BLACK
        set(value) {
            field = value
            invalidate()
        }

    var onItemSelectedListener: OnItemSelectedListener? = null

    var animationDuration: Long = 500

    private val path: Path = Path()
    private val paint: Paint = Paint()

    private var animator = ValueAnimator()

    private val presenter = AttachedTabsPresenter(this, isInEditMode)

    constructor(context: Context?) : super(context) {
        init(null)
    }

    constructor(context: Context?, attrs: AttributeSet?)
            : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int)
            : super(context, attrs, defStyleAttr, defStyleRes) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        attrs?.let {
            context.theme.obtainStyledAttributes(attrs,
                    R.styleable.AttachedTabsView,
                    0,
                    0).apply {
                curveColor = getColor(R.styleable.AttachedTabsView_curveColorTabs, curveColor)
                areTabsOnTop = getBoolean(R.styleable.AttachedTabsView_areTabsOnTop, areTabsOnTop)
                presenter.selectItem(
                        getInt(R.styleable.AttachedTabsView_selectedItem, 0),
                        areTabsOnTop)
                selectedItemBackground = getDrawable(R.styleable.AttachedTabsView_floatingDrawable)
                animationDuration = getInt(R.styleable.AttachedTabsView_animationDuration, animationDuration.toInt()).toLong()
                recycle()
            }
        }

        paint.apply {
            style = Paint.Style.FILL
            color = curveColor
            isAntiAlias = true
            isDither = true
        }
    }

    override fun dispatchDraw(canvas: Canvas?) {
        presenter.onDrawUnderneathChildren(areTabsOnTop, selectedItemBackground, path, canvas)
        super.dispatchDraw(canvas)
        presenter.onDrawAboveChildren()
    }

    override fun setTouchListenersToChildren() {
        for (child in children) {
            child.setOnTouchListener(onChildTouchListener)
        }
    }

    override fun drawPathOnCanvas(canvas: Canvas?, path: Path) {
        canvas?.drawPath(path, paint)
    }

    override fun drawDrawableOnCanvas(canvas: Canvas?, drawable: Drawable?) {
        canvas?.let {
            selectedItemBackground?.draw(it)
        }
    }

    private val onChildTouchListener = OnTouchListener { touchedView, motioEvent ->
        if (motioEvent.action == MotionEvent.ACTION_UP) {
            presenter.selectItem(touchedView, areTabsOnTop)
        }
        false
    }

    fun selectItem(index: Int) {
        presenter.selectItem(index, areTabsOnTop)
    }

    fun requestSelectNextToRight() {
        presenter.requestSelectNextToRight(childCount, areTabsOnTop)
    }

    fun requestSelectNextToLeft() {
        presenter.requestSelectNextToLeft(areTabsOnTop)
    }

    override fun createAnimator() {
        animator = ValueAnimator.ofFloat(0f, 1f)
                .setDuration(animationDuration)
                .apply {
                    interpolator = AccelerateDecelerateInterpolator()
                    addUpdateListener {
                        presenter.onTransitionProgressChanged(it.animatedValue as Float)
                    }
                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            presenter.onTransitionEnded()
                        }
                    })
                }
    }

    override fun changeFloatingDrawableState(state: IntArray?) {
        if (state != null) {
            selectedItemBackground?.state = state
        } else {
            selectedItemBackground?.state = IntArray(0)
        }
    }

    override fun changeChildSelectState(index: Int, isSelected: Boolean) {
        val child = getChildAt(index)
        child?.isSelected = isSelected
    }

    override fun notifyOnItemSelected(indexOfSelected: Int) {
        val viewUnderIndex = getChildAt(indexOfSelected)
        if (viewUnderIndex != null) {
            onItemSelectedListener?.onItemSelected(viewUnderIndex, indexOfSelected)
        }
    }

    override fun startAnimator() {
        animator.start()
    }

    override fun endAnimator() {
        requestEndAnimator()
    }


    private fun requestEndAnimator() {
        if (animator.isRunning) {
            animator.end()
            animator.removeAllUpdateListeners()
        }
    }

    fun getIndexOfSelectedItem(): Int = presenter.getIngexOfSelectedItem()

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

    companion object {
        val STATE_DRAWABLE_ANIMATING = IntArray(1) { R.attr.state_animating }
        val STATE_DRAWABLE_SELECTED = IntArray(1) { android.R.attr.state_selected }
    }
}

interface OnItemSelectedListener {

    fun onItemSelected(selectedView: View, indexOfSelected: Int)

}