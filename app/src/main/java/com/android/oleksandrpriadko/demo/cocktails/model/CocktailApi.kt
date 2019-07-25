package com.android.oleksandrpriadko.demo.cocktails.model

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CocktailApi {

    @GET(ACTION_SEARCH)
    fun searchDrinkByName(@Query(SEARCH_COCKTAIL) name: String): Call<FoundDrinksResponse>

    @GET(ACTION_SEARCH)
    fun searchIngredientByName(@Query(SEARCH_INGREDIENT) name: String): Call<Any>

    @GET(ACTION_LOOKUP)
    fun lookupCocktailById(@Query(LOOKUP_COCKTAIL) drinkId: String): Call<LookupCocktailDetailsResponse>

    @GET(ACTION_LOOKUP)
    fun lookupIngredient(@Query(LOOKUP_INGREDIENT) name: String): Call<Any>

    @GET(ACTION_FILTER)
    fun filterDrinksByIngredients(@Query(FILTER_INGREDIENTS) name: String): Call<FoundDrinksResponse>

    @GET(ACTION_LIST + LIST_INGREDIENTS)
    fun loadListOfAllIngredients(): Call<ListOfIngredientsResponse>

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

        fun createIngredientImageUrl(ingredientName: String): String {
            return "https://www.thecocktaildb.com/images/ingredients/$ingredientName.png"
        }
    }
}

enum class ActionType(val description: String) {
    ACTION_SEARCH(CocktailApi.ACTION_SEARCH),
    ACTION_LOOKUP(CocktailApi.ACTION_LOOKUP),
    ACTION_FILTER(CocktailApi.ACTION_FILTER)
}

enum class SearchType(val description: String) {
    SEARCH_COCKTAIL(CocktailApi.SEARCH_COCKTAIL),
    SEARCH_INGREDIENT(CocktailApi.SEARCH_INGREDIENT)
}

enum class LookupType(val description: String) {
    LOOKUP_COCKTAIL(CocktailApi.LOOKUP_COCKTAIL),
    LOOKUP_INGREDIENT(CocktailApi.LOOKUP_INGREDIENT)
}

enum class FilterType(val description: String) {
    FILTER_INGREDIENT(CocktailApi.FILTER_INGREDIENTS)
}