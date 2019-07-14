package com.android.oleksandrpriadko.extension

import android.content.Context
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.LENGTH_SHORT
import android.widget.Toast.makeText
import androidx.annotation.StringRes

fun Context.showShortToast(message: String?) = makeText(this, message, LENGTH_SHORT).show()

fun Context.showShortToast(@StringRes message: Int) = makeText(this, message, LENGTH_SHORT).show()

fun Context.showLongToast(message: String?) = makeText(this, message, LENGTH_LONG).show()

fun Context.showLongToast(@StringRes message: Int) = makeText(this, message, LENGTH_LONG).show()