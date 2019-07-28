package com.android.oleksandrpriadko.demo.cocktails.model

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CocktailApi {

    @GET(ACTION_SEARCH)
    fun searchDrinkByName(@Query(SEARCH_COCKTAIL) name: String): Call<FoundDrinksResponse>

    @GET(ACTION_SEARCH)
    fun searchIngredientByName(@Query(SEARCH_INGREDIENT) name: String): Call<FoundIngredientsResponse>

    @GET(ACTION_LOOKUP)
    fun lookupCocktailById(@Query(LOOKUP_COCKTAIL) drinkId: String): Call<FoundDrinksResponse>

    @GET(ACTION_LOOKUP)
    fun lookupIngredient(@Query(LOOKUP_INGREDIENT) name: String): Call<FoundIngredientNamesResponse>

    @GET(ACTION_FILTER)
    fun filterDrinksByIngredients(@Query(FILTER_INGREDIENTS) name: String): Call<FoundDrinksResponse>

    @GET(ACTION_LIST + LIST_INGREDIENTS)
    fun loadListOfAllIngredients(): Call<FoundIngredientNamesResponse>

    @GET(ACTION_POPULAR)
    fun loadPopularDrinks(): Call<FoundDrinksResponse>

    companion object {
        const val ACTION_SEARCH = "search.php"
        const val ACTION_LOOKUP = "lookup.php"
        const val ACTION_FILTER = "filter.php"
        const val ACTION_LIST = "list.php"
        const val ACTION_POPULAR = "popular.php"

        const val SEARCH_COCKTAIL = "s"
        const val SEARCH_INGREDIENT = "i"

        const val LOOKUP_COCKTAIL = "i"
        const val LOOKUP_INGREDIENT = "iid"

        const val FILTER_INGREDIENTS = "i"

        const val LIST_INGREDIENTS = "?i=list"

        fun createIngredientImageUrl(ingredientName: String, imageSize: ImageSize): String {
            return when (imageSize) {
                ImageSize.SMALL -> "https://www.thecocktaildb.com/images/ingredients/$ingredientName-Small.png"
                ImageSize.MEDIUM -> "https://www.thecocktaildb.com/images/ingredients/$ingredientName-Medium.png"
                ImageSize.NORMAL -> "https://www.thecocktaildb.com/images/ingredients/$ingredientName.png"
            }
        }

        fun ingredientNamesToString(ingredients: List<IngredientName>): String {
            return ingredients.joinToString(separator = ",") {
                it.strIngredient1.replace("\\s".toRegex(), "_")
            }
        }
    }
}

enum class ImageSize {
    SMALL,
    MEDIUM,
    NORMAL
}