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

    private val constraintSetLoading: ConstraintSet = ConstraintSet()
    private val constraintSetResults: ConstraintSet = ConstraintSet()
    private val constraintSetEmpty: ConstraintSet = ConstraintSet()

    private val adapterItems = StartDrinksAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = SearchPresenter(getString(R.string.cocktail_base_url), this)

        setContentView(R.layout.cocktail_activity_search)

        initConstraintSets()
        prepareSearch()
        initItemsRecView()

        App.connectionStatusReceiver.subscribe(this)

        presenter?.searchPopularDrinks()
    }

    private fun initConstraintSets() {
        constraintSetLoading.clone(
                this, R.layout.cocktail_activity_loading_state)
        constraintSetResults.clone(
                this, R.layout.cocktail_activity_results_state)
        constraintSetEmpty.clone(
                this, R.layout.cocktail_activity_empty_state)
    }

    private fun initItemsRecView() {
        adapterItems.itemListener = object : StartItemListener {
            override fun isEmpty(isEmpty: Boolean) {}

            override fun itemClicked(position: Int, item: DrinkDetails) {
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
        adapterItems.setData(foundDrinkDetails)
    }

    override fun areSearchResultsEmpty(): Boolean = adapterItems.getData().isEmpty()

    override fun clearSearchResults(redrawItems: Boolean) {
        adapterItems.clearData(redrawItems)
    }

    override fun scrollToFirstSearchResult() {
        itemsRecyclerView.layoutManager?.scrollToPosition(0)
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
                it.anchorView = inputLayout
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

    override fun addSelectedIngredient(ingredient: IngredientName) {
        addSelectedIngredient(ingredient.strIngredient1)
    }

    override fun addSelectedIngredient(ingredientName: String) {
        val chip = ingredientsChipGroup.inflateOn<Chip>(R.layout.cocktail_chip_search)
        chip.text = ingredientName
        chip.setOnCloseIconClickListener {
            ingredientsChipGroup.removeView(chip)
            ingredientsChipGroup.show(ingredientsChipGroup.childCount > 0)
            presenter?.onSelectedIngredientRemoved(ingredientsChipGroup.childCount)
        }
        ingredientsChipGroup.addView(chip)
        ingredientsChipGroup.show()
    }

    override fun requestRemoveSelectedIngredient(ingredient: IngredientName) {
        for (i in 0 until ingredientsChipGroup.childCount) {
            val chip: Chip = ingredientsChipGroup.getChildAt(i) as Chip
            if (chip.text == ingredient.strIngredient1) {
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
            if (it.getStringExtra(BundleConst.INGREDIENT_NAME) != null) {
                presenter?.onIngredientMatchSelected(
                        it.getStringExtra(BundleConst.INGREDIENT_NAME), true)
                presenter?.triggerSearchAfterSelection(true)

                when (searchTabs.getIndexOfSelectedItem()) {
                    BY_INGREDIENTS -> presenter?.onSearchTypeChanged(SearchType.BY_INGREDIENTS)
                    BY_NAME -> searchTabs.selectItem(BY_INGREDIENTS)
                }

            }
        }
    }

    override fun applyState(state: State) {
        val transition = AutoTransition().apply {
            duration = 0
        }
        TransitionManager.beginDelayedTransition(motionParent, transition)
        when (state) {
            State.LOADING -> {
                constraintSetLoading.applyTo(motionParent)
            }
            State.RESULTS -> {
                constraintSetResults.applyTo(motionParent)
            }
            State.EMPTY -> {
                constraintSetEmpty.applyTo(motionParent)
            }
            State.OFFLINE -> offlineLayout.show()
            State.ONLINE -> offlineLayout.hide()
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