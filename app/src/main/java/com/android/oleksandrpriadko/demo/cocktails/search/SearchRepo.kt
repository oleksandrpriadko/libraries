package com.android.oleksandrpriadko.demo.cocktails.search

import androidx.lifecycle.LifecycleOwner
import com.android.oleksandrpriadko.demo.cocktails.managers.CocktailManagerFinder
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

        getApi().searchDrink(name).enqueue(object : Callback<FoundDrinksResponse> {
            override fun onResponse(call: Call<FoundDrinksResponse>, response: Response<FoundDrinksResponse>) {
                loadingListener.onLoadingDone()
                if (response.isSuccessful) {
                    onDrinksLoaded(response.body(), loadingListener)
                } else {
                    loadingListener.onLoadingError(Throwable("not successful response"))
                }
            }

            override fun onFailure(call: Call<FoundDrinksResponse>, t: Throwable) {
                loadingListener.onLoadingDone()
                loadingListener.onLoadingError(t)
                loadingListener.noDrinksFound()
            }
        })
    }

    fun popularDrinks(loadingListener: LoadingListener) {
        loadingListener.onLoadingStarted()

        getApi().loadPopularDrinks().enqueue(object : Callback<FoundDrinksResponse> {
            override fun onResponse(call: Call<FoundDrinksResponse>, response: Response<FoundDrinksResponse>) {
                if (response.isSuccessful) {
                    onDrinksLoaded(response.body(), loadingListener)
                } else {
                    loadingListener.onLoadingError(Throwable("not successful response"))
                }
                loadingListener.onLoadingDone()
            }

            override fun onFailure(call: Call<FoundDrinksResponse>, t: Throwable) {
                loadingListener.onLoadingError(t)
                loadingListener.onLoadingDone()
            }

        })
    }

    private fun onDrinksLoaded(response: FoundDrinksResponse?, loadingListener: LoadingListener) {
        response?.let {
            val foundDrinks = it.drinkDetails
            when {
                foundDrinks != null && foundDrinks.isNotEmpty() -> loadingListener.onDrinksFound(foundDrinks)
                else -> loadingListener.noDrinksFound()
            }
        }
    }

    fun filterByIngredients(ingredientNamesCommaSeparated: String, loadingListener: LoadingListener) {
        loadingListener.onLoadingStarted()
        getApi().filterByIngredients(ingredientNamesCommaSeparated).enqueue(object : Callback<FoundDrinksResponse> {
            override fun onResponse(call: Call<FoundDrinksResponse>,
                                    response: Response<FoundDrinksResponse>) {
                loadingListener.onLoadingDone()
                if (response.isSuccessful) {
                    loadingListener.onFilterByIngredient(
                            response.body()?.drinkDetails ?: mutableListOf<DrinkDetails>())
                }
            }

            override fun onFailure(call: Call<FoundDrinksResponse>, t: Throwable) {
                loadingListener.onLoadingDone()
                loadingListener.onLoadingError(t)
                loadingListener.noDrinksFound()
            }

        })
    }

    fun loadListOfIngredients(loadingListener: LoadingListener) {
        loadingListener.onLoadingStarted()
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

    private fun onIngredientsLoaded(loadingListener: LoadingListener,
                                    response: Response<ListOfIngredientsResponse>) {
        loadingListener.onLoadingDone()
        if (response.isSuccessful) {
            val loadedIngredients =
                    response.body()?.ingredientNameList ?: mutableListOf<IngredientName>()
            if (loadedIngredients.isNotEmpty()) {
                CocktailManagerFinder.databaseManager.ingredientDao().deleteAll()
                for (ingredientName in loadedIngredients) {
                    CocktailManagerFinder.databaseManager.ingredientDao().insert(ingredientName)
                }
            }
            loadingListener.onListOfIngredientsLoaded(
                    CocktailManagerFinder.databaseManager.ingredientDao().getAll())
        }
    }

    private fun getApi() = retrofitRepoExtension.getApi(CocktailApi::class.java)

    fun findIngredientMatches(name: String): List<IngredientName>? {
        return CocktailManagerFinder.databaseManager.ingredientDao().findMatchesByName("%$name%")
    }

    fun findIngredient(name: String): IngredientName? {
        return CocktailManagerFinder.databaseManager.ingredientDao().findByName(name)
    }

    override fun cleanUp() {}
}