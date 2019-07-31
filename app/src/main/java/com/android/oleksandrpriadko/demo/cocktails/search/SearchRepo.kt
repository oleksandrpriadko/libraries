package com.android.oleksandrpriadko.demo.cocktails.search

import android.os.AsyncTask
import androidx.lifecycle.LifecycleOwner
import com.android.oleksandrpriadko.demo.cocktails.model.*
import com.android.oleksandrpriadko.demo.cocktails.model.wrappers.CocktailMapper
import com.android.oleksandrpriadko.demo.cocktails.model.wrappers.Drink
import com.android.oleksandrpriadko.demo.cocktails.model.wrappers.Ingredient
import com.android.oleksandrpriadko.loggalitic.LogPublishService
import com.android.oleksandrpriadko.mvp.repo.ObservableRepo
import com.android.oleksandrpriadko.mvp.repo_extension.RetrofitRepoExtension
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class SearchRepo(lifecycleOwner: LifecycleOwner,
                 baseUrl: String) : ObservableRepo(lifecycleOwner) {

    private val retrofitRepoExtension: RetrofitRepoExtension = RetrofitRepoExtension(
            baseUrl,
            converterFactory = GsonConverterFactory.create())

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
        }
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
                    listener.onDrinksFound(drinksListMapped)
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
                                    searchQueries = searchQueries.map {
                                        it.first
                                    })
                        }

                        override fun onNoDrinksFound() {
                            onNoDrinksFound(listener)
                        }
                    })
            multiIngredientRequestExecutor?.execute()
        } else {
            onNoInternet(listener)
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
        }
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
                    listener.onIngredientsLoaded(ingredientsMappedList)
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
        return listOf("dummy", "items", "waiting", "for", "database", name).map {
            Ingredient(it)
        }
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
        listener.onLoadingDone()
        listener.onNoInternet()
    }

    override fun cleanUp() {}
}

/**
 * @param searchQueries List<Pair<String, Int>> int - number of ingredients in query, higher number -
 * earlier position in response list
 */
class RequestExecutorMultiIngredients(private val api: CocktailApi,
                                      private val searchQueries: List<Pair<String, Int>>,
                                      private var listener: RequestExecutorListener)
    : AsyncTask<Void, Void, Response<FoundDrinksResponse>?>() {

    private lateinit var pool: ExecutorService

    override fun doInBackground(vararg voids: Void): Response<FoundDrinksResponse>? {
        var parentResponse: Response<FoundDrinksResponse>? = null
        val pairsOfResponsesAndQueries: MutableList<Pair<Response<FoundDrinksResponse>, Int>> = mutableListOf()

        pool = Executors.newFixedThreadPool(searchQueries.size)

        val listener = object : SingleRequestListener {
            override fun onLoaded(response: Response<FoundDrinksResponse>,
                                  numberOfIngredients: Int) {
                pairsOfResponsesAndQueries.add(Pair(response, numberOfIngredients))
            }
        }

        for (searchQuery in searchQueries) {
            pool.execute(SingleRequest(searchQuery, api, listener))
        }

        LogPublishService.logger().i(TAG, "started")

        pool.shutdown()

        while (!pool.isTerminated) {
            try {
                if (!pool.awaitTermination(TERMINATION_PERIOD_MS, TimeUnit.MILLISECONDS)) {
                    LogPublishService.logger().i(TAG, "processing")
                }
            } catch (ignore: InterruptedException) {
                Thread.currentThread().interrupt()
            }
        }

        pairsOfResponsesAndQueries.sortWith(Comparator { o1, o2 ->
            if (o1 != null && o2 != null) {
                o1.second.compareTo(o2.second)
            } else if (o1 != null) {
                -1
            } else if (o2 != null) {
                1
            } else {
                0
            }
        })
        pairsOfResponsesAndQueries.reverse()


        for (i in 0 until pairsOfResponsesAndQueries.size) {
            val pair = pairsOfResponsesAndQueries[i]
            if (parentResponse == null) {
                parentResponse = pair.first
            } else {
                val parentBody = parentResponse.body()
                val localBody = pair.first.body()
                if (parentBody != null) {
                    val localFoundDrinks: MutableList<DrinkDetails>? = localBody?.drinkDetails
                    val alreadyFoundDrinks: MutableList<DrinkDetails>? = parentBody.drinkDetails
                    if (alreadyFoundDrinks != null) {
                        if (localFoundDrinks != null) {
                            alreadyFoundDrinks.addAll(localFoundDrinks)
                        }
                    } else {
                        parentBody.drinkDetails = localFoundDrinks
                    }
                }
            }
        }

        return parentResponse
    }

    override fun onPostExecute(response: Response<FoundDrinksResponse>?) {
        super.onPostExecute(response)

        if (response != null) {
            listener.onLoaded(response)
        } else {
            listener.onNoDrinksFound()
        }

        LogPublishService.logger().i(TAG, "done")
    }

    override fun onCancelled(response: Response<FoundDrinksResponse>?) {
        //if canceled - shutdown immediately
        pool.shutdownNow()
        listener = object : RequestExecutorListener {
            override fun onLoaded(response: Response<FoundDrinksResponse>) {}

            override fun onNoDrinksFound() {}
        }
        super.onCancelled(response)
        LogPublishService.logger().i(TAG, "canceled")
        LogPublishService.logger().i(TAG, "${pool.isShutdown} isShutDown")
        LogPublishService.logger().i(TAG, "${pool.isTerminated} isTerminated")
    }

    private inner class SingleRequest internal constructor(private val searchPair: Pair<String, Int>,
                                                           private val api: CocktailApi,
                                                           private val listener: SingleRequestListener) : Runnable {

        override fun run() {
            LogPublishService.logger().e(TAG, "runnable run")
            val call: Call<FoundDrinksResponse> = api.filterDrinksByIngredients(searchPair.first)
            try {
                val localResponse: Response<FoundDrinksResponse> = call.execute()
                reportSuccessfulResponse(localResponse, listener)
            } catch (e: Exception) {
                e.printStackTrace()
                LogPublishService.logger().i(TAG, "$searchPair failed")
            }

        }

        private fun reportSuccessfulResponse(localResponse: Response<FoundDrinksResponse>,
                                             listener: SingleRequestListener) {
            val localBody = localResponse.body()
            if (localResponse.isSuccessful && localBody != null) {
                listener.onLoaded(localResponse, searchPair.second)
            }
        }
    }

    interface SingleRequestListener {

        fun onLoaded(response: Response<FoundDrinksResponse>, numberOfIngredients: Int)

    }

    interface RequestExecutorListener {

        fun onLoaded(response: Response<FoundDrinksResponse>)

        fun onNoDrinksFound()

    }

    companion object {

        private val TAG = RequestExecutorMultiIngredients::class.java.simpleName
        private const val TERMINATION_PERIOD_MS = 500L
    }
}