package com.android.oleksandrpriadko.demo.cocktails.search

import android.content.res.Resources
import androidx.lifecycle.LifecycleOwner
import com.android.oleksandrpriadko.demo.R
import com.android.oleksandrpriadko.demo.cocktails.model.CocktailApi
import com.android.oleksandrpriadko.demo.cocktails.model.DrinkDetails
import com.android.oleksandrpriadko.demo.cocktails.model.IngredientName
import com.android.oleksandrpriadko.mvp.presenter.BasePresenter

class SearchPresenter(baseUrl: String, presenterView: PresenterView) : BasePresenter<PresenterView>(presenterView) {

    private val repo = SearchRepo(presenterView, baseUrl)

    private var currentSearchType = SearchType.BY_INGREDIENTS

    private var currentState: State = State.LOADING

    private var triggerSearchAfterSelection: Boolean = false

    fun triggerSearchAfterSelection(triggerSearchAfterSelection: Boolean) {
        this.triggerSearchAfterSelection = triggerSearchAfterSelection
    }

    fun onSearchTypeChanged(newSearchType: SearchType) {
        currentSearchType = newSearchType
        when (currentSearchType) {
            SearchType.BY_INGREDIENTS -> {
                if (triggerSearchAfterSelection) {
                    triggerSearchAfterSelection = false
                    onSearchTriggered()
                } else {
                    onInputTextChanged(view?.getCurrentInputText())
                }
            }
            SearchType.BY_NAME -> {
                view?.requestRemoveAllSelectedIngredients()
            }
        }
        view?.updateSearchInputHint()
    }

    fun onSearchTriggered() {
        view?.hideKeyboard()
        view?.hideFoundIngredientMatches()
        if (getSelectedIngredientsNames().isNotEmpty()
                || view?.getCurrentInputText()?.isNotEmpty() == true) {
            when (currentSearchType) {
                SearchType.BY_INGREDIENTS -> {
                    filterByIngredients(getSelectedIngredientsNames())
                }
                SearchType.BY_NAME -> {
                    searchDrink(view?.getCurrentInputText())
                }
            }
        }
    }

    private fun getSelectedIngredientsNames(): List<IngredientName> {
        return when (val selectedIngredients = view?.getSelectedIngredients()) {
            null -> listOf()
            else -> selectedIngredients.map { IngredientName().apply { strIngredient1 = it } }
        }
    }

    fun onInputTextChanged(charSequence: CharSequence?) {
        if (charSequence != null) {
            if (charSequence.isNotEmpty()) {
                when (currentSearchType) {
                    SearchType.BY_INGREDIENTS -> findIngredientMatches(charSequence.toString())
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
            repo.searchDrinkByName(name = text.toString(), loadingListener = loadingListener)
        }
    }

    fun searchPopularDrinks() {
        repo.loadPopularDrinks(loadingListener)
    }

    private fun filterByIngredients(ingredients: List<IngredientName>) {
        if (ingredients.isNotEmpty()) {
            repo.filterDrinksByIngredients(
                    CocktailApi.ingredientNamesToString(ingredients), loadingListener = loadingListener)
        }
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

    fun onSelectedIngredientRemoved(selectedAmountAfter: Int) {
        if (selectedAmountAfter > 0) {
            onSearchTriggered()
        } else {
            searchPopularDrinks()
        }
    }

    fun onIngredientMatchSelected(name: String,
                                  addIfAbsentInDatabase: Boolean) {
        this.triggerSearchAfterSelection = triggerSearchAfterSelection
        val ingredientFromDatabase: IngredientName? = repo.findIngredientByName(name)

        if (ingredientFromDatabase != null) {
            if (!isIngredientAlreadyAdded(name)) {
                view?.addSelectedIngredient(ingredientFromDatabase)
                view?.scrollSearchInputToEnd()
            }
            view?.clearSearchInputText()
            view?.hideFoundIngredientMatches()
        } else if (addIfAbsentInDatabase) {
            if (!isIngredientAlreadyAdded(name)) {
                view?.addSelectedIngredient(name)
                view?.scrollSearchInputToEnd()
            }
            view?.clearSearchInputText()
            view?.hideFoundIngredientMatches()
        }
    }

//    fun onIngredientFromCarouselSelected(name: String, addIfAbsentInDatabase: Boolean) {
//        val ingredientFromDatabase: IngredientName? = repo.findIngredientByName(name)
//
//        if (ingredientFromDatabase != null) {
//            if (!isIngredientAlreadyAdded(name)) {
//                view?.addSelectedIngredient(ingredientFromDatabase)
//                view?.scrollSearchInputToEnd()
//            }
//        } else if (addIfAbsentInDatabase) {
//            if (!isIngredientAlreadyAdded(name)) {
//                view?.addSelectedIngredient(name)
//                view?.scrollSearchInputToEnd()
//            }
//        }
//    }

    private fun isIngredientAlreadyAdded(name: String): Boolean {
        view?.getSelectedIngredients()?.forEach { alreadySelected ->
            if (alreadySelected == name) {
                return true
            }
        }
        return false
    }

    fun onDrinkClicked(drinkDetails: DrinkDetails?) {
        drinkDetails?.let {
            view?.showDrinkDetails(drinkDetails.idDrink)
        }
    }

    fun getSearchInputHint(resources: Resources): String {
        return when (currentSearchType) {
            SearchType.BY_INGREDIENTS -> resources.getString(R.string.cocktail_hint_by_ingredients)
            SearchType.BY_NAME -> resources.getString(R.string.cocktail_hint_by_name)
        }
    }

    fun connectionStatusChanged(isConnectedToInternet: Boolean) {
        if (isConnectedToInternet) {
            saveAndApplyState(State.ONLINE)
            if (view?.areSearchResultsEmpty() == true) {
                searchPopularDrinks()
            }
        } else {
            saveAndApplyState(State.OFFLINE)
        }
    }

    fun onBackPressed() {
        view?.superOnBackPressed()
    }

    private fun saveAndApplyState(newState: State) {
        currentState = newState
        view?.applyState(newState)
    }

    private val loadingListener: LoadingListener = object : LoadingListener {

        override fun onDrinksFound(foundDrinkDetails: MutableList<DrinkDetails>) {
            view?.clearSearchResults(redrawItems = false)
            view?.populateSearchResults(foundDrinkDetails)
            view?.scrollToFirstSearchResult()
        }

        override fun onListOfIngredientsLoaded(ingredients: MutableList<IngredientName>) {

        }

        override fun noDrinksFound() {
            saveAndApplyState(State.EMPTY)
        }

        override fun onLoadingStarted() {
            saveAndApplyState(State.LOADING)
        }

        override fun onLoadingDone() {
            saveAndApplyState(State.RESULTS)
        }

        override fun onLoadingError(throwable: Throwable) {
            saveAndApplyState(State.EMPTY)
        }

        override fun onNoInternet() {
            saveAndApplyState(State.OFFLINE)
        }

    }
}

interface PresenterView : LifecycleOwner {

    fun populateSearchResults(foundDrinkDetails: MutableList<DrinkDetails>)

    fun scrollToFirstSearchResult()

    fun areSearchResultsEmpty(): Boolean

    fun showDrinkDetails(drinkId: String)

    fun addSelectedIngredient(ingredient: IngredientName)

    fun addSelectedIngredient(ingredientName: String)

    fun requestRemoveSelectedIngredient(ingredient: IngredientName)

    fun requestRemoveAllSelectedIngredients()

    fun scrollSearchInputToEnd()

    fun getCurrentInputText(): CharSequence?

    fun getSelectedIngredients(): List<String>

    fun clearSearchInputText()

    fun showFoundIngredientMatches(matches: List<String>)

    fun hideFoundIngredientMatches()

    fun updateSearchInputHint()

    fun hideKeyboard()

    fun applyState(state: State)

    fun superOnBackPressed()

    fun clearSearchResults(redrawItems: Boolean)
}

enum class SearchType {
    BY_INGREDIENTS,
    BY_NAME
}

enum class State {
    LOADING,
    RESULTS,
    EMPTY,
    OFFLINE,
    ONLINE
}