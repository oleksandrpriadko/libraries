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

    fun searchDrinkByName(name: String, loadingListener: LoadingListener) {
        loadingListener.onLoadingStarted()
        val api = getApi()
        if (api != null) {
            api.searchDrinkByName(name).enqueue(object : Callback<FoundDrinksResponse> {
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
        } else {
            loadingListener.onLoadingDone()
            loadingListener.onNoInternet()
        }
    }

    fun loadPopularDrinks(loadingListener: LoadingListener) {
        loadingListener.onLoadingStarted()
        val api = getApi()
        if (api != null) {
            api.loadPopularDrinks().enqueue(object : Callback<FoundDrinksResponse> {
                override fun onResponse(call: Call<FoundDrinksResponse>, response: Response<FoundDrinksResponse>) {
                    loadingListener.onLoadingDone()

                    if (response.isSuccessful) {
                        onDrinksLoaded(response.body(), loadingListener)
                    } else {
                        loadingListener.onLoadingError(Throwable("not successful response"))
                    }
                }

                override fun onFailure(call: Call<FoundDrinksResponse>, t: Throwable) {
                    loadingListener.onLoadingError(t)
                    loadingListener.onLoadingDone()
                }
            })
        } else {
            loadingListener.onLoadingDone()
            loadingListener.onNoInternet()
        }
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

    fun filterDrinksByIngredients(ingredientNamesCommaSeparated: String, loadingListener: LoadingListener) {
        loadingListener.onLoadingStarted()
        val api = getApi()
        if (api != null) {
            api.filterDrinksByIngredients(ingredientNamesCommaSeparated).enqueue(object : Callback<FoundDrinksResponse> {
                override fun onResponse(call: Call<FoundDrinksResponse>,
                                        response: Response<FoundDrinksResponse>) {
                    loadingListener.onLoadingDone()

                    if (response.isSuccessful) {
                        loadingListener.onDrinksFound(
                                response.body()?.drinkDetails ?: mutableListOf<DrinkDetails>())
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
        } else {
            loadingListener.onLoadingDone()
            loadingListener.onNoInternet()
        }
    }

    fun loadListOfAllIngredients(loadingListener: LoadingListener) {
        loadingListener.onLoadingStarted()

        val api = getApi()

        if (api != null) {
            api.loadListOfAllIngredients().enqueue(object : Callback<FoundIngredientNamesResponse> {
                override fun onResponse(call: Call<FoundIngredientNamesResponse>, response: Response<FoundIngredientNamesResponse>) {
                    onAllIngredientsLoaded(loadingListener, response)
                }

                override fun onFailure(call: Call<FoundIngredientNamesResponse>, t: Throwable) {
                    loadingListener.onLoadingDone()

                    val allIngredients = CocktailManagerFinder.databaseManager
                            .ingredientDao().getAll()
                    if (allIngredients.isNotEmpty()) {
                        loadingListener.onListOfIngredientsLoaded(allIngredients)
                    } else {
                        loadingListener.onLoadingError(t)
                    }
                }
            })
        } else {
            loadingListener.onLoadingDone()
            loadingListener.onNoInternet()
        }
    }

    private fun onAllIngredientsLoaded(loadingListener: LoadingListener,
                                       response: Response<FoundIngredientNamesResponse>) {
        loadingListener.onLoadingDone()

        if (response.isSuccessful) {
            val loadedIngredients =
                    response.body()?.ingredientList
                            ?: mutableListOf<IngredientName>()
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

    fun findIngredientByName(name: String): IngredientName? {
        return CocktailManagerFinder.databaseManager.ingredientDao().findByName(name)
    }

    override fun cleanUp() {}
}