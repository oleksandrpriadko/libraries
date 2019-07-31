package com.android.oleksandrpriadko.demo.cocktails.drinkdetails

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.android.oleksandrpriadko.demo.R
import com.android.oleksandrpriadko.demo.cocktails.managers.CocktailManagerFinder
import com.android.oleksandrpriadko.demo.cocktails.model.BundleConst
import com.android.oleksandrpriadko.demo.cocktails.model.wrappers.Drink
import com.android.oleksandrpriadko.demo.cocktails.model.wrappers.Ingredient
import com.android.oleksandrpriadko.demo.cocktails.search.SearchActivity
import com.android.oleksandrpriadko.demo.main.App
import com.android.oleksandrpriadko.extension.inflateOn
import com.android.oleksandrpriadko.extension.show
import com.android.oleksandrpriadko.overlay.HideMethod
import com.android.oleksandrpriadko.overlay.Overlay
import com.android.oleksandrpriadko.overlay.OverlayManager
import com.android.oleksandrpriadko.overlay.OverlayState
import com.android.oleksandrpriadko.recycler_adapter.PicassoHolderExtension
import com.android.oleksandrpriadko.retrofit.ConnectionStatusSubscriber
import com.google.android.material.chip.Chip
import com.squareup.picasso.Callback
import kotlinx.android.synthetic.main.cocktail_activity_drink_details.*
import kotlinx.android.synthetic.main.cocktail_activity_drink_details.nameTextView
import kotlinx.android.synthetic.main.cocktail_overlay_ingredient_details.*
import kotlinx.android.synthetic.main.cocktail_overlay_ingredient_details.view.*

class DrinkDetailsActivity : AppCompatActivity(), PresenterView, ConnectionStatusSubscriber {

    private var presenter: DrinkDetailsPresenter? = null

    private var drink: Drink? = null

    private lateinit var overlayManager: OverlayManager
    private lateinit var ingredientOverlay: ViewGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = DrinkDetailsPresenter(this, getString(R.string.cocktail_base_url))

        setContentView(R.layout.cocktail_activity_drink_details)

        App.connectionStatusReceiver.subscribe(this)

        initOverlay()

        requestLoadCocktail(intent)

        goBackImageView.setOnClickListener { presenter?.onGoBackClicked() }
    }

    private fun initOverlay() {
        overlayManager = OverlayManager(overlayContainer)
        overlayManager.hideParentAfter = false
        ingredientOverlay = overlayContainer.inflateOn(R.layout.cocktail_overlay_ingredient_details)
        ingredientOverlay.addToSearchTextView.setOnClickListener { presenter?.onAddIngredientToSearch() }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        requestLoadCocktail(intent)
    }

    private fun requestLoadCocktail(intent: Intent?) {
        intent?.let {
            val drinkId = it.getStringExtra(BundleConst.DRINK_ID)
            val ingredientsFromSearch: ArrayList<String> = it.getStringArrayListExtra(
                    BundleConst.INGREDIENTS_FROM_SEARCH)
            presenter?.loadDrinkDetails(drinkId, ingredientsFromSearch)
        }
    }

    override fun showOverlayLoadingIngredient(show: Boolean) {
        ingredientLoadingLayout.show(show)
    }

    override fun populateDrinkDetails(drink: Drink, ingredientsFromSearch: List<String>) {
        this.drink = drink
        val thisDrink: Drink = this.drink ?: drink

        nameTextView.text = thisDrink.name

        avatarImageView.setImageResource(CocktailManagerFinder.randomPlaceholderManager.pickPlaceHolder())

        PicassoHolderExtension.loadImage(
                thisDrink.imageUrl,
                avatarImageView,
                CocktailManagerFinder.randomPlaceholderManager.pickPlaceHolder())
        instructionsTextView.text = thisDrink.instructions

        displayIngredientsChips(thisDrink, ingredientsFromSearch)
    }

    private fun displayIngredientsChips(drink: Drink, ingredientNamesFromSearch: List<String>) {
        for (ingredient in drink.ingredientList) {
            createAddChip(drink, ingredient, ingredientNamesFromSearch)
        }
    }

    private fun createAddChip(drink: Drink,
                              ingredient: Ingredient,
                              ingredientsFromSearch: List<String>): Chip {
        val isIngredientMatchSearch: String? = ingredientsFromSearch.find {
            it.equals(ingredient.name, true)
        }
        @LayoutRes val chipLayoutRes = if (isIngredientMatchSearch != null) {
            R.layout.cocktail_item_ingredient_match
        } else {
            R.layout.cocktail_item_ingredient
        }
        return ingredientsChipGroup.inflateOn<Chip>(chipLayoutRes, false)
                .apply {
                    text = ingredient.createSpannableWithParentheses()
                    ingredientsChipGroup.addView(this)
                    setOnClickListener {
                        presenter?.onIngredientItemClicked(drink, ingredient)
                    }
                }
    }

    override fun showIngredientOverlay(selectedIngredient: Ingredient) {
        val overlayBuilder: Overlay.Builder = Overlay.Builder(ingredientOverlay)
                .contentView(R.id.contentOverlayLayout)
                .animationShowContent(R.anim.overlay_module_slide_up)
                .animationHideContent(R.anim.overlay_module_slide_down)
                .backgroundView(R.id.backgroundOverlayLayout)
                .animationShowBackground(R.anim.overlay_module_fade_in)
                .animationHideBackground(R.anim.overlay_module_fade_out)
                .overlayListener(object : Overlay.OverlayListener {
                    override fun stateChanged(state: OverlayState) {
                        when (state) {
                            OverlayState.ANIMATING_IN -> {
                                contentOverlayLayout.setOnClickListener { presenter?.onAddIngredientToSearch() }
                            }
                            OverlayState.DISMISSED, OverlayState.DISMISSED_BACK_CLICK -> {
                                presenter?.onIngredientOverlayHidden(selectedIngredient)
                            }
                            else -> {
                            }
                        }
                    }

                })
        overlayManager.add(overlayBuilder.build())
    }

    override fun populateIngredientDescription(description: String) {

    }

    override fun hideIngredientOverlay() {
        overlayManager.hideLast(HideMethod.DEFAULT)
    }

    override fun loadIngredientImage(imageUrl: String) {
        PicassoHolderExtension.loadImage(imageUrl,
                ingredientOverlay.ingredientImageView,
                object : Callback {
                    override fun onSuccess() {
                        presenter?.ingredientImageLoaded()
                    }

                    override fun onError() {

                    }

                })
    }

    override fun populateIngredientName(name: String) {
        ingredientOverlay.nameTextView.text = name
    }

    override fun openSearchWithIngredient(ingredient: Ingredient) {
        SearchActivity.addIngredientToSelected(this, ingredient)
    }

    override fun showLoadingLayout(show: Boolean) {
        loadingLayout.show(show)
    }

    override fun onConnectionStatusChanged(isConnectedToInternet: Boolean) {
        presenter?.onConnectionStatusChanged(isConnectedToInternet)
    }

    override fun showOfflineLayout(show: Boolean) {
        offlineLayout.show(show)
    }

    override fun clearImageInOverlay() {
        ingredientOverlay.ingredientImageView.setImageResource(0)
    }

    override fun clearNameInOverlay() {
        ingredientOverlay.nameTextView.text = ""
    }

    override fun onBackPressed() {
        if (!overlayManager.hideLast(HideMethod.DEFAULT)) {
            super.onBackPressed()
        }
    }

    override fun requestCloseScreen() {
        onBackPressed()
    }

    companion object {

        fun loadDrinkById(context: Context,
                          drink: Drink,
                          ingredientsFromSearch: ArrayList<String>) {
            context.startActivity(Intent(context, DrinkDetailsActivity::class.java).apply {
                putExtra(BundleConst.DRINK_ID, drink.id)
                putStringArrayListExtra(BundleConst.INGREDIENTS_FROM_SEARCH, ingredientsFromSearch)
            })
        }
    }
}