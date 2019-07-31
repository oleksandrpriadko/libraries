package com.android.oleksandrpriadko.demo.cocktails.model.wrappers

import com.android.oleksandrpriadko.demo.cocktails.drinkdetails.MeasureUnitsConverter
import com.android.oleksandrpriadko.demo.cocktails.model.*

class CocktailMapper {

    companion object {

        fun mapToDrink(drinkDetails: DrinkDetails?): Drink {
            val drinkMapped: Drink
            if (drinkDetails?.idDrink != null && drinkDetails.idDrink?.isNotEmpty() == true) {
                drinkMapped = Drink(drinkDetails.idDrink ?: "")
                drinkMapped.name = drinkDetails.strDrink ?: ""
                drinkMapped.imageUrl = drinkDetails.strDrinkThumb ?: ""
                drinkMapped.ingredientList = extractIngredientList(drinkDetails)
                drinkMapped.instructions = drinkDetails.strInstructions ?: ""
                drinkMapped.category = drinkDetails.strCategory ?: ""
                drinkMapped.alcoholicType = drinkDetails.strAlcoholic ?: ""
                drinkMapped.glassType = drinkDetails.strGlass ?: ""
                drinkMapped.dateModified = drinkDetails.dateModified ?: ""
            } else {
                drinkMapped = Drink.NOT_VALID
            }

            return drinkMapped
        }

        private fun extractIngredientList(drinkDetails: DrinkDetails): MutableList<Ingredient> {
            val ingredientList: MutableList<Ingredient> = mutableListOf()
            drinkDetails.listOfIngredientsNames.forEachIndexed { index, name ->
                //name
                val cleanName: String = cleanIngredientName(name)

                val ingredient = Ingredient(cleanName)
                //measure if valid
                val measure: String? = drinkDetails.listOfIngredientsMeasures[index]
                if (measure != null && measure.isNotEmpty()) {
                    ingredient.measure = measure
                }
                //measure unit
                ingredient.measureUnit = MeasureUnitsConverter.findValidUnit(ingredient.measure)

                ingredientList.add(ingredient)
            }

            return ingredientList
        }

        fun mapToIngredient(ingredientDetails: IngredientDetails?): Ingredient {
            val ingredientMapped = Ingredient(ingredientDetails?.strIngredient ?: "")
            ingredientMapped.id = ingredientDetails?.idIngredient ?: ""
            ingredientMapped.imageUrl = CocktailApi.createIngredientImageUrl(ingredientDetails?.strIngredient, ImageSize.NORMAL)
            ingredientMapped.description = ingredientDetails?.strDescription ?: ""
            ingredientMapped.type = ingredientDetails?.strType ?: ""
            return ingredientMapped
        }

        fun mapToIngredient(ingredientName: IngredientName?): Ingredient {
            return Ingredient(ingredientName?.strIngredient1 ?: "")
        }

        fun cleanIngredientName(draftName: String?): String {
            return draftName?.replace("'", "") ?: ""
        }
    }

}