package com.android.oleksandrpriadko.demo.cocktails.drinkdetails

import androidx.lifecycle.LifecycleOwner
import com.android.oleksandrpriadko.demo.cocktails.model.*
import com.android.oleksandrpriadko.demo.cocktails.model.wrappers.CocktailMapper
import com.android.oleksandrpriadko.demo.cocktails.model.wrappers.Drink
import com.android.oleksandrpriadko.demo.cocktails.model.wrappers.Ingredient
import com.android.oleksandrpriadko.mvp.repo.ObservableRepo
import com.android.oleksandrpriadko.mvp.repo_extension.RetrofitRepoExtension
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory

class DrinkDetailsRepo(lifecycleOwner: LifecycleOwner,
                       baseUrl: String) : ObservableRepo(lifecycleOwner) {

    private val retrofitRepoExtension: RetrofitRepoExtension = RetrofitRepoExtension(
            baseUrl,
            converterFactory = GsonConverterFactory.create())

    fun loadDrink(drinkId: String, listener: DrinkDetailsRepoListener) {
        val api = getApi()
        if (api != null) {
            api.lookupCocktailById(drinkId).enqueue(object : Callback<FoundDrinksResponse> {
                override fun onResponse(call: Call<FoundDrinksResponse>,
                                        response: Response<FoundDrinksResponse>) {
                    onDrinksLoaded(listener, response)
                }

                override fun onFailure(call: Call<FoundDrinksResponse>, t: Throwable) {
                    onLoadingError(listener)
                }
            })
        } else {
            onNoInternet(listener)
        }
    }

    private fun onDrinksLoaded(listener: DrinkDetailsRepoListener,
                               response: Response<FoundDrinksResponse>) {

        listener.onLoadingDone()

        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                val localDrinkDetailsList: MutableList<DrinkDetails> = body.drinkDetails
                        ?: mutableListOf()

                if (localDrinkDetailsList.isNotEmpty()) {
                    val drinkMapped: Drink = CocktailMapper.mapToDrink(localDrinkDetailsList[0])
                    listener.onDrinkLoaded(drinkMapped)
                } else {
                    onLoadingError(listener)
                }
            } else {
                onLoadingError(listener)
            }
        } else {
            onLoadingError(listener)
        }
    }

    fun loadIngredient(drink: Drink, ingredient: Ingredient, listener: DrinkDetailsRepoListener) {
        if (ingredient.hasEmptyFields()) {
            val match = drink.ingredientList.find { it.name.equals(ingredient.name, true) }
            if (match != null && !match.hasEmptyFields()) {
                listener.onIngredientLoaded(match)
            } else {
                listener.onLoadingStarted()
                val api = getApi()
                if (api != null) {
                    api.searchIngredientByName(ingredient.name).enqueue(object : Callback<FoundIngredientsResponse> {
                        override fun onResponse(call: Call<FoundIngredientsResponse>,
                                                response: Response<FoundIngredientsResponse>) {
                            onIngredientLoaded(drink, response, listener)
                        }

                        override fun onFailure(call: Call<FoundIngredientsResponse>, t: Throwable) {
                            onLoadingError(listener)
                        }
                    })
                } else {
                    onNoInternet(listener)
                }
            }
        }
    }

    private fun onIngredientLoaded(drink: Drink,
                                   response: Response<FoundIngredientsResponse>,
                                   listener: DrinkDetailsRepoListener) {
        listener.onLoadingDone()

        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                val loadedIngredientDetailsList: MutableList<IngredientDetails> = body.ingredientList
                        ?: mutableListOf()
                if (loadedIngredientDetailsList.isNotEmpty()) {
                    //fill if not
                    val ingredient = CocktailMapper.mapToIngredient(loadedIngredientDetailsList[0])
                    drink.requestFillIngredient(ingredient)
                    //notify loaded
                    listener.onIngredientLoaded(ingredient)
                } else {
                    onLoadingError(listener)
                }
            } else {
                onLoadingError(listener)
            }
        } else {
            onLoadingError(listener)
        }
    }

    private fun onLoadingError(listener: DrinkDetailsRepoListener) {
        listener.onLoadingDone()
        listener.onLoadingError()
    }

    private fun onNoInternet(listener: DrinkDetailsRepoListener) {
        listener.onLoadingDone()
        listener.onNoInternet()
    }

    private fun getApi() = retrofitRepoExtension.getApi(CocktailApi::class.java)

    override fun cleanUp() {}
}