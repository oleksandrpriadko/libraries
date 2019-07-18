package com.android.oleksandrpriadko.ui.attachedchips

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.Path.Direction
import android.util.AttributeSet
import android.view.View
import android.view.View.OnTouchListener
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import com.android.oleksandrpriadko.extension.addRectFPointsF
import com.android.oleksandrpriadko.extension.cubicToPointF
import com.android.oleksandrpriadko.extension.lineToPointF
import com.android.oleksandrpriadko.extension.moveToPointF
import com.android.oleksandrpriadko.ui.R


class AttachedTabsView : ConstraintLayout {

    private var indexOfSelectedItem = 0

    var areChipsOnTop = false
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
    var direction = Direction.CW

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
                areChipsOnTop = getBoolean(R.styleable.AttachedTabsView_areChipsOnTop, areChipsOnTop)
                indexOfSelectedItem = getInt(R.styleable.AttachedTabsView_selectedItem, indexOfSelectedItem)
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

        if (areChipsOnTop) {
            initValuesForChipsAbove()
        } else {
            paveTheWay(animationProgress)
        }

        canvas?.drawPath(path, paint)
        super.dispatchDraw(canvas)
    }

    private fun initValuesForChipsAbove() {
//        val index = Random.nextInt(3)
//        widthF = width.toFloat()
//        heightF = height.toFloat()
//
//        val selectedChip = chipList[index]
//
//        val chipCenterY = (selectedChip.top + selectedChip.measuredHeight * 0.5).toFloat()
//        val distanceChipCenterYToBottom = heightF - chipCenterY
//
//        rectF = RectF(selectedChip.left.toFloat(),
//                chipCenterY,
//                selectedChip.right.toFloat(),
//                heightF)
//
//        startPointF = PointF(rectF.left - 130, heightF)
//        endPointF = PointF(rectF.right + 130, heightF)
//
//        val distanceStartToRect = rectF.left
//        curve2PointF = PointF(rectF.left, rectF.top)
//        curve0PointF = PointF(
//                (distanceStartToRect * RATIO_CURVE_POINT_0_xUNDER).toFloat(),
//                chipCenterY + (distanceChipCenterYToBottom * RATIO_CURVE_POINT_0_Y_UNDER).toFloat())
//        curve1PointF = PointF(
//                (distanceStartToRect * RATIO_CURVE_POINT_1_xUNDER).toFloat(),
//                chipCenterY + (distanceChipCenterYToBottom * RATIO_CURVE_POINT_1_Y_UNDER).toFloat())
//
//        val distanceRectToEnd = widthF - rectF.right
//        curve3PointF = PointF(
//                rectF.right + (distanceRectToEnd * RATIO_CURVE_POINT_3_xUNDER).toFloat(),
//                chipCenterY + (distanceChipCenterYToBottom * RATIO_CURVE_POINT_3_Y_UNDER).toFloat())
//        curve4PointF = PointF(
//                rectF.right + (distanceRectToEnd * RATIO_CURVE_POINT_4_xUNDER).toFloat(),
//                chipCenterY + (distanceChipCenterYToBottom * RATIO_CURVE_POINT_4_Y_UNDER).toFloat())
//
//        path.apply {
//            reset()
//
//            // curve left
//            moveToPointF(startPointF)
//            cubicToPointF(curve0PointF, curve1PointF, curve2PointF)
//            lineTo(curve2PointF.x, heightF)
//
//            // rectangle below selected item
//            addRect(rectF, Path.Direction.CW)
//
//            // curve right
//            moveTo(rectF.right, chipCenterY)
//            cubicToPointF(curve3PointF, curve4PointF, endPointF)
//            lineTo(rectF.right, heightF)
//            close()
//        }
    }

    private fun paveTheWay(progress: Float) {
        if (coordinatesDestination === Coordinates.NOT_SET) {
            paveTheWayToStart()
        } else {
            paveTheWayToDestination(progress)
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

    private fun paveTheWayToDestination(progress: Float) {
        path.apply {
            reset()
            coordinatesDestination.apply {
                moveToPointF(movePoint(coordinatesStart.start, start, progress, direction))
                cubicToPointF(
                        movePoint(coordinatesStart.leftCurveStart, leftCurveStart, progress, direction),
                        movePoint(coordinatesStart.leftCurveMiddle, leftCurveMiddle, progress, direction),
                        movePoint(coordinatesStart.leftCurveEnd, leftCurveEnd, progress, direction))
                lineToPointF(movePoint(coordinatesStart.leftLine, leftLine, progress, direction))

                addRectFPointsF(
                        movePoint(coordinatesStart.rectLeftTop, rectLeftTop, progress, direction),
                        movePoint(coordinatesStart.rectRightBottom, rectRightBottom, progress, direction))

                moveToPointF(movePoint(coordinatesStart.rectRightBottom, rectRightBottom, progress, direction))
                cubicToPointF(
                        movePoint(coordinatesStart.rightCurveStart, rightCurveStart, progress, direction),
                        movePoint(coordinatesStart.rightCurveMiddle, rightCurveMiddle, progress, direction),
                        movePoint(coordinatesStart.rightCurveEnd, rightCurveEnd, progress, direction))
                lineToPointF(movePoint(coordinatesStart.rightLine, rightLine, progress, direction))
            }
            close()
        }
    }

    private val onChildTouchListener = OnTouchListener { touchedView, _ ->
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
        false
    }

    private fun getCoordinatesFromView(v: View): Coordinates {
        if (areChipsOnTop) {
            return Coordinates.NOT_SET
        } else {
            val viewCenterY: Float = v.y + v.measuredHeight / 2
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
                    rightLine = PointF(v.right.toFloat(), 0f))
        }
    }

    private fun createAnimator(): ValueAnimator {
        return ValueAnimator.ofFloat(0f, 1f)
                .setDuration(500)
                .apply {
                    addUpdateListener {
                        animationProgress = it.animatedValue as Float
                        invalidate()
                        requestLayout()
                    }
                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                            coordinatesStart = coordinatesDestination.copy()
                            coordinatesDestination = Coordinates.NOT_SET
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

    private fun requestEndAnimator() {
        if (animator.isRunning) {
            animator.end()
            animator.removeAllUpdateListeners()
        }
    }
}