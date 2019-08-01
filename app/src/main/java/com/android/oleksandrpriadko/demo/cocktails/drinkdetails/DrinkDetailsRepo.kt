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

    private val realmRepoExtension: CocktailsRealmRepoExtension = CocktailsRealmRepoExtension()

    fun loadDrink(drinkId: String, listener: DrinkDetailsRepoListener) {
        if (!checkDrinkInDb(drinkId, listener)) {
            loadDrinkFromServer(drinkId, listener)
        }
    }

    private fun checkDrinkInDb(drinkId: String, listener: DrinkDetailsRepoListener): Boolean {
        val fromDb: Drink? = realmRepoExtension.findDrinkByIdNullIfEmpty(drinkId)
        if (fromDb != null) {
            listener.onDrinkLoaded(fromDb)
            listener.onLoadingDone()
            realmRepoExtension.logState("${fromDb.name} in database, fields filled")
            return true
        }
        return false
    }

    private fun loadDrinkFromServer(drinkId: String, listener: DrinkDetailsRepoListener) {
        listener.onLoadingStarted()

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
                    realmRepoExtension.saveDrink(drinkMapped)
                    listener.onDrinkLoaded(drinkMapped)
                    retrofitRepoExtension.logState("${drinkMapped.name} downloaded and mapped")
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
        if (ingredient.hasEmptyFields(false)) {
            if (!checkIngredientInDrink(drink, ingredient, listener)) {
                listener.onLoadingStarted()

                if (!checkIngredientInDb(ingredient, listener)) {
                    loadIngredientFromServer(ingredient, listener)
                }
            }
        } else {
            listener.onIngredientLoaded(ingredient)
            logState("${ingredient.name} already good")
        }
    }

    private fun checkIngredientInDrink(drink: Drink,
                                       ingredient: Ingredient,
                                       listener: DrinkDetailsRepoListener): Boolean {
        val matched = drink.ingredientList.find {
            it.name.equals(ingredient.name, true)
        }
        if (matched != null && !matched.hasEmptyFields(false)) {
            listener.onIngredientLoaded(matched)
            logState("${ingredient.name} in drink, fields filled")
            return true
        }
        return false
    }

    private fun checkIngredientInDb(ingredient: Ingredient, listener: DrinkDetailsRepoListener): Boolean {
        val fromDb: Ingredient? = realmRepoExtension.findIngredientLikeNullIfEmpty(ingredient.name)

        if (fromDb != null) {
            listener.onLoadingDone()
            listener.onIngredientLoaded(fromDb)
            realmRepoExtension.logState("${ingredient.name} in database, fields filled")
            return true
        }
        return false
    }

    private fun loadIngredientFromServer(ingredient: Ingredient,
                                         listener: DrinkDetailsRepoListener) {
        val api = getApi()

        if (api != null) {

            api.searchIngredientByName(ingredient.name).enqueue(object : Callback<FoundIngredientsResponse> {
                override fun onResponse(call: Call<FoundIngredientsResponse>,
                                        response: Response<FoundIngredientsResponse>) {
                    onIngredientLoaded(response, listener)
                }

                override fun onFailure(call: Call<FoundIngredientsResponse>, t: Throwable) {
                    onLoadingError(listener)
                }
            })
        } else {
            onNoInternet(listener)
        }
    }

    private fun onIngredientLoaded(response: Response<FoundIngredientsResponse>,
                                   listener: DrinkDetailsRepoListener) {
        listener.onLoadingDone()

        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                val loadedIngredientDetailsList: MutableList<IngredientDetails> = body.ingredientList
                        ?: mutableListOf()
                if (loadedIngredientDetailsList.isNotEmpty()) {
                    //fill if not
                    val ingredientMapped = CocktailMapper.mapToIngredient(loadedIngredientDetailsList[0])
                    // update in realm
                    realmRepoExtension.saveIngredient(ingredientMapped)
                    //notify loaded
                    listener.onIngredientLoaded(ingredientMapped)
                    retrofitRepoExtension.logState("${ingredientMapped.name} downloaded and mapped")
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

    override fun cleanUp() {
        retrofitRepoExtension.cleanUp()
        realmRepoExtension.cleanUp()
    }
}