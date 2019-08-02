package com.android.oleksandrpriadko.demo.cocktails.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ListPopupWindow
import androidx.appcompat.widget.ListPopupWindow.INPUT_METHOD_NEEDED
import androidx.appcompat.widget.ListPopupWindow.POSITION_PROMPT_BELOW
import com.android.oleksandrpriadko.demo.R
import com.android.oleksandrpriadko.demo.cocktails.drinkdetails.DrinkDetailsActivity
import com.android.oleksandrpriadko.demo.cocktails.model.BundleConst
import com.android.oleksandrpriadko.demo.cocktails.model.wrappers.Drink
import com.android.oleksandrpriadko.demo.cocktails.model.wrappers.Ingredient
import com.android.oleksandrpriadko.demo.cocktails.model.wrappers.MeasuredIngredient
import com.android.oleksandrpriadko.demo.main.App
import com.android.oleksandrpriadko.extension.hide
import com.android.oleksandrpriadko.extension.inflateOn
import com.android.oleksandrpriadko.extension.show
import com.android.oleksandrpriadko.retrofit.ConnectionStatusSubscriber
import com.android.oleksandrpriadko.ui.attachedtabs.OnItemSelectedListener
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.cocktail_activity_search.*

class SearchActivity : AppCompatActivity(), PresenterView, ConnectionStatusSubscriber {

    private var presenter: SearchPresenter? = null

    private var popupListIngredientMatches: ListPopupWindow? = null

    private val adapterItems = FoundDrinksAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = SearchPresenter(getString(R.string.cocktail_base_url), this)

        setContentView(R.layout.cocktail_activity_search)

        prepareSearch()
        initItemsRecView()

        presenter?.saveOnPendingActionRunnable(Runnable {
            App.connectionStatusReceiver.subscribe(this)
            presenter?.searchPopularDrinks()
        })
    }

    private fun initItemsRecView() {
        adapterItems.itemListener = object : FoundDrinksItemListener {
            override fun isEmpty(isEmpty: Boolean) {}

            override fun itemClicked(position: Int, item: Drink) {
                presenter?.onDrinkClicked(item)
            }
        }
        itemsRecyclerView.adapter = adapterItems
    }

    private fun prepareSearch() {
        searchTabs.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(selectedView: View, indexOfSelected: Int) {
                val newSearchType =
                        when (indexOfSelected) {
                            BY_INGREDIENTS -> SearchType.BY_INGREDIENTS
                            BY_NAME -> SearchType.BY_NAME
                            else -> SearchType.BY_NAME
                        }
                presenter?.onSearchTypeChanged(newSearchType)
            }
        }

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

        inputLayout.setOnClickListener {
            searchInput.requestFocus()
            showKeyboard(searchInput)
        }
    }

    override fun getCurrentInputText(): CharSequence? = searchInput.text

    override fun clearSearchInputText() {
        searchInput.setText("")
    }

    override fun getSelectedIngredients(withInput: Boolean): List<Ingredient> {
        val selectedIngredients: MutableList<Ingredient> = mutableListOf()
        // collect ingredients from all chips
        for (i in 0 until ingredientsChipGroup.childCount) {
            val chip: Chip = ingredientsChipGroup.getChildAt(i) as Chip
            val chipTag: Ingredient? = chip.tag as? Ingredient
            if (chipTag != null) {
                selectedIngredients.add(chipTag)
            }
        }
        // request add current input text as ingredient
        if (withInput) {
            getCurrentInputText()?.let {
                if (it.isNotEmpty()) {
                    selectedIngredients.add(Ingredient(it.toString()))
                }
            }
        }
        return selectedIngredients
    }

    override fun populateSearchResults(drinks: MutableList<Drink>) {
        adapterItems.setData(drinks)
    }

    override fun areSearchResultsEmpty(): Boolean = adapterItems.getData().isEmpty()

    override fun clearSearchResults(redrawItems: Boolean) {
        try {
            adapterItems.clearData(redrawItems)
        } catch (ignore: UnsupportedOperationException) {
        }
    }

    override fun scrollToFirstSearchResult() {
        itemsRecyclerView.layoutManager?.scrollToPosition(0)
    }

    override fun showDrinkDetails(drink: Drink, ingredientNamesFromSearch: ArrayList<String>) {
        DrinkDetailsActivity.loadDrinkById(this, drink, ingredientNamesFromSearch)
    }

    override fun showFoundIngredientMatches(matches: List<Ingredient>) {
        if (matches.isNotEmpty()) {
            if (popupListIngredientMatches == null) {
                popupListIngredientMatches = ListPopupWindow(this)
                popupListIngredientMatches?.anchorView = inputLayout
                popupListIngredientMatches?.inputMethodMode = INPUT_METHOD_NEEDED
                popupListIngredientMatches?.promptPosition = POSITION_PROMPT_BELOW
            }

            popupListIngredientMatches?.let { window ->
                val names = matches.map {
                    it.name
                }
                val adapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_list_item_1, names)
                adapter.notifyDataSetChanged()
                window.setAdapter(adapter)
                window.setOnItemClickListener { adapterView, _, position, _ ->
                    presenter?.onIngredientMatchSelected(
                            Ingredient(adapterView.adapter.getItem(position) as? String ?: ""),
                            false)
                }
                window.show()
            }
        }
    }

    override fun hideFoundIngredientMatches() {
        popupListIngredientMatches?.dismiss()
    }

    override fun addSelectedIngredient(ingredient: Ingredient) {
        val chip = ingredientsChipGroup.inflateOn<Chip>(R.layout.cocktail_chip_search)
        chip.id = View.generateViewId()
        chip.text = ingredient.name
        chip.tag = ingredient
        chip.setOnCloseIconClickListener {
            ingredientsChipGroup.removeView(chip)
            ingredientsChipGroup.show(ingredientsChipGroup.childCount > 0)
            presenter?.onSelectedIngredientRemoved(ingredientsChipGroup.childCount)
        }
        ingredientsChipGroup.addView(chip)
        ingredientsChipGroup.show()
    }

    override fun requestRemoveSelectedIngredient(ingredient: Ingredient) {
        for (i in 0 until ingredientsChipGroup.childCount) {
            val chip: Chip = ingredientsChipGroup.getChildAt(i) as Chip
            if (chip.tag === ingredient) {
                ingredientsChipGroup.removeView(chip)
            }
        }
        ingredientsChipGroup.show(ingredientsChipGroup.childCount > 0)
    }

    override fun requestRemoveAllSelectedIngredients() {
        ingredientsChipGroup.removeAllViews()
        ingredientsChipGroup.hide()
    }

    override fun showLoadingLayout(show: Boolean) {
        loadingLayout.show(show)
    }

    override fun showEmptyLayout(show: Boolean) {
        backgroundOfflineView.show(show)
        emptyResultsImageView.show(show)
        emptyResultsTextView.show(show)
    }

    override fun showResultsListLayout(show: Boolean) {
        itemsRecyclerView.show(show)
    }

    override fun showOfflineLayout(show: Boolean) {
        offlineLayout.show(show)
    }

    override fun scrollSearchInputToEnd() {}

    override fun updateSearchInputHint() {
        searchInput.hint = presenter?.getSearchInputHint(resources)
    }

    private fun showKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, 0)
    }

    override fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchInput.windowToken, 0)
    }

    override fun onConnectionStatusChanged(isConnectedToInternet: Boolean) {
        presenter?.connectionStatusChanged(isConnectedToInternet = isConnectedToInternet)
    }

    override fun onBackPressed() {
        presenter?.onBackPressed()
    }

    override fun superOnBackPressed() {
        super.onBackPressed()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            presenter?.saveOnNewIntentRunnable(Runnable {
                if (it.getStringExtra(BundleConst.INGREDIENT_NAME) != null) {
                    presenter?.onIngredientMatchSelected(
                            Ingredient(it.getStringExtra(BundleConst.INGREDIENT_NAME)), true)
                    presenter?.triggerSearchAfterSelection(true)

                    when (searchTabs.getIndexOfSelectedItem()) {
                        BY_INGREDIENTS -> presenter?.onSearchTypeChanged(SearchType.BY_INGREDIENTS)
                        BY_NAME -> searchTabs.selectItem(BY_INGREDIENTS)
                    }
                }
            })
        }
    }

    companion object {

        private const val BY_INGREDIENTS = 0
        private const val BY_NAME = 1

        fun addIngredientToSelected(context: Context, measuredIngredient: MeasuredIngredient) {
            context.startActivity(Intent(context, SearchActivity::class.java).apply {
                putExtra(BundleConst.INGREDIENT_NAME, measuredIngredient.patronName)
            })
        }

        fun open(context: Context) {
            context.startActivity(Intent(context, SearchActivity::class.java))
        }

    }
}