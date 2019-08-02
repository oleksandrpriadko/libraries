package com.android.oleksandrpriadko.demo.cocktails.search

import androidx.lifecycle.LifecycleOwner
import com.android.oleksandrpriadko.demo.cocktails.model.*
import com.android.oleksandrpriadko.demo.cocktails.model.wrappers.CocktailMapper
import com.android.oleksandrpriadko.demo.cocktails.model.wrappers.Drink
import com.android.oleksandrpriadko.demo.cocktails.model.wrappers.Ingredient
import com.android.oleksandrpriadko.mvp.repo.ObservableRepo
import com.android.oleksandrpriadko.mvp.repo_extension.RetrofitRepoExtension
import io.realm.RealmResults
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class SearchRepo(lifecycleOwner: LifecycleOwner,
                 baseUrl: String) : ObservableRepo(lifecycleOwner) {

    private val retrofitRepoExtension: RetrofitRepoExtension = RetrofitRepoExtension(
            baseUrl,
            converterFactory = GsonConverterFactory.create())

    private val charRange : CharRange = ('a'..'z')

    private val realmRepoExtension: CocktailsRealmRepoExtension = CocktailsRealmRepoExtension()

    private var multiIngredientRequestExecutor: RequestExecutorMultiIngredients? = null

    fun searchDrinkByName(name: String, listener: SearchRepoListener) {
        listener.onLoadingStarted()

        val api = getApi()

        if (api != null) {
            api.searchDrinkByName(name).enqueue(object : Callback<FoundDrinksResponse> {
                override fun onResponse(call: Call<FoundDrinksResponse>,
                                        response: Response<FoundDrinksResponse>) {
                    onDrinksLoaded(response, listener, doMerge = false, doSort = false)
                }

                override fun onFailure(call: Call<FoundDrinksResponse>, t: Throwable) {
                    onNoDrinksFound(listener)
                }
            })
        } else {
            onNoInternet(listener)

            checkDrinkInDbContainsName(name, listener)

            listener.onLoadingDone()
        }
    }

    fun loadPopularDrinks(listener: SearchRepoListener) {
        listener.onLoadingStarted()

        val api = getApi()

        if (api != null) {
            api.loadPopularDrinks().enqueue(object : Callback<FoundDrinksResponse> {
                override fun onResponse(call: Call<FoundDrinksResponse>,
                                        response: Response<FoundDrinksResponse>) {
                    onDrinksLoaded(response, listener, doMerge = false, doSort = false)
                }

                override fun onFailure(call: Call<FoundDrinksResponse>, t: Throwable) {
                    onNoDrinksFound(listener)
                }
            })
        } else {
            onNoInternet(listener)

            checkDrinkInDbContainsName(charRange.random().toString(), listener)

            listener.onLoadingDone()
        }
    }

    private fun checkDrinkInDbContainsName(drinkName: String, listener: SearchRepoListener): Boolean {
        val drinksFromDb: List<Drink>? = realmRepoExtension.findDrinkContains(drinkName)

        if (drinksFromDb.isNullOrEmpty()) {
            listener.noDrinksFound()
            return false
        } else {
            listener.onDrinksFound(drinksFromDb)
            listener.onLoadingDone()
            realmRepoExtension.logState("drinksList found in database")
        }
        return false
    }

    private fun onDrinksLoaded(response: Response<FoundDrinksResponse>,
                               listener: SearchRepoListener,
                               doMerge: Boolean,
                               doSort: Boolean,
                               searchQueries: List<String> = listOf()) {
        listener.onLoadingDone()

        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                val foundDrinkDetailsDetailsList: MutableList<DrinkDetails> = body.drinkDetails
                        ?: mutableListOf()
                if (foundDrinkDetailsDetailsList.isNotEmpty()) {
                    val distinctList: List<DrinkDetails>
                    if (doMerge) {
                        distinctList = foundDrinkDetailsDetailsList.distinctBy { it.idDrink }
                    } else {
                        distinctList = foundDrinkDetailsDetailsList
                    }
                    val drinksListMapped: List<Drink> = distinctList.map {
                        CocktailMapper.mapToDrink(it)
                    }
                    if (doSort && searchQueries.isNotEmpty()) {
                        Collections.sort(drinksListMapped, object : Comparator<Drink> {
                            override fun compare(o1: Drink?, o2: Drink?): Int {
                                if (o1 === o2) return 0

                                return if (o1 != null) {
                                    if (o2 == null) {
                                        -1
                                    } else {
                                        o1.calculateIngredientMatches(searchQueries)
                                                .compareTo(o2.calculateIngredientMatches(searchQueries))
                                    }
                                } else if (o2 != null) {
                                    1
                                } else {
                                    0
                                }
                            }
                        })
                    }
                    realmRepoExtension.saveDrinks(drinksListMapped)

                    listener.onDrinksFound(drinksListMapped)
                    retrofitRepoExtension.logState("drinksList downloaded and mapped")
                } else {
                    onNoDrinksFound(listener)
                }
            } else {
                onNoDrinksFound(listener)
            }
        } else {
            onNoDrinksFound(listener)
        }

    }

    /**
     * @param searchQueries List<Pair<String, Int>> int - number of ingredients in query, higher number -
     * earlier position in response list
     */
    fun filterDrinksByIngredients(searchQueries: List<Pair<String, Int>>, listener: SearchRepoListener) {
        listener.onLoadingStarted()

        val api = getApi()

        if (api != null) {
            if (multiIngredientRequestExecutor != null) {
                multiIngredientRequestExecutor?.cancel(true)
            }

            multiIngredientRequestExecutor = RequestExecutorMultiIngredients(
                    api,
                    searchQueries,
                    object : RequestExecutorMultiIngredients.RequestExecutorListener {
                        override fun onLoaded(response: Response<FoundDrinksResponse>) {
                            onDrinksLoaded(response,
                                    listener,
                                    doMerge = true,
                                    doSort = true,
                                    searchQueries = searchQueries.map { it.first })
                        }

                        override fun onNoDrinksFound() {
                            onNoDrinksFound(listener)
                        }
                    })
            multiIngredientRequestExecutor?.execute()
        } else {
            onNoInternet(listener)

            //TODO: find matches in database

            listener.onLoadingDone()
        }
    }

    fun loadAllIngredients(listener: SearchRepoListener) {
        listener.onLoadingStarted()

        val api = getApi()

        if (api != null) {
            api.loadAllIngredients().enqueue(object : Callback<FoundIngredientNamesResponse> {
                override fun onResponse(call: Call<FoundIngredientNamesResponse>,
                                        response: Response<FoundIngredientNamesResponse>) {
                    onIngredientsLoaded(listener, response)
                }

                override fun onFailure(call: Call<FoundIngredientNamesResponse>, t: Throwable) {
                    onNoIngredientsFound(listener)
                }
            })
        } else {
            onNoInternet(listener)

            checkAllIngredientsInDb(listener)

            listener.onLoadingDone()
        }
    }

    private fun checkAllIngredientsInDb(listener: SearchRepoListener): Boolean {
        val ingredientsListFromDb: RealmResults<Ingredient>? = realmRepoExtension.findAllIngredients()

        if (ingredientsListFromDb.isNullOrEmpty()) {
            listener.noIngredientsFound()
        } else {
            listener.onLoadingDone()
            listener.onIngredientsLoaded(ingredientsListFromDb)
            realmRepoExtension.logState("ingredientsList in database")
            return true
        }
        return false
    }

    private fun onIngredientsLoaded(listener: SearchRepoListener,
                                    response: Response<FoundIngredientNamesResponse>) {
        listener.onLoadingDone()

        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                val loadedIngredientNamesList: MutableList<IngredientName> = body.ingredientList
                        ?: mutableListOf()
                if (loadedIngredientNamesList.isNotEmpty()) {
                    val ingredientsMappedList: List<Ingredient> = loadedIngredientNamesList.map {
                        CocktailMapper.mapToIngredient(it)
                    }
                    if (ingredientsMappedList.isNotEmpty()) {
                        realmRepoExtension.saveIngredients(ingredientsMappedList)
                        listener.onIngredientsLoaded(ingredientsMappedList)
                        retrofitRepoExtension.logState("ingredientList downloaded and mapped")
                    } else {
                        onNoIngredientsFound(listener)
                    }
                } else {
                    onNoIngredientsFound(listener)
                }
            } else {
                onNoIngredientsFound(listener)
            }
        } else {
            onNoIngredientsFound(listener)
        }
    }

    private fun getApi() = retrofitRepoExtension.getApi(CocktailApi::class.java)

    fun findIngredientMatches(name: String): List<Ingredient>? {
        return realmRepoExtension.findIngredientContains(name)
    }

    fun findIngredientByName(name: String): Ingredient? {
        return Ingredient(name)
    }

    private fun onNoDrinksFound(listener: SearchRepoListener) {
        listener.onLoadingDone()
        listener.onLoadingError()
        listener.noDrinksFound()
    }

    private fun onNoIngredientsFound(listener: SearchRepoListener) {
        listener.onLoadingDone()
        listener.onLoadingError()
        listener.noIngredientsFound()
    }

    private fun onNoInternet(listener: SearchRepoListener) {
        listener.onNoInternet()
    }

    override fun cleanUp() {
        retrofitRepoExtension.cleanUp()
        realmRepoExtension.cleanUp()
    }

    override fun enableLog(): Boolean {
        return false
    }
}