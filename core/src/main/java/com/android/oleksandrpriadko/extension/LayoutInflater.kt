package com.android.oleksandrpriadko.extension

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

fun Context.inflateOnParent(@LayoutRes layoutId: Int,
                            container: ViewGroup? = null,
                            addToParent: Boolean = false): View
        = LayoutInflater.from(this).inflate(layoutId, container, addToParent)