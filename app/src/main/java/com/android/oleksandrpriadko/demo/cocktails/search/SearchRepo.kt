package com.android.oleksandrpriadko.demo.cocktails.search

import android.os.AsyncTask
import androidx.lifecycle.LifecycleOwner
import com.android.oleksandrpriadko.demo.cocktails.managers.CocktailManagerFinder
import com.android.oleksandrpriadko.demo.cocktails.model.*
import com.android.oleksandrpriadko.loggalitic.LogPublishService
import com.android.oleksandrpriadko.mvp.repo.ObservableRepo
import com.android.oleksandrpriadko.mvp.repo_extension.RetrofitRepoExtension
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class SearchRepo(lifecycleOwner: LifecycleOwner,
                 private val baseUrl: String) : ObservableRepo(lifecycleOwner) {

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
            val executor = ExecutorHandlerFindDrinksDetails(baseUrl,
                    listOf(ingredientNamesCommaSeparated, ingredientNamesCommaSeparated),
                    object : ExecutorHandlerFindDrinksDetails.ExecutorLoadListener {
                        override fun onLoadingStarted() {

                        }

                        override fun onLoaded(foundDrinksDetails: List<DrinkDetails>) {

                        }

                    })
            executor.execute()
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

class ExecutorHandlerFindDrinksDetails(private val baseUrl: String,
                                       private val ingredientList: List<String>,
                                       private var listener: ExecutorLoadListener) : AsyncTask<Void, Void, List<DrinkDetails>>() {

    private lateinit var pool: ExecutorService
    private lateinit var apiService: CocktailApi

    init {
        intiRetrofit()
    }

    private fun intiRetrofit() {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BASIC
        val client = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build()
        val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        apiService = retrofit.create(CocktailApi::class.java)
    }

    override fun onPreExecute() {
        super.onPreExecute()
        listener.onLoadingStarted()
    }

    override fun doInBackground(vararg voids: Void): List<DrinkDetails> {
        // if we are here - it is means that cache is expired or empty
        val foundDrinksDetails = mutableListOf<DrinkDetails>()
        pool = Executors.newFixedThreadPool(ingredientList.size)
        //execute runnable in ExecutorService
        for (i in 0 until ingredientList.size) {
            pool.execute(SingleRequest(foundDrinksDetails, ingredientList[i], apiService))
        }
        LogPublishService.logger().i(TAG, "started")
        pool.shutdown()
        //check if all runnable is finished
        while (!pool.isTerminated) {
            try {
                if (!pool.awaitTermination(TERMINATION_PERIOD_MS, TimeUnit.MILLISECONDS)) {
                    LogPublishService.logger().i(TAG, "processing")
                }
            } catch (ignore: InterruptedException) {
                Thread.currentThread().interrupt()
            }

        }
        //return filled array to onPostExecute
        return foundDrinksDetails
    }

    override fun onPostExecute(foundDrinksDetails: List<DrinkDetails>) {
        super.onPostExecute(foundDrinksDetails)
        if (foundDrinksDetails.isNotEmpty()) {
            LogPublishService.logger().i("ExecutorAsync", "not empty list")
        }
        LogPublishService.logger().i(TAG, "done")
    }

    override fun onCancelled(foundDrinksDetails: List<DrinkDetails>) {
        //if canceled - shutdown immediately
        pool.shutdownNow()
        listener = object : ExecutorLoadListener {
            override fun onLoadingStarted() {}

            override fun onLoaded(foundDrinksDetails: List<DrinkDetails>) {

            }
        }
        super.onCancelled(foundDrinksDetails)
        LogPublishService.logger().i(TAG, "canceled")
        LogPublishService.logger().i(TAG, "${pool.isShutdown} isShutDown")
        LogPublishService.logger().i(TAG, "${pool.isTerminated} isTerminated")
    }

    private inner class SingleRequest internal constructor(var foundDrinksDetails: MutableList<DrinkDetails>,
                                                           private val ingredientString: String,
                                                           private val api: CocktailApi) : Runnable {

        override fun run() {

            val call: Call<FoundDrinksResponse> = api.filterDrinksByIngredients(ingredientString)
            try {
                val response = call.execute()
                if (response.isSuccessful) {
                    val body = response.body()
                    body?.let {
                        foundDrinksDetails.addAll(it.drinkDetails ?: mutableListOf())
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                LogPublishService.logger().i(TAG, "data $ingredientString failed")
            }

        }
    }

    interface ExecutorLoadListener {

        fun onLoadingStarted()

        fun onLoaded(foundDrinksDetails: List<DrinkDetails>)

    }

    companion object {

        private val TAG = ExecutorHandlerFindDrinksDetails::class.java.simpleName
        private const val TERMINATION_PERIOD_MS = 500L
    }
}