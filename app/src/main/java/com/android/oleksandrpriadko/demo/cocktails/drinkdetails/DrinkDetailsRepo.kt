package com.android.oleksandrpriadko.demo.cocktails.drinkdetails

import androidx.lifecycle.LifecycleOwner
import com.android.oleksandrpriadko.demo.cocktails.model.*
import com.android.oleksandrpriadko.demo.cocktails.model.wrappers.CocktailMapper
import com.android.oleksandrpriadko.demo.cocktails.model.wrappers.Drink
import com.android.oleksandrpriadko.demo.cocktails.model.wrappers.Ingredient
import com.android.oleksandrpriadko.demo.cocktails.model.wrappers.MeasuredIngredient
import com.android.oleksandrpriadko.mvp.repo.ObservableRepo
import com.android.oleksandrpriadko.mvp.repo_extension.RetrofitRepoExtension
import com.android.oleksandrpriadko.retrofit.ConnectionStatusReceiver
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

    private fun checkDrinkInDb(drinkId: String,
                               listener: DrinkDetailsRepoListener,
                               onlyFilledDrink: Boolean = ConnectionStatusReceiver.isOnline): Boolean {
        val fromDb: Drink? = when {
            onlyFilledDrink -> realmRepoExtension.findDrinkByIdNullIfEmpty(drinkId)
            else -> realmRepoExtension.findDrinkById(drinkId)
        }
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

    fun loadIngredient(measuredIngredient: MeasuredIngredient, listener: DrinkDetailsRepoListener) {
        val ingredient: Ingredient? = realmRepoExtension.findIngredientLikeNullIfEmpty(measuredIngredient.patronName)
        when {
            ingredient == null || ingredient.hasEmptyFields() -> {
                listener.onLoadingStarted()

                if (!checkIngredientInDb(measuredIngredient.patronName, listener)) {
                    loadIngredientFromServer(measuredIngredient.patronName, listener)
                }
            }
            else -> {
                listener.onIngredientLoaded(ingredient)
                logState("${ingredient.name} already good")
            }
        }
    }

    private fun checkIngredientInDb(nameOfIngredient: String,
                                    listener: DrinkDetailsRepoListener,
                                    onlyFilledDrink: Boolean = ConnectionStatusReceiver.isOnline): Boolean {
        val fromDb: Ingredient? =
                if (onlyFilledDrink) {
                    realmRepoExtension.findIngredientLikeNullIfEmpty(nameOfIngredient)
                } else {
                    realmRepoExtension.findIngredientLike(nameOfIngredient)
                }

        if (fromDb != null) {
            listener.onLoadingDone()
            listener.onIngredientLoaded(fromDb)
            realmRepoExtension.logState("$nameOfIngredient in database, fields filled")
            return true
        }
        return false
    }

    private fun loadIngredientFromServer(nameOfIngredient: String,
                                         listener: DrinkDetailsRepoListener) {
        val api = getApi()

        if (api != null) {

            api.searchIngredientByName(nameOfIngredient).enqueue(object : Callback<FoundIngredientsResponse> {
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

    override fun enableLog(): Boolean {
        return false
    }

}