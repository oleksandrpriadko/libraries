package com.android.oleksandrpriadko.extension

import android.text.ParcelableSpan
import android.text.SpannableStringBuilder
import android.text.Spanned

fun SpannableStringBuilder.isEqual(other: SpannableStringBuilder): Boolean {
    if (toString() == other.toString()) {
        val otherSpans = other.getSpans(0, other.length, Any::class.java)
        val spans = getSpans(0, length, Any::class.java)
        if (spans.size == otherSpans.size) {
            for (i in spans.indices) {
                val thisSpan = spans[i]
                val otherSpan = otherSpans[i]
                if (getSpanStart(thisSpan) != other.getSpanStart(otherSpan) ||
                        getSpanEnd(thisSpan) != other.getSpanEnd(otherSpan) ||
                        getSpanFlags(thisSpan) != other.getSpanFlags(otherSpan)) {
                    return false
                }
            }
            return true
        }
    }
    return false
}

fun SpannableStringBuilder.append(string: String, vararg spans: ParcelableSpan): SpannableStringBuilder {
    append(string)
    for (span in spans) {
        setSpan(span, length - string.length, length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
    }
    return this
}
