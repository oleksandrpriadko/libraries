package com.android.oleksandrpriadko.demo.cocktails.model.wrappers

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import com.android.oleksandrpriadko.demo.cocktails.drinkdetails.MeasureUnit

data class Ingredient(var name: String) {
    var id: String = ""
    var imageUrl: String = ""
    var description: String = ""
    var type: String = ""
    var measure: String = ""
    var measureUnit: MeasureUnit = MeasureUnit.AS_IS

    fun hasEmptyFields(): Boolean {
        return name.isEmpty()
                .or(imageUrl.isEmpty())
                .or(id.isEmpty())
                .or(description.isEmpty())
                .or(type.isEmpty())
                .or(measure.isEmpty())
                .or(measureUnit === MeasureUnit.AS_IS)
    }

    fun fillEmptyFields(donor: Ingredient): Boolean {
        var isChanged = false
        if (isFieldCanBeChanged(name, donor.name)) {
            name = donor.name
            if (!isChanged) {
                isChanged = true
            }
        }
        if (isFieldCanBeChanged(id, donor.id)) {
            id = donor.id
            if (!isChanged) {
                isChanged = true
            }
        }
        if (isFieldCanBeChanged(imageUrl, donor.imageUrl)) {
            imageUrl = donor.imageUrl
            if (!isChanged) {
                isChanged = true
            }
        }
        if (isFieldCanBeChanged(description, donor.description)) {
            description = donor.description
            if (!isChanged) {
                isChanged = true
            }
        }
        if (isFieldCanBeChanged(type, donor.type)) {
            type = donor.type
            if (!isChanged) {
                isChanged = true
            }
        }
        if (isFieldCanBeChanged(measure, donor.measure)) {
            measure = donor.measure
            if (!isChanged) {
                isChanged = true
            }
        }
        if (measureUnit === MeasureUnit.AS_IS && donor.measureUnit !== MeasureUnit.AS_IS) {
            measureUnit = donor.measureUnit
            if (!isChanged) {
                isChanged = true
            }
        }

        return isChanged
    }

    private fun isFieldCanBeChanged(receiver: String, donor: String): Boolean {
        return receiver.isEmpty() && !receiver.equals(donor, true)
    }

    fun createSpannableWithParentheses(): SpannableStringBuilder {
        val nameTrimmed = name.trim { it <= ' ' }
        val measureTrimmed = measure.trim { it <= ' ' }

        val patternFormat = if (measureTrimmed.isEmpty()) "%s" else "%s (%s)"
        val whole = String.format(patternFormat, nameTrimmed, measureTrimmed)
        val indexOfMeasure = if (measureTrimmed.isEmpty()) nameTrimmed.length else whole.indexOf("(")
        val spannableStringBuilder = SpannableStringBuilder(whole)
        spannableStringBuilder.setSpan(
                StyleSpan(Typeface.BOLD),
                0,
                indexOfMeasure,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannableStringBuilder
    }

    companion object {
        val INVALID = Ingredient("invalid")
    }
}