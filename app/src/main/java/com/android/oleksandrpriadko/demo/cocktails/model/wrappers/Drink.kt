package com.android.oleksandrpriadko.demo.cocktails.model.wrappers

data class Drink(val id: String) {
    var name: String = ""
    var imageUrl: String = ""
    var ingredientList: MutableList<Ingredient> = mutableListOf()
    var instructions: String = ""
    var category: String = ""
    var alcoholicType: String = ""
    var glassType: String = ""
    var dateModified: String = ""

    fun requestFillIngredient(ingredient: Ingredient) {
        val toBeFilled = ingredientList.find {
            it.id.equals(ingredient.id, true)
        }
        if (toBeFilled != null && toBeFilled.hasEmptyFields()) {
            toBeFilled.fillEmptyFields(ingredient)
        }
    }

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

    companion object {
        val NOT_VALID = Drink("-1")
    }
}