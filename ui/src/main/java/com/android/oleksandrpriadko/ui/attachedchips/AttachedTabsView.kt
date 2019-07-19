package com.android.oleksandrpriadko.ui.attachedchips

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.Path.Direction
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import com.android.oleksandrpriadko.extension.*
import com.android.oleksandrpriadko.ui.R
import kotlin.math.roundToInt


class AttachedTabsView : ConstraintLayout {

    var indexOfSelectedItem = 0
        set(value) {
            field = value
            invalidate()
        }

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

    private var animationProgress = 0f
    private var direction = Direction.CW

    private var coordinatesStart = Coordinates.NOT_SET
    private var coordinatesDestination = Coordinates.NOT_SET

    private val path: Path = Path()
    private val paint: Paint = Paint()

    private var animator = ValueAnimator()

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
                indexOfSelectedItem = getInt(R.styleable.AttachedTabsView_selectedItem, indexOfSelectedItem)
                selectedItemBackground = getDrawable(R.styleable.AttachedTabsView_selectedItemBackground)
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
        for (child in children) {
            child.setOnTouchListener(onChildTouchListener)
        }

        if (coordinatesStart === Coordinates.NOT_SET) {
            coordinatesStart = getCoordinatesFromView(getChildAt(indexOfSelectedItem))
            coordinatesDestination = Coordinates.NOT_SET
        }

        if (areTabsOnTop) {
            initValuesForChipsAbove()
        } else {
            paveTheWay()
        }

        canvas?.drawPath(path, paint)

        canvas?.let {
            selectedItemBackground?.draw(it)
        }

        super.dispatchDraw(canvas)
    }

    private fun initValuesForChipsAbove() {}

    private fun paveTheWay() {
        if (coordinatesDestination === Coordinates.NOT_SET) {
            paveTheWayToStart()
            selectedItemBackground?.let {
                it.bounds = coordinatesStart.drawableBounds
            }
        } else {
            paveTheWayToDestination()
            selectedItemBackground?.let {
                it.bounds = moveBounds(animationProgress)
            }
        }
    }

    private fun paveTheWayToStart() {
        path.apply {
            reset()
            moveToPointF(coordinatesStart.start)
            cubicToPointF(
                    coordinatesStart.leftCurveStart,
                    coordinatesStart.leftCurveMiddle,
                    coordinatesStart.leftCurveEnd)
            lineToPointF(coordinatesStart.leftLine)

            addRectFPointsF(
                    coordinatesStart.rectLeftTop,
                    coordinatesStart.rectRightBottom)

            moveToPointF(coordinatesStart.rectRightBottom)
            cubicToPointF(
                    coordinatesStart.rightCurveStart,
                    coordinatesStart.rightCurveMiddle,
                    coordinatesStart.rightCurveEnd)
            lineToPointF(coordinatesStart.rightLine)
            close()
        }
    }

    private fun paveTheWayToDestination() {
        path.apply {
            reset()
            coordinatesDestination.apply {
                moveToPointF(movePoint(coordinatesStart.start, start, animationProgress, direction))
                cubicToPointF(
                        movePoint(coordinatesStart.leftCurveStart, leftCurveStart, animationProgress, direction),
                        movePoint(coordinatesStart.leftCurveMiddle, leftCurveMiddle, animationProgress, direction),
                        movePoint(coordinatesStart.leftCurveEnd, leftCurveEnd, animationProgress, direction))
                lineToPointF(movePoint(coordinatesStart.leftLine, leftLine, animationProgress, direction))

                addRectFPointsF(
                        movePoint(coordinatesStart.rectLeftTop, rectLeftTop, animationProgress, direction),
                        movePoint(coordinatesStart.rectRightBottom, rectRightBottom, animationProgress, direction))

                moveToPointF(movePoint(coordinatesStart.rectRightBottom, rectRightBottom, animationProgress, direction))
                cubicToPointF(
                        movePoint(coordinatesStart.rightCurveStart, rightCurveStart, animationProgress, direction),
                        movePoint(coordinatesStart.rightCurveMiddle, rightCurveMiddle, animationProgress, direction),
                        movePoint(coordinatesStart.rightCurveEnd, rightCurveEnd, animationProgress, direction))
                lineToPointF(movePoint(coordinatesStart.rightLine, rightLine, animationProgress, direction))
            }
            close()
        }
    }

    private val onChildTouchListener = OnTouchListener { touchedView, motioEvent ->
        if (motioEvent.action == MotionEvent.ACTION_UP) {
            requestEndAnimator()

            coordinatesDestination = getCoordinatesFromView(touchedView)

            var indexOfNewSelectedItem = 0
            children.forEachIndexed { index, view ->
                if (view === touchedView) {
                    indexOfNewSelectedItem = index
                }
            }
            direction = if (indexOfNewSelectedItem >= indexOfSelectedItem) Direction.CW else Direction.CCW

            indexOfSelectedItem = indexOfNewSelectedItem
            animator = createAnimator()
            animator.start()
        }
        false
    }

    private fun getCoordinatesFromView(v: View): Coordinates {
        if (areTabsOnTop) {
            return Coordinates.NOT_SET
        } else {
            val viewCenterY: Float = v.y + v.measuredHeight / 2
            val viewBounds = Rect(v.left, v.top, v.right, v.bottom)
            return Coordinates(
                    start = PointF(v.x - viewCenterY, 0f),
                    leftCurveStart = PointF(v.x - viewCenterY / 2, 0f),
                    leftCurveMiddle = PointF(v.x, viewCenterY / 2),
                    leftCurveEnd = PointF(v.x, viewCenterY),
                    leftLine = PointF(v.x, 0f),
                    rectLeftTop = PointF(v.x, 0f),
                    rectRightBottom = PointF(v.right.toFloat(), viewCenterY),
                    rightCurveStart = PointF(v.right.toFloat(), viewCenterY / 2),
                    rightCurveMiddle = PointF(v.right.toFloat() + viewCenterY / 2, 0f),
                    rightCurveEnd = PointF(v.right.toFloat() + viewCenterY, 0f),
                    rightLine = PointF(v.right.toFloat(), 0f),
                    drawableBounds = viewBounds)
        }
    }

    private fun createAnimator(): ValueAnimator {
        return ValueAnimator.ofFloat(0f, 1f)
                .setDuration(500)
                .apply {
                    addUpdateListener {
                        animationProgress = it.animatedValue as Float
                        if (animationProgress == 0f) {
                            applyDrawableState(stateDrawableAnimating)
                        }
                        invalidate()
                    }
                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                            coordinatesStart = coordinatesDestination.copy()
                            coordinatesDestination = Coordinates.NOT_SET
                            applyDrawableState(null)
                        }
                    })
                }
    }

    private fun movePoint(pointS: PointF, pointE: PointF, progress: Float, direction: Direction): PointF {
        return when (direction) {
            Direction.CW -> {
                PointF(pointS.x + (pointE.x - pointS.x) * progress, pointS.y)
            }
            Direction.CCW -> {
                PointF(pointS.x - (pointS.x - pointE.x) * progress, pointS.y)
            }
        }
    }

    private fun moveBounds(progress: Float): Rect {
        val bounds: Rect
        return when (direction) {
            Direction.CW -> {
                val shiftXLeft = ((coordinatesDestination.drawableBounds.left - coordinatesStart.drawableBounds.left) * progress).roundToInt()
                val shiftXRight = ((coordinatesDestination.drawableBounds.right - coordinatesStart.drawableBounds.right) * progress).roundToInt()
                bounds = Rect(coordinatesStart.drawableBounds)
                bounds.offsetX(shiftXLeft, shiftXRight)
                bounds
            }
            Direction.CCW -> {
                val shiftXLeft = ((coordinatesStart.drawableBounds.left - coordinatesDestination.drawableBounds.left) * progress).roundToInt()
                val shiftXRight = ((coordinatesStart.drawableBounds.right - coordinatesDestination.drawableBounds.right) * progress).roundToInt()
                bounds = Rect(coordinatesStart.drawableBounds)
                bounds.offsetX(-shiftXLeft, -shiftXRight)
                bounds
            }
        }
    }

    private fun requestEndAnimator() {
        if (animator.isRunning) {
            animator.end()
            animator.removeAllUpdateListeners()
        }
    }

    private fun applyDrawableState(requiredState: IntArray?) {
        if (requiredState != null) {
            val requiredStates = super.onCreateDrawableState(requiredState.size)
            mergeDrawableStates(requiredStates, requiredState)
            selectedItemBackground?.state = requiredStates
        } else {
            selectedItemBackground?.state = IntArray(0)
        }
    }

    companion object {
        val stateDrawableAnimating = IntArray(1) { R.attr.state_animating }
    }
}