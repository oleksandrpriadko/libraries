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
import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.android.oleksandrpriadko.demo.R
import com.android.oleksandrpriadko.demo.cocktails.drinkdetails.DrinkDetailsActivity
import com.android.oleksandrpriadko.demo.cocktails.model.BundleConst
import com.android.oleksandrpriadko.demo.cocktails.model.DrinkDetails
import com.android.oleksandrpriadko.demo.cocktails.model.IngredientName
import com.android.oleksandrpriadko.extension.hide
import com.android.oleksandrpriadko.extension.inflateOn
import com.android.oleksandrpriadko.extension.show
import com.android.oleksandrpriadko.recycler_adapter.BaseAdapterRecyclerView
import com.android.oleksandrpriadko.recycler_adapter.BaseItemListenerAdapter
import com.android.oleksandrpriadko.ui.attachedtabs.OnItemSelectedListener
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.cocktail_activity_search.*


class SearchActivity : AppCompatActivity(), PresenterView {

    private var presenter: SearchPresenter? = null

    private var popupListIngredientMatches: ListPopupWindow? = null

    private val constraintSetLoadingCarousel: ConstraintSet = ConstraintSet()
    private val constraintSetResultsCarousel: ConstraintSet = ConstraintSet()
    private val constraintSetLoadingSearch: ConstraintSet = ConstraintSet()
    private val constraintSetResultsSearch: ConstraintSet = ConstraintSet()
    private val constraintSetEmptySearch: ConstraintSet = ConstraintSet()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = SearchPresenter(getString(R.string.cocktail_base_url), this)

        setContentView(R.layout.cocktail_activity_search)

        initConstraintSets()
        prepareSearch()
        initCarousel()
        presenter?.searchPopularDrinks()
    }

    private fun initConstraintSets() {
        constraintSetLoadingCarousel.clone(
                this, R.layout.cocktail_activity_search_carousel_loading_revealed)
        constraintSetResultsCarousel.clone(
                this, R.layout.cocktail_activity_search_carousel_results_revealed)
        constraintSetLoadingSearch.clone(
                this, R.layout.cocktail_activity_search_search_loading_revealed)
        constraintSetResultsSearch.clone(
                this, R.layout.cocktail_activity_search_search_results_revealed)
        constraintSetEmptySearch.clone(
                this, R.layout.cocktail_activity_search_search_empty_revealed)
    }

    private fun initCarousel() {
        val adapter = CarouselDrinksAdapter()
        adapter.itemListener = object : ItemListener {
            override fun onIngredientClicked(ingredientName: String, drinkPosition: Int) {
                searchTabs.selectItem(BY_INGREDIENTS)
                presenter?.onIngredientFromCarouselSelected(ingredientName, true)
            }

            override fun isEmpty(isEmpty: Boolean) {

            }

            override fun itemClicked(position: Int, item: DrinkDetails) {
                presenter?.onDrinkClicked(item)
            }
        }
        itemsCarouselRecyclerView.adapter = adapter
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
    }

    override fun getCurrentInputText(): CharSequence? = searchInput.text

    override fun clearSearchInputText() {
        searchInput.setText("")
    }

    override fun getSelectedIngredients(): List<String> {
        val selectedIngredients = mutableListOf<String>()
        // add all selected ingredients in chips
        for (i in 0 until ingredientsChipGroup.childCount) {
            val chip: Chip = ingredientsChipGroup.getChildAt(i) as Chip
            selectedIngredients.add(chip.text.toString())
        }
        // request add current input text
        getCurrentInputText()?.let {
            if (it.isNotEmpty()) {
                selectedIngredients.add(it.toString())
            }
        }
        return selectedIngredients
    }

    override fun populateSearchResults(foundDrinkDetails: MutableList<DrinkDetails>) {
        val drinkDetailsAdapter = DrinkDetailsAdapter()
        drinkDetailsAdapter.setData(foundDrinkDetails)
        drinkDetailsAdapter.itemListener = object : BaseItemListenerAdapter<DrinkDetails>() {
            override fun itemClicked(position: Int, item: DrinkDetails) {
                presenter?.onDrinkClicked(item)
            }
        }
        searchResultsRecView.adapter = drinkDetailsAdapter
    }

    override fun clearSearchResults() {
        searchResultsRecView.adapter?.let {
            if (it is BaseAdapterRecyclerView<*, *, *>) {
                it.clearData()
            }
        }
    }

    override fun scrollToFirstSearchResult() {
        searchResultsRecView.layoutManager?.scrollToPosition(0)
    }

    override fun showDrinkDetails(drinkId: String) {
        DrinkDetailsActivity.loadDrinkById(this, drinkId)
    }

    override fun showFoundIngredientMatches(matches: List<String>) {
        if (matches.isNotEmpty()) {
            if (popupListIngredientMatches == null) {
                popupListIngredientMatches = ListPopupWindow(this)
            }

            popupListIngredientMatches?.let {
                it.anchorView = inputScrollView
                it.inputMethodMode = INPUT_METHOD_NEEDED
                it.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, matches).apply { notifyDataSetChanged() })
                it.setOnItemClickListener { adapterView, _, position, _ ->
                    presenter?.onIngredientMatchSelected(
                            adapterView.adapter.getItem(position) as String,
                            false)
                }
                it.promptPosition = POSITION_PROMPT_BELOW
                it.show()
            }
        }
    }

    override fun hideFoundIngredientMatches() {
        popupListIngredientMatches?.dismiss()
    }

    override fun addSelectedIngredient(ingredientName: IngredientName) {
        addSelectedIngredient(ingredientName.strIngredient1)
    }

    override fun addSelectedIngredient(ingredientName: String) {
        val chip = ingredientsChipGroup.inflateOn<Chip>(R.layout.cocktail_chip_search)
        chip.text = ingredientName
        chip.setOnCloseIconClickListener {
            ingredientsChipGroup.removeView(chip)
            ingredientsChipGroup.show(ingredientsChipGroup.childCount > 0)
        }
        ingredientsChipGroup.addView(chip)
        ingredientsChipGroup.show()
    }

    override fun requestRemoveSelectedIngredient(ingredientName: IngredientName) {
        for (i in 0 until ingredientsChipGroup.childCount) {
            val chip: Chip = ingredientsChipGroup.getChildAt(i) as Chip
            if (chip.text == ingredientName.strIngredient1) {
                ingredientsChipGroup.removeView(chip)
            }
        }
        ingredientsChipGroup.show(ingredientsChipGroup.childCount > 0)
    }

    override fun requestRemoveAllSelectedIngredients() {
        ingredientsChipGroup.removeAllViews()
        ingredientsChipGroup.hide()
    }

    override fun scrollSearchInputToEnd() {}

    override fun updateSearchInputHint() {
        searchInput.hint = presenter?.getSearchInputHint(resources)
    }

    override fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchInput.windowToken, 0)
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
            if (it.getStringExtra(BundleConst.INGREDIENT_NAME) != null) {
                searchTabs.selectItem(BY_INGREDIENTS)
                presenter?.onIngredientMatchSelected(
                        it.getStringExtra(BundleConst.INGREDIENT_NAME),
                        true)
            }
        }
    }

    override fun populateCarousel(foundDrinkDetails: MutableList<DrinkDetails>) {
        (itemsCarouselRecyclerView.adapter as CarouselDrinksAdapter).setData(foundDrinkDetails)
    }

    override fun scrollToFirstCarouselResult() {

    }

    override fun applyState(state: State) {
        val transition = AutoTransition().apply {
            duration = 100
        }
        TransitionManager.beginDelayedTransition(motionParent, transition)
        when (state) {
            State.LOADING_CAROUSEL -> constraintSetLoadingCarousel.applyTo(motionParent)
            State.RESULTS_CAROUSEL -> constraintSetResultsCarousel.applyTo(motionParent)
            State.LOADING_SEARCH -> constraintSetLoadingSearch.applyTo(motionParent)
            State.RESULTS_SEARCH -> constraintSetResultsSearch.applyTo(motionParent)
            State.EMPTY_SEARCH -> constraintSetEmptySearch.applyTo(motionParent)
        }
    }

    companion object {

        private const val BY_INGREDIENTS = 0
        private const val BY_NAME = 1

        fun addIngredientToSelected(context: Context, ingredientName: String) {
            context.startActivity(Intent(context, SearchActivity::class.java).apply {
                putExtra(BundleConst.INGREDIENT_NAME, ingredientName)
            })
        }

        fun open(context: Context) {
            context.startActivity(Intent(context, SearchActivity::class.java))
        }

    }
}