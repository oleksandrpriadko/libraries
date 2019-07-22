package com.android.oleksandrpriadko.demo.cocktails.cocktaildetails

import androidx.lifecycle.LifecycleOwner
import com.android.oleksandrpriadko.demo.cocktails.model.CocktailApi
import com.android.oleksandrpriadko.demo.cocktails.model.LookupCocktailDetailsResponse
import com.android.oleksandrpriadko.mvp.repo.ObservableRepo
import com.android.oleksandrpriadko.mvp.repo_extension.RetrofitRepoExtension
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory

class CocktailDetailsRepo(lifecycleOwner: LifecycleOwner,
                          baseUrl: String) : ObservableRepo(lifecycleOwner) {

    private val retrofitRepoExtension: RetrofitRepoExtension = RetrofitRepoExtension(
            baseUrl,
            loggingLevel = HttpLoggingInterceptor.Level.BODY,
            converterFactory = GsonConverterFactory.create())

    fun loadDrinkDetails(drinkId: String, loadingListener: LoadingListener) {
        retrofitRepoExtension.getApi(CocktailApi::class.java)
                .lookupCocktail(drinkId)
                .enqueue(object : Callback<LookupCocktailDetailsResponse> {
                    override fun onResponse(call: Call<LookupCocktailDetailsResponse>, response: Response<LookupCocktailDetailsResponse>) {
                        loadingListener.onLoadingDone()
                        if (response.isSuccessful) {
                            response.body()?.let {
                                loadingListener.onDrinkDetails(it.drinkDetails[0])
                            }
                        }
                    }

                    override fun onFailure(call: Call<LookupCocktailDetailsResponse>, t: Throwable) {
                        loadingListener.onLoadingDone()
                        loadingListener.onLoadingError(t)

                    }

                })
    }

    override fun cleanUp() {

    }


}