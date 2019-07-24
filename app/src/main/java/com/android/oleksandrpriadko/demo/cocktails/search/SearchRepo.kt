package com.android.oleksandrpriadko.demo.cocktails.search

import androidx.lifecycle.LifecycleOwner
import com.android.oleksandrpriadko.demo.cocktails.CocktailManagerFinder
import com.android.oleksandrpriadko.demo.cocktails.model.*
import com.android.oleksandrpriadko.mvp.repo.ObservableRepo
import com.android.oleksandrpriadko.mvp.repo_extension.RetrofitRepoExtension
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory

class SearchRepo(lifecycleOwner: LifecycleOwner,
                 baseUrl: String) : ObservableRepo(lifecycleOwner) {

    private val retrofitRepoExtension: RetrofitRepoExtension = RetrofitRepoExtension(
            baseUrl,
            converterFactory = GsonConverterFactory.create())

    fun searchDrink(name: String, loadingListener: LoadingListener) {
        loadingListener.onLoadingStarted()

        getApi().searchDrink(name).enqueue(object : Callback<SearchDrinkByNameResponse> {
            override fun onResponse(call: Call<SearchDrinkByNameResponse>, response: Response<SearchDrinkByNameResponse>) {
                loadingListener.onLoadingDone()
                if (response.isSuccessful) {
                    onDrinksLoaded(response.body(), loadingListener)
                } else {
                    loadingListener.onLoadingError(Throwable("not successful response"))
                }
            }

            override fun onFailure(call: Call<SearchDrinkByNameResponse>, t: Throwable) {
                loadingListener.onLoadingDone()
                loadingListener.onLoadingError(t)
            }
        })
    }

    private fun onDrinksLoaded(response: SearchDrinkByNameResponse?, loadingListener: LoadingListener) {
        response?.let {
            val foundDrinks = it.drinkDetails
            when {
                foundDrinks != null && foundDrinks.isNotEmpty() -> loadingListener.onDrinksFound(foundDrinks)
                else -> loadingListener.noDrinksFound()
            }
        }
    }

    fun filterByIngredient(ingredientNames: List<IngredientName>, loadingListener: LoadingListener) {
        loadingListener.onLoadingStarted()
        getApi().filterByIngredient(ingredientNames[0].strIngredient1).enqueue(object : Callback<FilterByIngredientResponse> {
            override fun onResponse(call: Call<FilterByIngredientResponse>,
                                    response: Response<FilterByIngredientResponse>) {
                loadingListener.onLoadingDone()
                if (response.isSuccessful) {
                    loadingListener.onFilterByIngredient(
                            response.body()?.drinkDetails ?: mutableListOf<DrinkDetails>())
                }
            }

            override fun onFailure(call: Call<FilterByIngredientResponse>, t: Throwable) {
                loadingListener.onLoadingDone()
                loadingListener.onLoadingError(t)
            }

        })
    }

    fun loadListOfIngredients(loadingListener: LoadingListener) {
        loadingListener.onLoadingStarted()
        if (CocktailManagerFinder.databaseCocktail.ingredientDao().getAll().isNotEmpty()) {
            loadingListener.onListOfIngredientsLoaded(CocktailManagerFinder.databaseCocktail.ingredientDao().getAll())
        } else {
            getApi().listOfIngredients().enqueue(object : Callback<ListOfIngredientsResponse> {
                override fun onResponse(call: Call<ListOfIngredientsResponse>, response: Response<ListOfIngredientsResponse>) {
                    onIngredientsLoaded(loadingListener, response)
                }

                override fun onFailure(call: Call<ListOfIngredientsResponse>, t: Throwable) {
                    loadingListener.onLoadingDone()
                    loadingListener.onLoadingError(t)
                }
            })
        }
    }

    private fun onIngredientsLoaded(loadingListener: LoadingListener,
                                    response: Response<ListOfIngredientsResponse>) {
        loadingListener.onLoadingDone()
        if (response.isSuccessful) {
            val loadedIngredients =
                    response.body()?.ingredientNameList ?: mutableListOf<IngredientName>()
            for (ingredientName in loadedIngredients) {
                CocktailManagerFinder.databaseCocktail.ingredientDao().insert(ingredientName)
            }
            loadingListener.onListOfIngredientsLoaded(loadedIngredients)
        }
    }

    private fun getApi() = retrofitRepoExtension.getApi(CocktailApi::class.java)

    fun findIngredientMatches(name: String): List<IngredientName>? {
        return CocktailManagerFinder.databaseCocktail.ingredientDao().findMatchesByName("%$name%")
    }

    fun findIngredient(name: String): IngredientName? {
        return CocktailManagerFinder.databaseCocktail.ingredientDao().findByName(name)
    }

    override fun cleanUp() {}
}