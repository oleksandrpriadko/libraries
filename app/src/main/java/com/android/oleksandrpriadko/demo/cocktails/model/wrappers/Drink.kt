package com.android.oleksandrpriadko.demo.cocktails.model.wrappers

import com.android.oleksandrpriadko.demo.cocktails.model.CocktailsRealmRepoExtension
import com.android.oleksandrpriadko.loggalitic.LogPublishService
import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Drink(@SerializedName(CocktailsRealmRepoExtension.DRINK_FIELD_ID)
                 @PrimaryKey
                 var id: String) : RealmObject() {
    @SerializedName(CocktailsRealmRepoExtension.DRINK_FIELD_NAME)
    var name: String = ""
    var imageUrl: String = ""
    var ingredientList: RealmList<Ingredient> = RealmList()
    var instructions: String = ""
    var category: String = NO_CATEGORY
    var alcoholicType: String = NO_ALCOHOLIC_TYPE
    var glassType: String = NO_GLASS_TYPE
    var dateModified: String = ""

    constructor() : this("")

    fun calculateIngredientMatches(ingredientNames: List<String>): Int {
        var matchesCount = 0
        for (ingredientName in ingredientNames) {
            val foundMatch: Ingredient? = ingredientList.find { ingredient ->
                ingredient.name.equals(ingredientName, true)
            }
            if (foundMatch != null) {
                matchesCount++
            }
        }
        return matchesCount
    }

    fun hasEmptyFields(): Boolean {
        return id.isEmpty()
                .or(name.isEmpty())
                .or(imageUrl.isEmpty())
                .or(ingredientList.isEmpty())
                .or(instructions.isEmpty())
                .or(category.isEmpty())
                .or(category.equals(NO_CATEGORY, true))
                .or(alcoholicType.isEmpty())
                .or(alcoholicType.equals(NO_ALCOHOLIC_TYPE, true))
                .or(glassType.isEmpty())
                .or(glassType.equals(NO_GLASS_TYPE, true))
    }

    fun fillEmptyFields(donor: Drink): Boolean {
        var isChanged = false

        if (isFieldCanBeChanged(id, donor.id)) {
            id = donor.id
            if (!isChanged) {
                isChanged = true
            }
        }

        if (isFieldCanBeChanged(name, donor.name)) {
            name = donor.name
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

        for (i in 0 until
                if (ingredientList.size > donor.ingredientList.size)
                    ingredientList.size
                else donor.ingredientList.size) {
            var donorIngredient: Ingredient? = null
            var receiverIngredient: Ingredient? = null
            try {
                donorIngredient = donor.ingredientList[i]
            } catch (e: IndexOutOfBoundsException) {
            }
            try {
                receiverIngredient = ingredientList[i]
            } catch (e: IndexOutOfBoundsException) {
            }
            if (receiverIngredient == null && donorIngredient != null) {
                ingredientList.add(donorIngredient)
                isChanged = true
            } else if (receiverIngredient?.fillEmptyFields(donorIngredient) == true) {
                isChanged = true
            }
        }

        if (isFieldCanBeChanged(instructions, donor.instructions)) {
            instructions = donor.instructions
            if (!isChanged) {
                isChanged = true
            }
        }

        if (isFieldCanBeChanged(category, donor.category)) {
            category = donor.category
            if (!isChanged) {
                isChanged = true
            }
        }

        if (isFieldCanBeChanged(alcoholicType, donor.alcoholicType)) {
            alcoholicType = donor.alcoholicType
            if (!isChanged) {
                isChanged = true
            }
        }

        if (isFieldCanBeChanged(glassType, donor.glassType)) {
            glassType = donor.glassType
            if (!isChanged) {
                isChanged = true
            }
        }

        return isChanged
    }

    private fun isFieldCanBeChanged(receiver: String, donor: String): Boolean {
        val isDonorValid = donor.isNotEmpty()
        val isReceiverEligible = receiver.isEmpty()
                .or(receiver.equals(NO_CATEGORY, true))
                .or(receiver.equals(NO_ALCOHOLIC_TYPE, true))
                .or(receiver.equals(NO_GLASS_TYPE, true))

        return isDonorValid.and(isReceiverEligible) && !receiver.equals(donor, true)
    }

    override fun toString(): String {
        return "Drink(id='$id', " +
                "name='$name', " +
                "imageUrl='$imageUrl', " +
                "ingredientList=${ingredientList.size}, " +
                "instructions='${instructions.isNotEmpty()}', " +
                "category='$category', " +
                "alcoholicType='$alcoholicType', " +
                "glassType='$glassType', " +
                "dateModified='$dateModified')"
    }


    private fun logState(message: String) {
        LogPublishService.logger().e(this::class.java.simpleName, message)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Drink) return false

        if (id != other.id) return false
        if (name != other.name) return false
        if (imageUrl != other.imageUrl) return false
        if (ingredientList != other.ingredientList) return false
        for (i in 0 until ingredientList.size) {
            if (ingredientList[i] != other.ingredientList[i]) {
                return false
            }
        }
        if (instructions != other.instructions) return false
        if (category != other.category) return false
        if (alcoholicType != other.alcoholicType) return false
        if (glassType != other.glassType) return false
        if (dateModified != other.dateModified) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + imageUrl.hashCode()
        result = 31 * result + ingredientList.hashCode()
        result = 31 * result + instructions.hashCode()
        result = 31 * result + category.hashCode()
        result = 31 * result + alcoholicType.hashCode()
        result = 31 * result + glassType.hashCode()
        result = 31 * result + dateModified.hashCode()
        return result
    }

    companion object {
        private const val NO_CATEGORY = "Undefined"
        private const val NO_ALCOHOLIC_TYPE = "Undefined"
        private const val NO_GLASS_TYPE = "Undefined"

        val NOT_VALID = Drink("-1")
    }
}