package com.android.oleksandrpriadko.demo.cocktails.model.wrappers

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import com.android.oleksandrpriadko.demo.cocktails.drinkdetails.MeasureUnit
import com.android.oleksandrpriadko.loggalitic.LogPublishService
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class MeasuredIngredient(@PrimaryKey
                              @SerializedName(MEASURED_INGREDIENT_FIELD_ID)
                              var id: String) : RealmObject() {

    var patronName: String = ""
    var measure: String = ""
    var measureUnitAsString: String = MeasureUnit.AS_IS.toString()

    constructor() : this(ID_NOT_SET)

    fun getMeasureUnit(): MeasureUnit = MeasureUnit.valueOf(measureUnitAsString)

    fun hasEmptyFields(checkMeasureUnit: Boolean = false): Boolean {
        return patronName.isEmpty()
                .or(id.isEmpty().or(id.equals(ID_NOT_SET, true)))
    }

    fun fillEmptyFields(donor: MeasuredIngredient?): Boolean {
        if (donor == null) {
            return false
        }

        var isChanged = false
        if (id.isEmpty()) {
            logState("$id to be changed to donor id = ${donor.id}")
            id = donor.id
            if (!isChanged) {
                isChanged = true
            }
        }

        if (isFieldCanBeChanged(patronName, donor.patronName)) {
            logState("$patronName to be changed to donor patronName = ${donor.patronName}")
            patronName = donor.patronName
            if (!isChanged) {
                isChanged = true
            }
        }
        if (isFieldCanBeChanged(measure, donor.measure)) {
            logState("$measure to be changed to donor measure = ${donor.measure}")
            measure = donor.measure
            if (!isChanged) {
                isChanged = true
            }
        }
        if (measureUnitAsString.equals(MeasureUnit.AS_IS.toString(), true)
                && !donor.measureUnitAsString.equals(MeasureUnit.AS_IS.toString(), true)) {
            logState("$measureUnitAsString to be changed to donor measureUnitAsString = ${donor.measureUnitAsString}")
            measureUnitAsString = donor.measureUnitAsString
            if (!isChanged) {
                isChanged = true
            }
        }

        return isChanged
    }

    private fun isFieldCanBeChanged(receiver: String, donor: String): Boolean {
        val isDonorValid = donor.isNotEmpty()
        val isReceiverEligible = receiver.isEmpty()

        return isDonorValid.and(isReceiverEligible) && !receiver.equals(donor, true)
    }

    fun createSpannableWithParentheses(): SpannableStringBuilder {
        val nameTrimmed = patronName.trim { it <= ' ' }
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

    private fun logState(message: String) {
        if (false) {
            LogPublishService.logger().e(this::class.java.simpleName, message)
        }
    }

    override fun toString(): String {
        return "MeasuredIngredient(id='$id', " +
                "patronName='$patronName', " +
                "measure='$measure', " +
                "measureUnitAsString='$measureUnitAsString')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MeasuredIngredient) return false

        if (id != other.id) return false
        if (patronName != other.patronName) return false
        if (measure != other.measure) return false
        if (measureUnitAsString != other.measureUnitAsString) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + patronName.hashCode()
        result = 31 * result + measure.hashCode()
        result = 31 * result + measureUnitAsString.hashCode()
        return result
    }

    companion object {
        const val MEASURED_INGREDIENT_FIELD_ID = "id"
        const val ID_NOT_SET = "Not set"
    }

}