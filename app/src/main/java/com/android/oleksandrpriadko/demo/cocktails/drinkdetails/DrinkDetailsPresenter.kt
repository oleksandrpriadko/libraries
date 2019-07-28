package com.android.oleksandrpriadko.demo.cocktails.drinkdetails

import androidx.lifecycle.LifecycleOwner
import com.android.oleksandrpriadko.demo.cocktails.model.CocktailApi
import com.android.oleksandrpriadko.demo.cocktails.model.DrinkDetails
import com.android.oleksandrpriadko.demo.cocktails.model.ImageSize
import com.android.oleksandrpriadko.demo.cocktails.model.IngredientDetails
import com.android.oleksandrpriadko.mvp.presenter.BasePresenter

class DrinkDetailsPresenter(presenterView: PresenterView,
                            baseUrl: String) : BasePresenter<PresenterView>(presenterView) {

    private val repo = DrinkDetailsRepo(presenterView, baseUrl)

    private var shownIngredientName: String? = null
    private var addIngredientToSearch: Boolean = false

    fun loadDrinkDetails(drinkId: String) {
        repo.loadDrinkDetails(drinkId, object : LoadingListener {
            override fun onNoInternet() {
                view?.showLoadingLayout(show = false)
            }

            override fun onLoadingStarted() {
                view?.showLoadingLayout(show = true)
            }

            override fun onLoadingDone() {
                view?.showLoadingLayout(show = false)
            }

            override fun onLoadingError(throwable: Throwable) {
                view?.showLoadingLayout(show = false)
            }

            override fun onDrinkDetailsLoaded(drinkDetails: DrinkDetails) {
                view?.populateDrinkDetails(drinkDetails)
            }
        })
    }

    private fun requestLoadIngredientDetails() {
        shownIngredientName?.let {
            repo.loadIngredientDetails(it, object : LoadingListener {
                override fun onLoadingStarted() {

                }

                override fun onLoadingDone() {

                }

                override fun onLoadingError(throwable: Throwable) {

                }

                override fun onNoInternet() {

                }

                override fun onIngredientDetailsLoaded(ingredient: IngredientDetails) {
                    view?.setIngredientName(ingredient.strIngredient)
                    view?.setIngredientDescription(ingredient.strDescription ?: "")
                    view?.loadIngredientImage(CocktailApi.createIngredientImageUrl(
                            ingredient.strIngredient, ImageSize.NORMAL))
                }
            })
        }
    }

    fun onIngredientItemClicked(ingredientName: String?) {
        shownIngredientName = ingredientName
        shownIngredientName?.let {
            view?.showIngredientOverlay()
            view?.clearImageInOverlay()
            requestLoadIngredientDetails()
        }
    }

    fun onIngredientOverlayHidden() {
        shownIngredientName?.let {
            if (addIngredientToSearch) {
                view?.openSearchWithIngredient(it)
            }
        }
    }

    fun onAddIngredientToSearch() {
        shownIngredientName?.let {
            addIngredientToSearch = true
            view?.hideIngredientOverlay()
        }
    }
}

interface PresenterView : LifecycleOwner {

    fun populateDrinkDetails(drinkDetails: DrinkDetails)

    fun loadIngredientImage(imageUrl: String)

    fun setIngredientName(name: String)

    fun setIngredientDescription(description: String)

    fun showIngredientOverlay()

    fun hideIngredientOverlay()

    fun openSearchWithIngredient(shownIngredientName: String)

    fun showLoadingLayout(show: Boolean)

    fun clearImageInOverlay()

}