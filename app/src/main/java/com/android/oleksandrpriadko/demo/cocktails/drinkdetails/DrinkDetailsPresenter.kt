package com.android.oleksandrpriadko.demo.cocktails.drinkdetails

import androidx.lifecycle.LifecycleOwner
import com.android.oleksandrpriadko.demo.cocktails.model.CocktailApi
import com.android.oleksandrpriadko.demo.cocktails.model.DrinkDetails
import com.android.oleksandrpriadko.mvp.presenter.BasePresenter

class DrinkDetailsPresenter(presenterView: PresenterView,
                            baseUrl: String) : BasePresenter<PresenterView>(presenterView) {

    private val repo = DrinkDetailsRepo(presenterView, baseUrl)

    private var shownIngredientName: String? = null

    fun loadDrinkDetails(drinkId: String) {
        repo.loadDrinkDetails(drinkId, object : LoadingListener {
            override fun onNoInternet() {

            }

            override fun onLoadingStarted() {

            }

            override fun onLoadingDone() {

            }

            override fun onLoadingError(throwable: Throwable) {

            }

            override fun onDrinkDetailsLoaded(drinkDetails: DrinkDetails) {
                view?.populateDrinkDetails(drinkDetails)
            }
        })
    }

    fun onIngredientChipClicked(ingredientName: String?) {
        shownIngredientName = ingredientName
        shownIngredientName?.let {
            view?.showIngredientPopup(true)
            view?.loadIngredientImage(CocktailApi.createIngredientImageUrl(it))
        }
    }

    fun onIngredientLayoutClicked() {
        shownIngredientName?.let {
            view?.openSearchWithIngredient(it)
        }
    }
}

interface PresenterView : LifecycleOwner {

    fun populateDrinkDetails(drinkDetails: DrinkDetails)

    fun showIngredientPopup(show: Boolean)

    fun loadIngredientImage(imageUrl: String)

    fun openSearchWithIngredient(shownIngredientName: String)

}