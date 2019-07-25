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

    private var currentState: State = State.RESULTS_CAROUSEL

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
        view?.hideSoftKeyboard()
        view?.clearSearResults()
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
                    saveAndApplyState(State.LOADING_SEARCH)
                }

                override fun onLoadingDone() {
                    saveAndApplyState(State.RESULTS_SEARCH)
                }

                override fun onLoadingError(throwable: Throwable) {

                }

                override fun noDrinksFound() {
                    saveAndApplyState(State.EMPTY_SEARCH)
                }

                override fun onDrinksFound(foundDrinkDetails: MutableList<DrinkDetails>) {
                    view?.populateSearchResults(foundDrinkDetails)
                    view?.scrollSearchResultsToTop()
                }
            })
        }
    }

    fun searchPopularDrinks() {
        repo.popularDrinks(object : LoadingListener {
            override fun onNoInternet() {

            }

            override fun onLoadingStarted() {
                saveAndApplyState(State.LOADING_CAROUSEL)
            }

            override fun onLoadingDone() {
                saveAndApplyState(State.RESULTS_CAROUSEL)
            }

            override fun onLoadingError(throwable: Throwable) {

            }

            override fun noDrinksFound() {

            }

            override fun onDrinksFound(foundDrinkDetails: MutableList<DrinkDetails>) {
                view?.populateCarouselList(foundDrinkDetails)
                view?.scrollCarouselToStart()
            }
        })
    }

    private fun filterByIngredients(ingredientNames: List<IngredientName>) {
        if (ingredientNames.isNotEmpty()) {
            repo.filterByIngredients(ingredientNamesListToString(ingredientNames), loadingListener = object : LoadingListener {
                override fun onNoInternet() {

                }

                override fun onLoadingStarted() {
                    saveAndApplyState(State.LOADING_SEARCH)
                }

                override fun onLoadingDone() {
                    saveAndApplyState(State.RESULTS_SEARCH)
                }

                override fun onLoadingError(throwable: Throwable) {

                }

                override fun onFilterByIngredient(foundDrinkDetails: MutableList<DrinkDetails>) {
                    view?.populateSearchResults(foundDrinkDetails)
                    view?.scrollSearchResultsToTop()
                }

                override fun noDrinksFound() {
                    saveAndApplyState(State.EMPTY_SEARCH)
                }
            })
        }
    }

    private fun ingredientNamesListToString(ingredientNames: List<IngredientName>): String {
        return ingredientNames.joinToString(separator = ",") {
            it.strIngredient1.replace("\\s".toRegex(), "_")
        }
    }

    fun loadAllIngredients() {
        repo.loadListOfIngredients(loadingListener = object : LoadingListener {
            override fun onNoInternet() {

            }

            override fun onLoadingStarted() {
                saveAndApplyState(State.LOADING_CAROUSEL)
            }

            override fun onLoadingDone() {
                saveAndApplyState(State.RESULTS_CAROUSEL)
            }

            override fun onLoadingError(throwable: Throwable) {

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
            view?.clearSearchInputText()
            view?.hideFoundIngredientMatches()
            view?.scrollSearchInputToEnd()
        }
    }

    fun onIngredientMatchSelected(ingredientName: IngredientName) {
        view?.addChipIngredient(ingredientName)
        view?.clearSearchInputText()
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

    fun onBackPressed() {
        if (currentState == State.RESULTS_CAROUSEL) {
            view?.superOnBackPressed()
        } else {
            saveAndApplyState(State.RESULTS_CAROUSEL)
        }
    }

    private fun saveAndApplyState(newState: State) {
        currentState = newState
        view?.applyState(newState)
    }
}

interface PresenterView : LifecycleOwner {

    fun populateSearchResults(foundDrinkDetails: MutableList<DrinkDetails>)

    fun scrollSearchResultsToTop()

    fun openCocktailDetails(drinkId: String)

    fun addChipIngredient(ingredientName: IngredientName)

    fun requestRemoveChipIngredient(ingredientName: IngredientName)

    fun requestRemoveAllChipsIngredients()

    fun scrollSearchInputToEnd()

    fun getCurrentInputText(): CharSequence?

    fun getSelectedIngredients(): List<String>

    fun clearSearchInputText()

    fun showFoundIngredientMatches(matches: List<String>)

    fun hideFoundIngredientMatches()

    fun updateSearchInputHint()

    fun hideSoftKeyboard()

    fun populateCarouselList(foundDrinkDetails: MutableList<DrinkDetails>)

    fun scrollCarouselToStart()

    fun applyState(state: State)

    fun superOnBackPressed()

    fun clearSearResults()
}

enum class SearchType {
    BY_INGREDIENTS,
    BY_NAME
}

enum class State {
    LOADING_CAROUSEL,
    RESULTS_CAROUSEL,
    LOADING_SEARCH,
    RESULTS_SEARCH,
    EMPTY_SEARCH
}