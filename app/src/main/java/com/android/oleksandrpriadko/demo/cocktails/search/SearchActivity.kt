package com.android.oleksandrpriadko.demo.cocktails.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.FOCUS_RIGHT
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ListPopupWindow
import androidx.appcompat.widget.ListPopupWindow.INPUT_METHOD_NEEDED
import androidx.appcompat.widget.ListPopupWindow.POSITION_PROMPT_BELOW
import com.android.oleksandrpriadko.demo.R
import com.android.oleksandrpriadko.demo.cocktails.cocktaildetails.CocktailDetailsActivity
import com.android.oleksandrpriadko.demo.cocktails.model.Cocktail
import com.android.oleksandrpriadko.demo.cocktails.model.DrinkDetails
import com.android.oleksandrpriadko.demo.cocktails.model.IngredientName
import com.android.oleksandrpriadko.extension.inflateOn
import com.android.oleksandrpriadko.recycler_adapter.BaseItemListenerAdapter
import com.android.oleksandrpriadko.ui.attachedtabs.OnItemSelectedListener
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.cocktail_activity_search.*
import kotlinx.android.synthetic.main.cocktail_layout_input.*
import kotlinx.android.synthetic.main.cocktail_layout_search_tabs.*

class SearchActivity : AppCompatActivity(), PresenterView {

    private var presenter: SearchPresenter? = null

    private var popupMenuIngredientMatches: ListPopupWindow? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = SearchPresenter(getString(R.string.cocktail_base_url), this)

        setContentView(R.layout.cocktail_activity_search)

        searchTabs.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(selectedView: View, indexOfSelected: Int) {
                val newSearchType =
                        when (indexOfSelected) {
                            0 -> SearchType.BY_INGREDIENTS
                            1 -> SearchType.BY_NAME
                            else -> SearchType.BY_NAME
                        }
                presenter?.onSearchTypeChanged(newSearchType)
            }

        }

        initDrinksList()

        prepareSearchInput()

        presenter?.loadAllIngredients()
    }

    private fun initDrinksList() {
        val adapter = CocktailSearchScreenAdapter()
        val items = mutableListOf(
                Cocktail("first item in a row is here"),
                Cocktail("second item in a row is here"),
                Cocktail("third item in a row is here"),
                Cocktail("fourth item in a row is here"),
                Cocktail("fifth item in a row is here"),
                Cocktail("sixth item in a row is here"),
                Cocktail("seventh item in a row is here"),
                Cocktail("eighths item in a row is here"),
                Cocktail("ninth item in a row is here"),
                Cocktail("tenth item in a row is here"),
                Cocktail("eleventh item in a row is here"))
        adapter.setData(items)
        itemsCarouselRecyclerView.adapter = adapter
    }

    private fun prepareSearchInput() {
        searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                presenter?.onSearchTriggered()
            }
            false
        }

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                presenter?.onInputTextChanged(s)
            }
        })

        updateSearchInputHint()
    }

    override fun getCurrentInputText(): CharSequence? = searchInput.text

    override fun clearInputText() {
        searchInput.setText("")
    }

    override fun getSelectedIngredients(): List<String> {
        val selectedIngredients = mutableListOf<String>()
        for (i in 0 until ingredientsChipGroup.childCount) {
            val chip: Chip = ingredientsChipGroup.getChildAt(i) as Chip
            selectedIngredients.add(chip.text.toString())
        }
        return selectedIngredients
    }

    override fun showResults(show: Boolean) {
        return if (show) searchWrapLayout.transitionToEnd()
        else searchWrapLayout.transitionToStart()
    }

    override fun populateResults(foundDrinkDetails: MutableList<DrinkDetails>) {
        val foundDrinksAdapter = FoundDrinkDetailsAdapter()
        foundDrinksAdapter.setData(foundDrinkDetails)
        foundDrinksAdapter.itemListener = object : BaseItemListenerAdapter<DrinkDetails>() {
            override fun itemClicked(position: Int, item: DrinkDetails) {
                presenter?.onDrinkClicked(item)
            }
        }
        searchResultsRecView.adapter = foundDrinksAdapter
    }

    override fun openCocktailDetails(drinkId: String) {
        CocktailDetailsActivity.loadCocktailById(this, drinkId)
    }

    override fun showLoadingLayout(show: Boolean) {

    }

    override fun showFoundIngredientMatches(matches: List<String>) {
        if (matches.isNotEmpty()) {
            if (popupMenuIngredientMatches == null) {
                popupMenuIngredientMatches = ListPopupWindow(this)
            }

            popupMenuIngredientMatches?.let {
                it.anchorView = inputScrollView
                it.inputMethodMode = INPUT_METHOD_NEEDED
                it.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, matches).apply { notifyDataSetChanged() })
                it.setOnItemClickListener { adapterView, _, position, _ ->
                    presenter?.onIngredientMatchSelected(adapterView.adapter.getItem(position) as String)
                }
                it.promptPosition = POSITION_PROMPT_BELOW
                it.show()
            }
        }
    }

    override fun hideFoundIngredientMatches() {
        popupMenuIngredientMatches?.dismiss()
    }

    override fun addChipIngredient(ingredientName: IngredientName) {
        val chip = ingredientsChipGroup.inflateOn<Chip>(R.layout.cocktail_chip_search)
        chip.text = ingredientName.strIngredient1
        chip.setOnCloseIconClickListener {
            ingredientsChipGroup.removeView(chip)
        }
        ingredientsChipGroup.addView(chip)
    }

    override fun requestRemoveChipIngredient(ingredientName: IngredientName) {
        for (i in 0 until ingredientsChipGroup.childCount) {
            val chip: Chip = ingredientsChipGroup.getChildAt(i) as Chip
            if (chip.text == ingredientName.strIngredient1) {
                ingredientsChipGroup.removeView(chip)
            }
        }
    }

    override fun requestRemoveAllChipsIngredients() {
        ingredientsChipGroup.removeAllViews()
    }

    override fun scrollSearchInputToEnd() {
        inputScrollView.fullScroll(FOCUS_RIGHT)
    }

    override fun updateSearchInputHint() {
        searchInput.hint = presenter?.getSearchInputHint(resources)
    }

    override fun onBackPressed() {
        if (searchWrapLayout.progress > 0f) {
            searchWrapLayout.transitionToStart()
        } else {
            super.onBackPressed()
        }
    }
}