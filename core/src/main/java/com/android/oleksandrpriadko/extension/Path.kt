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

fun Path.lineToPointF(pointF: PointF) {
    lineTo(pointF.x, pointF.y)
}

fun Path.addRectFPointsF(leftTop: PointF, rightBottom: PointF) {
    addRect(leftTop.x, leftTop.y, rightBottom.x, rightBottom.y, Path.Direction.CW)
}