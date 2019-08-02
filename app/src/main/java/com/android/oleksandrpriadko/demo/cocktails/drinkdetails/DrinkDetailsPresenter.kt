package com.android.oleksandrpriadko.demo.cocktails.drinkdetails

import androidx.lifecycle.LifecycleOwner
import com.android.oleksandrpriadko.demo.cocktails.model.wrappers.Drink
import com.android.oleksandrpriadko.demo.cocktails.model.wrappers.Ingredient
import com.android.oleksandrpriadko.demo.cocktails.model.wrappers.MeasuredIngredient
import com.android.oleksandrpriadko.mvp.presenter.BasePresenter

class DrinkDetailsPresenter(presenterView: PresenterView,
                            baseUrl: String) : BasePresenter<PresenterView>(presenterView) {

    private var addIngredientToSearch: Boolean = false
    private val repo = DrinkDetailsRepo(presenterView, baseUrl)

    fun loadDrinkDetails(drinkId: String?, ingredientsFromSearch: List<String>?) {
        if (drinkId == null) {
            logState("drink id is null")
            return
        }
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

    fun onIngredientItemClicked(measuredIngredient: MeasuredIngredient) {
        view?.showIngredientOverlay(measuredIngredient)
        view?.clearImageInOverlay()
        view?.clearNameInOverlay()
        requestLoadIngredientDetails(measuredIngredient)
    }

    private fun requestLoadIngredientDetails(measuredIngredient: MeasuredIngredient) {
        repo.loadIngredient(measuredIngredient, object : DrinkDetailsRepoListener {
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

    fun onIngredientOverlayHidden(measureIngredientAttachedToOverlay: MeasuredIngredient) {
        if (addIngredientToSearch) {
            view?.openSearchWithIngredient(measureIngredientAttachedToOverlay)
        }
    }

    fun onAddIngredientToSearch() {
        addIngredientToSearch = true
        view?.hideIngredientOverlay()
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        view?.requestCheckDrinkInIntent()
        runnableOnNewIntent?.run()
        runnableOnNewIntent = null
        runnableOnActivityResult?.run()
        runnableOnActivityResult = null
    }

    fun onConnectionStatusChanged(connectedToInternet: Boolean) {
        view?.showOfflineLayout(!connectedToInternet)
    }

    fun onGoBackClicked() {
        view?.requestCloseScreen()
    }
}

interface PresenterView : LifecycleOwner {

    fun populateDrinkDetails(drink: Drink, ingredientsFromSearch: List<String>?)

    fun showOverlayLoadingIngredient(show: Boolean)

    fun loadIngredientImage(imageUrl: String)

    fun populateIngredientName(name: String)

    fun populateIngredientDescription(description: String)

    fun showIngredientOverlay(selectedMeasuredIngredient: MeasuredIngredient)

    fun hideIngredientOverlay()

    fun openSearchWithIngredient(measuredIngredient: MeasuredIngredient)

    fun showLoadingLayout(show: Boolean)

    fun clearImageInOverlay()

    fun clearNameInOverlay()

    fun showOfflineLayout(show: Boolean)

    fun requestCloseScreen()

    fun requestCheckDrinkInIntent()

}