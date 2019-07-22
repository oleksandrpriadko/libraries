package com.android.oleksandrpriadko.demo.cocktails.cocktaildetails

import androidx.lifecycle.LifecycleOwner
import com.android.oleksandrpriadko.demo.cocktails.model.DrinkDetails
import com.android.oleksandrpriadko.mvp.presenter.BasePresenter

class CocktailDetailsPresenter(presenterView: PresenterView,
                               baseUrl: String) : BasePresenter<PresenterView>(presenterView) {

    private val repo = CocktailDetailsRepo(presenterView, baseUrl)

    fun loadDrinkDetails(drinkId: String) {
        repo.loadDrinkDetails(drinkId, object : LoadingListener {
            override fun onLoadingStarted() {

            }

            override fun onLoadingDone() {

            }

            override fun onLoadingError(throwable: Throwable) {

            }

            override fun onDrinkDetails(drinkDetails: DrinkDetails) {
                view?.onDrinkDetailsLoaded(drinkDetails)
            }
        })
    }

}

interface PresenterView : LifecycleOwner {

    fun onDrinkDetailsLoaded(drinkDetails: DrinkDetails)

}