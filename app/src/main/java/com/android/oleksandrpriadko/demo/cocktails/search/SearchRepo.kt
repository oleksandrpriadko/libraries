package com.android.oleksandrpriadko.demo.cocktails.search

import androidx.lifecycle.LifecycleOwner
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
            baseUrl, converterFactory = GsonConverterFactory.create())

    fun searchCocktail(name: String, loadingListener: LoadingListener) {
        loadingListener.onLoadingStarted()

        retrofitRepoExtension
                .getApi(CocktailApi::class.java)
                .searchCocktail(name)
                .enqueue(object : Callback<Any> {
                    override fun onResponse(call: Call<Any>, response: Response<Any>) {
                        loadingListener.onLoadingDone()
                    }

                    override fun onFailure(call: Call<Any>, t: Throwable) {
                        loadingListener.onLoadingDone()
                        loadingListener.onLoadingError(t)

                    }

                })
    }

    fun filterByIngredient(name: String, loadingListener: LoadingListener) {
        loadingListener.onLoadingStarted()

        retrofitRepoExtension
                .getApi(CocktailApi::class.java)
                .filterByIngredient(name)
                .enqueue(object : Callback<FilterByIngredientResponse> {
                    override fun onResponse(call: Call<FilterByIngredientResponse>, response: Response<FilterByIngredientResponse>) {
                        loadingListener.onLoadingDone()
                        if (response.isSuccessful) {
                            loadingListener.onFilterByIngredient(
                                    response.body()?.drinks ?: mutableListOf<Drink>())
                        }
                    }

                    override fun onFailure(call: Call<FilterByIngredientResponse>, t: Throwable) {
                        loadingListener.onLoadingDone()
                        loadingListener.onLoadingError(t)

                    }

                })
    }

    fun listOfIngredients(loadingListener: LoadingListener) {
        loadingListener.onLoadingStarted()

        retrofitRepoExtension
                .getApi(CocktailApi::class.java)
                .listOfIngredients()
                .enqueue(object : Callback<ListOfIngredientsResponse> {
                    override fun onResponse(call: Call<ListOfIngredientsResponse>, response: Response<ListOfIngredientsResponse>) {
                        loadingListener.onLoadingDone()
                        if (response.isSuccessful) {
                            loadingListener.onListOfIngredients(
                                    response.body()?.ingredientNameList ?: mutableListOf<IngredientName>())
                        }
                    }

                    override fun onFailure(call: Call<ListOfIngredientsResponse>, t: Throwable) {
                        loadingListener.onLoadingDone()
                        loadingListener.onLoadingError(t)

                    }

                })
    }

    override fun cleanUp() {}
}