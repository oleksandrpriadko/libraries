package com.android.oleksandrpriadko.demo.cocktails.drinkdetails

import androidx.lifecycle.LifecycleOwner
import com.android.oleksandrpriadko.demo.cocktails.model.wrappers.Drink
import com.android.oleksandrpriadko.demo.cocktails.model.wrappers.Ingredient
import com.android.oleksandrpriadko.mvp.presenter.BasePresenter

class DrinkDetailsPresenter(presenterView: PresenterView,
                            baseUrl: String) : BasePresenter<PresenterView>(presenterView) {

    private var addIngredientToSearch: Boolean = false
    private val repo = DrinkDetailsRepo(presenterView, baseUrl)

    fun loadDrinkDetails(drinkId: String, ingredientsFromSearch: List<String>) {
        repo.loadDrink(drinkId, object : DrinkDetailsRepoListener {
            override fun onNoInternet() {
                view?.showOfflineLayout(show = true)
            }

            override fun onLoadingStarted() {
                view?.showLoadingLayout(show = true)
            }

            override fun onLoadingDone() {
                view?.showLoadingLayout(show = false)
            }

            override fun onLoadingError() {
                view?.showLoadingLayout(show = false)
            }

            override fun onDrinkLoaded(drink: Drink) {
                view?.populateDrinkDetails(drink, ingredientsFromSearch)
            }
        })
    }

    fun onIngredientItemClicked(drink: Drink, ingredient: Ingredient) {
        view?.showIngredientOverlay(ingredient)
        view?.clearImageInOverlay()
        view?.clearNameInOverlay()
        requestLoadIngredientDetails(drink, ingredient)
    }

    private fun requestLoadIngredientDetails(drink: Drink, ingredient: Ingredient) {
        repo.loadIngredient(drink, ingredient, object : DrinkDetailsRepoListener {
            override fun onNoInternet() {
                view?.showOfflineLayout(show = true)
            }

            override fun onLoadingStarted() {
                view?.showOverlayLoadingIngredient(true)
            }

            override fun onLoadingDone() {
            }

            override fun onLoadingError() {
            }

            override fun onIngredientLoaded(ingredient: Ingredient) {
                view?.populateIngredientName(ingredient.name)
                view?.populateIngredientDescription(ingredient.description)
                view?.loadIngredientImage(ingredient.imageUrl)
            }
        })
    }

    fun ingredientImageLoaded() {
        view?.showOverlayLoadingIngredient(false)
    }

    fun onIngredientOverlayHidden(ingredientAttachedToOverlay: Ingredient) {
        if (addIngredientToSearch) {
            view?.openSearchWithIngredient(ingredientAttachedToOverlay)
        }
    }

    fun onAddIngredientToSearch() {
        addIngredientToSearch = true
        view?.hideIngredientOverlay()
    }

    fun onConnectionStatusChanged(connectedToInternet: Boolean) {
        view?.showOfflineLayout(!connectedToInternet)
    }

    fun onGoBackClicked() {
        view?.requestCloseScreen()
    }
}

interface PresenterView : LifecycleOwner {

    fun populateDrinkDetails(drink: Drink, ingredientsFromSearch: List<String>)

    fun showOverlayLoadingIngredient(show: Boolean)

    fun loadIngredientImage(imageUrl: String)

    fun populateIngredientName(name: String)

    fun populateIngredientDescription(description: String)

    fun showIngredientOverlay(selectedIngredient: Ingredient)

    fun hideIngredientOverlay()

    fun openSearchWithIngredient(ingredient: Ingredient)

    fun showLoadingLayout(show: Boolean)

    fun clearImageInOverlay()

    fun clearNameInOverlay()

    fun showOfflineLayout(show: Boolean)

    fun requestCloseScreen()

}