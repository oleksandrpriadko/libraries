package com.android.oleksandrpriadko.extension

import android.graphics.Path
import android.graphics.PointF

fun Path.moveToPointF(pointF: PointF) {
    moveTo(pointF.x, pointF.y)
}

fun Path.cubicToPointF(pointCurveStartF: PointF,
                       pointCurveEndF: PointF,
                       pointEndF: PointF) {
    cubicTo(pointCurveStartF.x, pointCurveStartF.y,
            pointCurveEndF.x, pointCurveEndF.y,
            pointEndF.x, pointEndF.y)
}