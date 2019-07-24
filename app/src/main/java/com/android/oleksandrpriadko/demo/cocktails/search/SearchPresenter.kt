package com.android.oleksandrpriadko.demo.cocktails.search

import android.content.res.Resources
import androidx.lifecycle.LifecycleOwner
import com.android.oleksandrpriadko.demo.R
import com.android.oleksandrpriadko.demo.cocktails.model.DrinkDetails
import com.android.oleksandrpriadko.demo.cocktails.model.IngredientName
import com.android.oleksandrpriadko.mvp.presenter.BasePresenter

class SearchPresenter(baseUrl: String, presenterView: PresenterView) : BasePresenter<PresenterView>(presenterView) {

    private val repo = SearchRepo(presenterView, baseUrl)

    private var currentSearchType = SearchType.BY_INGREDIENTS

    fun onSearchTypeChanged(newSearchType: SearchType) {
        currentSearchType = newSearchType
        when (currentSearchType) {
            SearchType.BY_INGREDIENTS -> {
                onInputTextChanged(view?.getCurrentInputText())
            }
            SearchType.BY_NAME -> {
                view?.requestRemoveAllChipsIngredients()
            }
        }
        view?.updateSearchInputHint()
    }

    fun onSearchTriggered() {
        when (currentSearchType) {
            SearchType.BY_INGREDIENTS -> {
                filterByIngredients(getSelectedIngredientsNames())
            }
            SearchType.BY_NAME -> {
                searchDrink(view?.getCurrentInputText())
            }
        }
    }

    private fun getSelectedIngredientsNames(): List<IngredientName> {
        val selectedIngredients = view?.getSelectedIngredients()
        selectedIngredients?.let {
            return it.map { IngredientName().apply { strIngredient1 = it } }
        }

        return listOf()
    }

    fun onInputTextChanged(charSequence: CharSequence?) {
        if (charSequence != null) {
            if (charSequence.isNotEmpty()) {
                when (currentSearchType) {
                    SearchType.BY_INGREDIENTS -> {
                        findIngredientMatches(charSequence.toString())
                    }
                    else -> {
                    }
                }
            } else {
                view?.hideFoundIngredientMatches()
            }
        }
    }

    private fun searchDrink(text: CharSequence?) {
        if (text != null) {
            repo.searchDrink(name = text.toString(), loadingListener = object : LoadingListener {
                override fun onNoInternet() {

                }

                override fun onLoadingStarted() {
                    view?.showLoadingLayout(true)
                }

                override fun onLoadingDone() {
                    view?.showLoadingLayout(false)
                }

                override fun onLoadingError(throwable: Throwable) {
                    view?.showLoadingLayout(false)
                }

                override fun noDrinksFound() {

                }

                override fun onDrinksFound(foundDrinkDetails: MutableList<DrinkDetails>) {
                    view?.populateResults(foundDrinkDetails)
                }
            })
        }
    }

    private fun filterByIngredients(ingredientNames: List<IngredientName>) {
        if (ingredientNames.isNotEmpty()) {
            repo.filterByIngredient(ingredientNames = ingredientNames, loadingListener = object : LoadingListener {
                override fun onNoInternet() {

                }

                override fun onLoadingStarted() {
                    view?.showLoadingLayout(true)
                }

                override fun onLoadingDone() {
                    view?.showLoadingLayout(false)
                }

                override fun onLoadingError(throwable: Throwable) {
                    view?.showLoadingLayout(false)
                }

                override fun onFilterByIngredient(foundDrinkDetails: MutableList<DrinkDetails>) {
                    view?.populateResults(foundDrinkDetails)
                }

                override fun noDrinksFound() {

                }
            })
        }
    }

    fun loadAllIngredients() {
        repo.loadListOfIngredients(loadingListener = object : LoadingListener {
            override fun onNoInternet() {

            }

            override fun onLoadingStarted() {
                view?.showLoadingLayout(true)
            }

            override fun onLoadingDone() {
                view?.showLoadingLayout(false)
            }

            override fun onLoadingError(throwable: Throwable) {
                view?.showLoadingLayout(false)
            }

            override fun onListOfIngredientsLoaded(ingredientNames: MutableList<IngredientName>) {

            }
        })
    }

    private fun findIngredientMatches(name: String): Boolean {
        val listOfMatches = repo.findIngredientMatches(name)
        return if (listOfMatches != null && listOfMatches.isNotEmpty()) {
            view?.showFoundIngredientMatches(listOfMatches.map { it.strIngredient1 })
            true
        } else {
            view?.hideFoundIngredientMatches()
            false
        }
    }

    fun onIngredientMatchSelected(name: String) {
        repo.findIngredient(name)?.let {
            view?.addChipIngredient(it)
            view?.clearInputText()
            view?.hideFoundIngredientMatches()
        }
    }

    fun onIngredientMatchSelected(ingredientName: IngredientName) {
        view?.addChipIngredient(ingredientName)
        view?.clearInputText()
        view?.hideFoundIngredientMatches()
    }

    fun onDrinkClicked(drinkDetails: DrinkDetails?) {
        drinkDetails?.let {
            view?.openCocktailDetails(drinkDetails.idDrink)
        }
    }

    fun getSearchInputHint(resources: Resources): String {
        return when (currentSearchType) {
            SearchType.BY_INGREDIENTS -> resources.getString(R.string.cocktail_hint_by_ingredients)
            SearchType.BY_NAME -> resources.getString(R.string.cocktail_hint_by_name)
        }
    }
}

interface PresenterView : LifecycleOwner {

    fun showLoadingLayout(show: Boolean)

    fun showResults(show: Boolean)

    fun populateResults(foundDrinkDetails: MutableList<DrinkDetails>)

    fun openCocktailDetails(drinkId: String)

    fun addChipIngredient(ingredientName: IngredientName)

    fun requestRemoveChipIngredient(ingredientName: IngredientName)

    fun requestRemoveAllChipsIngredients()

    fun scrollSearchInputToEnd()

    fun getCurrentInputText(): CharSequence?

    fun getSelectedIngredients(): List<String>

    fun clearInputText()

    fun showFoundIngredientMatches(matches: List<String>)

    fun hideFoundIngredientMatches()

    fun updateSearchInputHint()
}

enum class SearchType {
    BY_INGREDIENTS,
    BY_NAME
}