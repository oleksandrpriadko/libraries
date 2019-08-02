package com.android.oleksandrpriadko.demo.cocktails.model.wrappers

import com.android.oleksandrpriadko.demo.cocktails.model.CocktailApi
import com.android.oleksandrpriadko.demo.cocktails.model.DrinkDetails
import com.android.oleksandrpriadko.demo.cocktails.model.IngredientDetails
import com.android.oleksandrpriadko.demo.cocktails.model.IngredientName
import io.realm.RealmList

class CocktailMapper {

    companion object {

        fun mapToDrink(drinkDetails: DrinkDetails?): Drink {
            val drinkMapped: Drink
            if (drinkDetails?.idDrink != null && drinkDetails.idDrink?.isNotEmpty() == true) {
                drinkMapped = Drink(drinkDetails.idDrink ?: "")
                drinkMapped.name = drinkDetails.strDrink ?: ""
                drinkMapped.imageUrl = drinkDetails.strDrinkThumb ?: ""
                drinkMapped.ingredientList = RealmList()
                drinkMapped.ingredientList.addAll(extractIngredientList(drinkDetails))
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

        private fun extractIngredientList(drinkDetails: DrinkDetails): MutableList<MeasuredIngredient> {
            val ingredientList: MutableList<MeasuredIngredient> = mutableListOf()
            drinkDetails.listOfIngredientsNames.forEachIndexed { index, name ->
                //name
                val cleanName: String = cleanIngredientName(name)

                val measuredIngredient = MeasuredIngredient("${drinkDetails.strDrink}$cleanName")
                //patron name
                measuredIngredient.patronName = cleanName
                //measure if valid
                val measure: String? = drinkDetails.listOfIngredientsMeasures[index]
                if (measure != null && measure.isNotEmpty()) {
                    measuredIngredient.measure = measure
                }
                //measure unit
//                measuredIngredient.measureUnitAsString = MeasureUnitsConverter.findValidUnit(measuredIngredient.measure).toString()

                ingredientList.add(measuredIngredient)
            }

            return ingredientList
        }

        fun mapToIngredient(ingredientDetails: IngredientDetails?): Ingredient {
            val draftIngredientName: String = ingredientDetails?.strIngredient ?: ""
            val ingredientMapped = Ingredient(cleanIngredientName(draftIngredientName))
            ingredientMapped.id = ingredientDetails?.idIngredient ?: ""
            ingredientMapped.imageUrl = CocktailApi.createIngredientImageUrl(ingredientDetails?.strIngredient)
            ingredientMapped.description = ingredientDetails?.strDescription ?: ""
            ingredientMapped.type = ingredientDetails?.strType ?: ""
            return ingredientMapped
        }

        fun mapToIngredient(ingredientName: IngredientName?): Ingredient {
            return Ingredient(cleanIngredientName(ingredientName?.strIngredient1 ?: ""))
        }

        fun cleanIngredientName(draftName: String?): String {
            val words: MutableList<String>? = draftName?.split(" ")?.toMutableList()

            var firstLetterCapitalised = ""

            if (words != null) {
                for (word in words) {
                    firstLetterCapitalised += "${word.capitalize()} "
                }
            }

            firstLetterCapitalised = firstLetterCapitalised.trim()

            return firstLetterCapitalised.replace("'", "")
        }
    }

}