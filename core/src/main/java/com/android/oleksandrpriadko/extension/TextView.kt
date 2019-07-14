package com.android.oleksandrpriadko.extension

import android.os.Build
import android.text.Html
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.annotation.StringRes

fun TextView.text(): String {
    if (this.text == null) {
        return ""
    } else {
        return this.text.toString()
    }
}

fun TextView.toInt(): Int = Integer.parseInt(text())

fun TextView.toFloat(): Float = text().toFloat()

fun TextView.toDouble(): Double = text().toDouble()

fun TextView.setHtml(htmlAsString: String) {
    text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(htmlAsString, Html.FROM_HTML_MODE_COMPACT)
    } else {
        Html.fromHtml(htmlAsString)
    }
    movementMethod = LinkMovementMethod.getInstance()
}

fun TextView.setHtml(@StringRes htmlAsResId: Int) {
    val html = this.context.getString(htmlAsResId)
    setHtml(html)
}
