package com.android.oleksandrpriadko.extension

import android.content.Context
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.*

fun Context.animation(@AnimRes id: Int): Animation
        = AnimationUtils.loadAnimation(this, id)

fun Context.color(@ColorRes id: Int): Int = this.resources.getColor(id)

fun Context.quantityString(@PluralsRes id: Int, quantity: Int, formatArgs: Int = quantity): String
        = this.resources.getQuantityString(id, quantity, formatArgs)

fun Context.dimenPixelSize(@DimenRes id: Int): Int = this.resources.getDimensionPixelSize(id)

fun Context.stringArray(@ArrayRes id: Int): Array<String> = this.resources.getStringArray(id)
