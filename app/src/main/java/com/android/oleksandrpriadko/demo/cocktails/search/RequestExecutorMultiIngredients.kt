package com.android.oleksandrpriadko.demo.cocktails.search

import android.os.AsyncTask
import com.android.oleksandrpriadko.demo.cocktails.model.CocktailApi
import com.android.oleksandrpriadko.demo.cocktails.model.DrinkDetails
import com.android.oleksandrpriadko.demo.cocktails.model.FoundDrinksResponse
import com.android.oleksandrpriadko.loggalitic.LogPublishService
import retrofit2.Call
import retrofit2.Response
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

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