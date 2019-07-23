package com.android.oleksandrpriadko.demo.cocktails.search

import androidx.lifecycle.LifecycleOwner
import com.android.oleksandrpriadko.demo.cocktails.model.Drink
import com.android.oleksandrpriadko.demo.cocktails.model.IngredientName
import com.android.oleksandrpriadko.mvp.presenter.BasePresenter

class SearchPresenter(baseUrl: String, presenterView: PresenterView) : BasePresenter<PresenterView>(presenterView) {

    private val repo = SearchRepo(presenterView, baseUrl)

    fun searchCocktail(text: String) {
        view?.expandResultsLayout(true)

        repo.searchCocktail(name = text, loadingListener = object : LoadingListener {
            override fun onLoadingStarted() {
                view?.showLoadingLayout(true)
            }

            override fun onLoadingDone() {
                view?.showLoadingLayout(false)
            }

            override fun onLoadingError(throwable: Throwable) {
                view?.showLoadingLayout(false)
            }
        })
    }

    fun filterByIngredient(text: String) {
        repo.filterByIngredient(name = text, loadingListener = object : LoadingListener {
            override fun onLoadingStarted() {
                view?.showLoadingLayout(true)
            }

            override fun onLoadingDone() {
                view?.showLoadingLayout(false)
            }

            override fun onLoadingError(throwable: Throwable) {
                view?.showLoadingLayout(false)
            }

            override fun onFilterByIngredient(foundDrinks: MutableList<Drink>) {
                view?.showFoundCocktails(foundDrinks)
            }
        })
    }

    fun loadListOfIngredients() {
        repo.listOfIngredients(loadingListener = object : LoadingListener {
            override fun onLoadingStarted() {
                view?.showLoadingLayout(true)
            }

            override fun onLoadingDone() {
                view?.showLoadingLayout(false)
            }

            override fun onLoadingError(throwable: Throwable) {
                view?.showLoadingLayout(false)
            }

            override fun onListOfIngredients(ingredientNames: MutableList<IngredientName>) {

            }
        })
    }

    fun onDrinkClicked(drink: Drink?) {
        drink?.let {
            view?.openCocktailDetails(drink.idDrink)
        }
    }
}

interface PresenterView : LifecycleOwner {

    fun showFoundCocktails(foundDrinks: MutableList<Drink>)

    fun openCocktailDetails(drinkId: String)

    fun expandResultsLayout(expand: Boolean)

    fun showLoadingLayout(show: Boolean)

}