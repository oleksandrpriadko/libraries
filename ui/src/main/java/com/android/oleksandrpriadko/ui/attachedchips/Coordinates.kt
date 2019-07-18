package com.android.oleksandrpriadko.ui.attachedchips

import android.graphics.PointF

internal data class Coordinates(val start: PointF = PointF(),

                       val leftCurveStart: PointF = PointF(),
                       val leftCurveMiddle: PointF = PointF(),
                       val leftCurveEnd: PointF = PointF(),

                       val leftLine: PointF = PointF(),

                       val rectLeftTop: PointF = PointF(),
                       val rectRightBottom: PointF = PointF(),

                       val rightCurveStart: PointF = PointF(),
                       val rightCurveMiddle: PointF = PointF(),
                       val rightCurveEnd: PointF = PointF(),

                       val rightLine: PointF = PointF()) {

    companion object {
        val NOT_SET = Coordinates()
    }
}