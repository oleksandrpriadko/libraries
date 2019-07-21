package com.android.oleksandrpriadko.extension

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.LayoutRes

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun View.show(show: Boolean) {
    if (show) show() else hide()

}

fun View.layoutParams(height: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
                      width: Int = ViewGroup.LayoutParams.MATCH_PARENT) {
    val newLayoutParams = layoutParams ?: ViewGroup.LayoutParams(width, height)
    newLayoutParams.height = height
    newLayoutParams.width = width
    layoutParams = newLayoutParams
}

fun View.frameLayoutParams(height: Int = FrameLayout.LayoutParams.WRAP_CONTENT,
                           width: Int = FrameLayout.LayoutParams.MATCH_PARENT) {
    val newLayoutParams = layoutParams ?: FrameLayout.LayoutParams(width, height)
    newLayoutParams.height = height
    newLayoutParams.width = width
    layoutParams = newLayoutParams
}

fun View.hasParent(): Boolean = parent != null

fun <T>ViewGroup.inflateOn(@LayoutRes layoutId: Int,
                        addToParent: Boolean = false): T {

    return LayoutInflater.from(context).inflate(layoutId, this, addToParent) as T

}
