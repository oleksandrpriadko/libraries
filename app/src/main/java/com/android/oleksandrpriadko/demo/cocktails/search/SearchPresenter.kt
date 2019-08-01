package com.android.oleksandrpriadko.demo.cocktails.search

import android.content.res.Resources
import androidx.lifecycle.LifecycleOwner
import com.android.oleksandrpriadko.demo.R
import com.android.oleksandrpriadko.demo.cocktails.model.CocktailApi
import com.android.oleksandrpriadko.demo.cocktails.model.wrappers.Drink
import com.android.oleksandrpriadko.demo.cocktails.model.wrappers.Ingredient
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
        if (getSelectedIngredients().isNotEmpty()
                || view?.getCurrentInputText()?.isNotEmpty() == true) {
            when (currentSearchType) {
                SearchType.BY_INGREDIENTS -> {
                    filterByIngredients(getSelectedIngredients())
                }
                SearchType.BY_NAME -> {
                    searchDrink(view?.getCurrentInputText())
                }
            }
        }
    }

    private fun getSelectedIngredients(): List<Ingredient> {
        return view?.getSelectedIngredients() ?: listOf()
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
            repo.searchDrinkByName(name = text.toString(), listener = searchRepoListener)
        }
    }

    fun searchPopularDrinks() {
        repo.loadPopularDrinks(searchRepoListener)
    }

    private fun filterByIngredients(ingredients: List<Ingredient>) {
        val searchQueryList: List<Pair<String, Int>> = CocktailApi.ingredientToSearchQuery(ingredients)
        repo.filterDrinksByIngredients(searchQueryList, listener = searchRepoListener)
    }

    private fun findIngredientMatches(ingredientName: String): Boolean {
        val listOfMatches = repo.findIngredientMatches(ingredientName)
        return if (listOfMatches != null && listOfMatches.isNotEmpty()) {
            view?.showFoundIngredientMatches(listOfMatches)
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

    fun onIngredientMatchSelected(ingredient: Ingredient,
                                  addIfAbsentInDatabase: Boolean) {
        val ingredientFromDatabase: Ingredient? = repo.findIngredientByName(ingredient.name)

        if (ingredientFromDatabase != null) {
            if (!isIngredientAlreadyAdded(ingredient)) {
                view?.addSelectedIngredient(ingredientFromDatabase)
                view?.scrollSearchInputToEnd()
            }
            view?.clearSearchInputText()
            view?.hideFoundIngredientMatches()
        } else if (addIfAbsentInDatabase) {
            if (!isIngredientAlreadyAdded(ingredient)) {
                view?.addSelectedIngredient(ingredient)
                view?.scrollSearchInputToEnd()
            }
            view?.clearSearchInputText()
            view?.hideFoundIngredientMatches()
        }
    }

    private fun isIngredientAlreadyAdded(ingredient: Ingredient): Boolean {
        view?.getSelectedIngredients()?.forEach { alreadySelected ->
            if (alreadySelected.name.equals(ingredient.name, true)) {
                return true
            }
        }
        return false
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        if (currentState == State.LOADING) {
            saveAndApplyState(State.RESULTS)
        }
        runnableOnNewIntent?.run()
        runnableOnNewIntent = null
        runnableOnActivityResult?.run()
        runnableOnActivityResult = null
    }

    fun onDrinkClicked(drink: Drink?) {
        drink?.let { drinkNotNull ->
            val ingredientNamesFromSearch = getSelectedIngredients().map { it.name }
            view?.showDrinkDetails(drinkNotNull, ingredientNamesFromSearch as ArrayList<String>)
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
        view?.let {
            currentState = newState
            it.applyState(newState)
        }
    }

    private val searchRepoListener: SearchRepoListener = object : SearchRepoListener {

        override fun onDrinksFound(drinkList: MutableList<Drink>) {
            view?.clearSearchResults(redrawItems = false)
            view?.populateSearchResults(drinkList)
            view?.scrollToFirstSearchResult()
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

        override fun onLoadingError() {
            saveAndApplyState(State.EMPTY)
        }

        override fun onNoInternet() {
            saveAndApplyState(State.OFFLINE)
        }
    }
}

interface PresenterView : LifecycleOwner {

    fun populateSearchResults(drinks: MutableList<Drink>)

    fun scrollToFirstSearchResult()

    fun areSearchResultsEmpty(): Boolean

    fun showDrinkDetails(drink: Drink, ingredientNamesFromSearch: ArrayList<String>)

    fun addSelectedIngredient(ingredient: Ingredient)

    fun requestRemoveSelectedIngredient(ingredient: Ingredient)

    fun requestRemoveAllSelectedIngredients()

    fun scrollSearchInputToEnd()

    fun getCurrentInputText(): CharSequence?

    fun getSelectedIngredients(): List<Ingredient>

    fun clearSearchInputText()

    fun showFoundIngredientMatches(matches: List<Ingredient>)

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