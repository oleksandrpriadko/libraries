package com.android.oleksandrpriadko.extension

import android.graphics.Rect

fun Rect.offsetX(dXLeft: Int, dXRight: Int) {
    this.left += dXLeft
    this.right += dXRight
}