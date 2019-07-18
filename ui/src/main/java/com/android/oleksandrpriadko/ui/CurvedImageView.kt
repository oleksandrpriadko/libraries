package com.android.oleksandrpriadko.ui

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.widget.ImageView
import com.android.oleksandrpriadko.extension.cubicToPointF
import com.android.oleksandrpriadko.extension.dimenPixelSize
import com.android.oleksandrpriadko.extension.moveToPointF

/**
 * ImageView which draws Bezier curve on the bottom of canvas.
 *
 * Minimum height is
 */
class CurvedImageView : ImageView {

    constructor(context: Context?) : super(context) { init(null) }

    constructor(context: Context?, attrs: AttributeSet?)
            : super(context, attrs) { init(attrs) }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) { init(attrs) }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int)
            : super(context, attrs, defStyleAttr, defStyleRes) { init(attrs) }


    private val path: Path = Path()
    private val paint: Paint = Paint()
    private var widthF: Float = 0f
    private var heightF: Float = 0f
    private var pointStartF = PointF()
    private var pointCurveStartF = PointF()
    private var pointCurveEndF = PointF()
    private var pointEndF = PointF()
    private var curveColor = Color.WHITE

    private fun init(attrs: AttributeSet?) {
        attrs?.let {
            context.theme.obtainStyledAttributes(attrs,
                    R.styleable.CurvedImageView,
                    0,
                    0).apply {
                curveColor = getColor(R.styleable.CurvedImageView_curveColor, curveColor)
                recycle()
            }
        }

        with(paint) {
            style = Paint.Style.FILL_AND_STROKE
            color = curveColor
            isAntiAlias = true
        }

        minimumHeight = context.dimenPixelSize(R.dimen.min_height_curved_image_view)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        resetValues()
    }

    private fun resetValues() {
        widthF = width.toFloat()
        heightF = height.toFloat()

        pointStartF.x = 0f
        pointStartF.y = heightF
        pointCurveStartF.x = SHIFT_CURVE_X
        pointCurveStartF.y = heightF - SHIFT_CURVE_Y
        pointCurveEndF.x = widthF - SHIFT_CURVE_X
        pointCurveEndF.y = heightF - SHIFT_CURVE_Y * 0.4f
        pointEndF.x = widthF
        pointEndF.y = heightF - SHIFT_CURVE_Y

        path.apply {
            reset()
            moveToPointF(pointStartF)
            cubicToPointF(pointCurveStartF, pointCurveEndF, pointEndF)
            lineTo(widthF, heightF)
            close()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawPath(path, paint)
    }

    companion object {
        const val SHIFT_CURVE_X = 22f
        const val SHIFT_CURVE_Y = 200f
    }
}