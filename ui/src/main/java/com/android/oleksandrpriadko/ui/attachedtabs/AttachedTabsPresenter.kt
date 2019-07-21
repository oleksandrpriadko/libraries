package com.android.oleksandrpriadko.ui.attachedtabs

import android.graphics.Canvas
import android.graphics.Path
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.RestrictTo
import androidx.lifecycle.LifecycleOwner
import com.android.oleksandrpriadko.extension.*
import com.android.oleksandrpriadko.mvp.presenter.BasePresenter
import kotlin.math.roundToInt

class AttachedTabsPresenter(presenterView: PresenterView)
    : BasePresenter<PresenterView>(presenterView) {

    private var indexOfSelectedItem = 0

    private var transitionProgress = 0f
    private var direction = Path.Direction.CW

    private var coordinatesStart = Coordinates.NOT_SET
    private var coordinatesDestination = Coordinates.NOT_SET

    fun selectItem(index: Int, areTabsOnTop: Boolean) {
        if (indexOfSelectedItem == index) {
            return
        }
        val toBeSelectedView: View? = view?.getChildAt(index)
        if (toBeSelectedView != null) {
            selectItem(toBeSelectedView, areTabsOnTop)
        }
    }

    fun selectItem(toBeSelectedView: View, areTabsOnTop: Boolean) {
        view?.endAnimator()

        coordinatesDestination = getCoordinatesFromView(toBeSelectedView, areTabsOnTop)

        var indexOfToBeSelectedView = 0

        view?.let {
            indexOfToBeSelectedView = it.indexOfChild(toBeSelectedView)
        }

        direction = if (indexOfToBeSelectedView >= indexOfSelectedItem) Path.Direction.CW else Path.Direction.CCW
        view?.changeChildSelectState(indexOfSelectedItem, false)
        indexOfSelectedItem = indexOfToBeSelectedView

        view?.createAnimator()
        view?.startAnimator()
    }

    fun onDrawUnderneathChildren(areTabsOnTop: Boolean,
                                 drawable: Drawable?,
                                 path: Path,
                                 canvas: Canvas?) {
        view?.setTouchListenersToChildren()

        if (coordinatesStart === Coordinates.NOT_SET) {
            coordinatesStart = getCoordinatesFromView(view?.getChildAt(indexOfSelectedItem), areTabsOnTop)
            coordinatesDestination = Coordinates.NOT_SET
        }

        paveTheWay(drawable, path)

        view?.drawPathOnCanvas(canvas, path)

        view?.drawDrawableOnCanvas(canvas, drawable)
    }

    fun onDrawAboveChildren() {}

    fun onTransitionProgressChanged(progress: Float) {
        transitionProgress = progress
        if (transitionProgress == 0f) {
            view?.changeFloatingDrawableState(AttachedTabsView.STATE_DRAWABLE_ANIMATING)
        }
        view?.invalidate()
    }

    fun onTransitionEnded() {
        coordinatesStart = coordinatesDestination.copy()
        coordinatesDestination = Coordinates.NOT_SET
        view?.changeFloatingDrawableState(AttachedTabsView.STATE_DRAWABLE_SELECTED)
        view?.notifyOnItemSelected(indexOfSelectedItem)
        view?.changeChildSelectState(indexOfSelectedItem, true)
    }

    @RestrictTo(RestrictTo.Scope.SUBCLASSES)
    internal fun paveTheWay(selectedItemBackground: Drawable?,
                            path: Path) {
        if (coordinatesDestination === Coordinates.NOT_SET) {
            paveTheWayToStart(path)
            selectedItemBackground?.let {
                it.bounds = coordinatesStart.drawableBounds
            }
        } else {
            paveTheWayToDestination(path)
            selectedItemBackground?.let {
                it.bounds = moveBounds(transitionProgress)
            }
        }
    }

    @RestrictTo(RestrictTo.Scope.SUBCLASSES)
    internal fun paveTheWayToStart(path: Path) {
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

    @RestrictTo(RestrictTo.Scope.SUBCLASSES)
    internal fun paveTheWayToDestination(path: Path) {
        path.apply {
            reset()
            coordinatesDestination.apply {
                moveToPointF(movePoint(coordinatesStart.start, start, transitionProgress, direction))
                cubicToPointF(
                        movePoint(coordinatesStart.leftCurveStart, leftCurveStart, transitionProgress, direction),
                        movePoint(coordinatesStart.leftCurveMiddle, leftCurveMiddle, transitionProgress, direction),
                        movePoint(coordinatesStart.leftCurveEnd, leftCurveEnd, transitionProgress, direction))
                lineToPointF(movePoint(coordinatesStart.leftLine, leftLine, transitionProgress, direction))

                addRectFPointsF(
                        movePoint(coordinatesStart.rectLeftTop, rectLeftTop, transitionProgress, direction),
                        movePoint(coordinatesStart.rectRightBottom, rectRightBottom, transitionProgress, direction))

                moveToPointF(movePoint(coordinatesStart.rectRightBottom, rectRightBottom, transitionProgress, direction))
                cubicToPointF(
                        movePoint(coordinatesStart.rightCurveStart, rightCurveStart, transitionProgress, direction),
                        movePoint(coordinatesStart.rightCurveMiddle, rightCurveMiddle, transitionProgress, direction),
                        movePoint(coordinatesStart.rightCurveEnd, rightCurveEnd, transitionProgress, direction))
                lineToPointF(movePoint(coordinatesStart.rightLine, rightLine, transitionProgress, direction))
            }
            close()
        }
    }

    @RestrictTo(RestrictTo.Scope.SUBCLASSES)
    internal fun movePoint(pointS: PointF, pointE: PointF, progress: Float, direction: Path.Direction): PointF {
        return when (direction) {
            Path.Direction.CW -> {
                PointF(pointS.x + (pointE.x - pointS.x) * progress, pointS.y)
            }
            Path.Direction.CCW -> {
                PointF(pointS.x - (pointS.x - pointE.x) * progress, pointS.y)
            }
        }
    }

    @RestrictTo(RestrictTo.Scope.SUBCLASSES)
    internal fun moveBounds(progress: Float): Rect {
        val bounds: Rect
        return when (direction) {
            Path.Direction.CW -> {
                val shiftXLeft = ((coordinatesDestination.drawableBounds.left - coordinatesStart.drawableBounds.left) * progress).roundToInt()
                val shiftXRight = ((coordinatesDestination.drawableBounds.right - coordinatesStart.drawableBounds.right) * progress).roundToInt()
                bounds = Rect(coordinatesStart.drawableBounds)
                bounds.offsetX(shiftXLeft, shiftXRight)
                bounds
            }
            Path.Direction.CCW -> {
                val shiftXLeft = ((coordinatesStart.drawableBounds.left - coordinatesDestination.drawableBounds.left) * progress).roundToInt()
                val shiftXRight = ((coordinatesStart.drawableBounds.right - coordinatesDestination.drawableBounds.right) * progress).roundToInt()
                bounds = Rect(coordinatesStart.drawableBounds)
                bounds.offsetX(-shiftXLeft, -shiftXRight)
                bounds
            }
        }
    }

    private fun getCoordinatesFromView(v: View?, areTabsOnTop: Boolean, rect: Rect = Rect()): Coordinates {
        if (v == null) {
            return Coordinates.NOT_SET
        } else {
            if (areTabsOnTop) {
                rect.set(v.left, v.top, v.right, v.bottom)
                val viewCenterY: Float = rect.centerY().toFloat()
                var parentHeight = 0f
                view?.let {
                    parentHeight = it.getMeasuredHeight().toFloat()
                }
                return Coordinates(
                        start = PointF(v.x - viewCenterY, parentHeight),
                        leftCurveStart = PointF(v.x - viewCenterY / 2, parentHeight),
                        leftCurveMiddle = PointF(v.x, viewCenterY / 2),
                        leftCurveEnd = PointF(v.x, viewCenterY),
                        leftLine = PointF(v.x, parentHeight),
                        rectLeftTop = PointF(v.x, parentHeight),
                        rectRightBottom = PointF(v.right.toFloat(), viewCenterY),
                        rightCurveStart = PointF(v.right.toFloat(), viewCenterY / 2),
                        rightCurveMiddle = PointF(v.right.toFloat() + viewCenterY / 2, parentHeight),
                        rightCurveEnd = PointF(v.right.toFloat() + viewCenterY, parentHeight),
                        rightLine = PointF(v.right.toFloat(), parentHeight),
                        drawableBounds = rect)
            } else {
                val viewBounds = Rect(v.left, v.top, v.right, v.bottom)
                val viewCenterY: Float = viewBounds.centerY().toFloat()
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
    }
}

interface PresenterView : LifecycleOwner {

    fun setTouchListenersToChildren()

    fun getMeasuredHeight(): Int

    fun getChildAt(index: Int): View?

    fun indexOfChild(view: View): Int

    fun drawPathOnCanvas(canvas: Canvas?, path: Path)

    fun drawDrawableOnCanvas(canvas: Canvas?, drawable: Drawable?)

    fun createAnimator()

    fun startAnimator()

    fun endAnimator()

    fun changeFloatingDrawableState(state: IntArray?)

    fun changeChildSelectState(index: Int, isSelected: Boolean)

    fun invalidate()

    fun notifyOnItemSelected(indexOfSelected: Int)
}