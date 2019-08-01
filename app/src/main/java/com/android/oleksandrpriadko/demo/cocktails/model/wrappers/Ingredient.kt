package com.android.oleksandrpriadko.demo.cocktails.model.wrappers

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import com.android.oleksandrpriadko.demo.cocktails.drinkdetails.MeasureUnit
import com.android.oleksandrpriadko.demo.cocktails.model.CocktailsRealmRepoExtension
import com.android.oleksandrpriadko.loggalitic.LogPublishService
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Ingredient(@SerializedName(CocktailsRealmRepoExtension.INGREDIENT_FIELD_NAME)
                      @PrimaryKey
                      var name: String) : RealmObject() {
    @SerializedName(CocktailsRealmRepoExtension.INGREDIENT_FIELD_ID)
    var id: String = ""
    var imageUrl: String = ""
    var description: String = NO_DESCRIPTION
    var type: String = NO_TYPE
    var measure: String = ""
    var measureUnitAsString: String = MeasureUnit.AS_IS.toString()

    constructor() : this("")

    fun getMeasureUnit(): MeasureUnit = MeasureUnit.valueOf(measureUnitAsString)

    fun hasEmptyFields(isDatabaseCheck: Boolean): Boolean {
        val isMeasureEmpty = if (!isDatabaseCheck) {
            measure.isEmpty().or(measureUnitAsString.equals(MeasureUnit.AS_IS.toString(), true))
        } else {
            false
        }
        return name.isEmpty()
                .or(imageUrl.isEmpty())
                .or(id.isEmpty())
                .or(description.isEmpty())
                .or(description.equals(NO_DESCRIPTION, true))
                .or(type.isEmpty())
                .or(type.equals(NO_TYPE, true))
                .or(isMeasureEmpty)
    }

    fun fillEmptyFields(donor: Ingredient?): Boolean {
        if (donor == null) {
            return false
        }

        var isChanged = false

        if (isFieldCanBeChanged(name, donor.name)) {
            logState("$name to be changed to donor name = ${donor.name}")
            name = donor.name
            if (!isChanged) {
                isChanged = true
            }
        }
        if (isFieldCanBeChanged(id, donor.id)) {
            logState("$id to be changed to donor id = ${donor.id}")
            id = donor.id
            if (!isChanged) {
                isChanged = true
            }
        }
        if (isFieldCanBeChanged(imageUrl, donor.imageUrl)) {
            logState("imageUrl to be changed to donor imageUrl")
            imageUrl = donor.imageUrl
            if (!isChanged) {
                isChanged = true
            }
        }
        if (isFieldCanBeChanged(description, donor.description)) {
            logState("description to be changed to donor description")
            description = donor.description
            if (!isChanged) {
                isChanged = true
            }
        }
        if (isFieldCanBeChanged(type, donor.type)) {
            logState("$type to be changed to donor type = ${donor.type}")
            type = donor.type
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
                .or(receiver.equals(NO_DESCRIPTION, true))
                .or(receiver.equals(NO_TYPE, true))

        return isDonorValid.and(isReceiverEligible) && !receiver.equals(donor, true)
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

    private fun logState(message: String) {
        LogPublishService.logger().e(this::class.java.simpleName, message)
    }

    override fun toString(): String {
        return "Ingredient(name='$name', " +
                "id='$id', " +
                "imageUrl='$imageUrl', " +
                "description='${description.isNotEmpty()}', " +
                "type='$type'," +
                " measure='$measure', " +
                "measureUnitAsString='$measureUnitAsString')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Ingredient) return false

        if (name != other.name) return false
        if (id != other.id) return false
        if (imageUrl != other.imageUrl) return false
        if (description != other.description) return false
        if (type != other.type) return false
        if (measure != other.measure) return false
        if (measureUnitAsString != other.measureUnitAsString) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + imageUrl.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + measure.hashCode()
        result = 31 * result + measureUnitAsString.hashCode()
        return result
    }


    companion object {
        const val NO_DESCRIPTION = "No description"
        const val NO_TYPE = "Undefined"
        val INVALID = Ingredient("invalid")
    }
}