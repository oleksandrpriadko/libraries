package com.android.oleksandrpriadko.demo.cocktails

import android.graphics.Point
import android.graphics.Rect
import android.graphics.drawable.VectorDrawable
import androidx.annotation.FloatRange
import androidx.lifecycle.LifecycleOwner
import com.android.oleksandrpriadko.mvp.presenter.BasePresenter
import kotlin.math.sqrt
import kotlin.random.Random

class DrinkLoadingPresenter(view: PresenterView,
                            private val screenWidth: Int,
                            screenHeight: Int,
                            @FloatRange(from = 0.0, to = 1.0)
                            private val itemDiameterRatio: Float = 0.7f) : BasePresenter<PresenterView>(view) {

    var animationProgress = 0f
        set(value) {
            if (field != value) {
                field = value
                view?.invalidate(field)
            }
        }

    private var itemSquareSideSize = calculateItemRectSideSize(screenWidth, screenHeight)
    private var itemDiameter = calculateItemSideSize(itemSquareSideSize)

    private var positions = populatePositions()

    private fun calculateItemSideSize(itemRectSideSize: Int): Int {
        return (itemRectSideSize * itemDiameterRatio).toInt()
    }

    private fun calculateItemRectSideSize(screenWidth: Int, screenHeight: Int): Int {
        view?.let {
            val itemSquare: Int = (screenWidth * screenHeight) / it.getAvailableDrawablesCount()
            return sqrt(itemSquare.toDouble()).toInt()
        }
        return sqrt((screenWidth * screenHeight).toDouble()).toInt()
    }

    private fun populatePositions(): MutableList<Point> {
        val localPositions = mutableListOf<Point>()

        view?.let {
            val itemsInWidth = screenWidth / itemSquareSideSize
            val shiftX = /*(screenWidth - (itemSquareSideSize * itemsInWidth)) * 0.5*/ 0

            for (i in 0 until it.getAvailableDrawablesCount()) {
                val startX: Int = (shiftX + (itemSquareSideSize * (i % itemsInWidth)))
                val startY: Int = (itemSquareSideSize * (i / itemsInWidth))
                val positionOfItemInItemSquare = defineItemPosition(itemSquareSideSize, itemDiameter)
                val positionOfItemOnScreen = Point(startX + positionOfItemInItemSquare.x, startY + positionOfItemInItemSquare.y)
                localPositions.add(positionOfItemOnScreen)
            }
        }
        return localPositions
    }

    private fun defineItemPosition(itemSquareSideSize: Int, itemDiameter: Int): Point {
        val maxRange = itemSquareSideSize - itemDiameter
        if (maxRange == 0) {
            return Point(0, 0)
        }
        return Point(Random.nextInt(maxRange), Random.nextInt(maxRange))
    }

    fun getPosition(index: Int): Rect {
        return when {
            positions.isNotEmpty() -> Rect(
                    positions[index].x,
                    positions[index].y,
                    positions[index].x + itemDiameter,
                    positions[index].y + itemDiameter)
            else -> Rect()
        }
    }
}

interface PresenterView : LifecycleOwner {

    fun getAvailableDrawablesCount(): Int

    fun invalidate(progress: Float)

}

class DrinkLoadingDrawable(val vectorDrawable: VectorDrawable) {

    private val rotationDegree: Float = Random.nextInt(-270, 270).toFloat()
    private val scaleCoefficientDiff: Float = Random.nextDouble(0.5).toFloat()
    val translationX: Float = Random.nextDouble(-100.0, 100.0).toFloat()
    val translationY: Float = Random.nextDouble(-100.0, 100.0).toFloat()

    fun calculateRotation(progress: Float): Float = rotationDegree + rotationDegree * progress
    fun calculateScaleCoefficient(progress: Float): Float = 1 + scaleCoefficientDiff * progress

    fun centerXF(progress: Float): Float = vectorDrawable.bounds.centerX().toFloat() + (translationX / 2) * progress
    fun centerYF(progress: Float): Float = vectorDrawable.bounds.centerY().toFloat() + (translationY / 2) * progress

}