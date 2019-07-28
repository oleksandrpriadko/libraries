package com.android.oleksandrpriadko.demo.cocktails.drinkdetails

import androidx.lifecycle.LifecycleOwner
import com.android.oleksandrpriadko.demo.cocktails.model.CocktailApi
import com.android.oleksandrpriadko.demo.cocktails.model.FoundDrinksResponse
import com.android.oleksandrpriadko.demo.cocktails.model.FoundIngredientsResponse
import com.android.oleksandrpriadko.mvp.repo.ObservableRepo
import com.android.oleksandrpriadko.mvp.repo_extension.RetrofitRepoExtension
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory

class DrinkDetailsRepo(lifecycleOwner: LifecycleOwner,
                       baseUrl: String) : ObservableRepo(lifecycleOwner) {

    private val retrofitRepoExtension: RetrofitRepoExtension = RetrofitRepoExtension(
            baseUrl,
            loggingLevel = HttpLoggingInterceptor.Level.BODY,
            converterFactory = GsonConverterFactory.create())

    fun loadDrinkDetails(drinkId: String, loadingListener: LoadingListener) {
        retrofitRepoExtension.getApi(CocktailApi::class.java)
                .lookupCocktailById(drinkId)
                .enqueue(object : Callback<FoundDrinksResponse> {
                    override fun onResponse(call: Call<FoundDrinksResponse>, response: Response<FoundDrinksResponse>) {
                        loadingListener.onLoadingDone()
                        if (response.isSuccessful) {
                            response.body()?.let {
                                val localDrinkDetails = it.drinkDetails
                                if (localDrinkDetails != null) {
                                    loadingListener.onDrinkDetailsLoaded(localDrinkDetails[0])
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<FoundDrinksResponse>, t: Throwable) {
                        loadingListener.onLoadingDone()
                        loadingListener.onLoadingError(t)

                    }

                })
    }

    fun loadIngredientDetails(ingredientName: String, loadingListener: LoadingListener) {
        retrofitRepoExtension.getApi(CocktailApi::class.java)
                .searchIngredientByName(ingredientName)
                .enqueue(object : Callback<FoundIngredientsResponse> {
                    override fun onResponse(call: Call<FoundIngredientsResponse>,
                                            response: Response<FoundIngredientsResponse>) {
                        onIngredientDetailsLoaded(response, loadingListener)
                    }

                    override fun onFailure(call: Call<FoundIngredientsResponse>, t: Throwable) {
                        loadingListener.onLoadingDone()
                        loadingListener.onLoadingError(t)

                    }

                })
    }

    private fun onIngredientDetailsLoaded(response: Response<FoundIngredientsResponse>,
                                          loadingListener: LoadingListener) {
        loadingListener.onLoadingDone()
        if (response.isSuccessful) {
            response.body()?.let {
                val localIngredients = it.ingredientList
                if (localIngredients != null && localIngredients.isNotEmpty()) {
                    val ingredient = localIngredients[0]
                    loadingListener.onIngredientDetailsLoaded(ingredient)
                }
            }
        }
    }

    override fun cleanUp() {

    }


}