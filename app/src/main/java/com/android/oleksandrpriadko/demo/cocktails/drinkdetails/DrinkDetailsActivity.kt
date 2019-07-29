package com.android.oleksandrpriadko.demo.cocktails.drinkdetails

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.android.oleksandrpriadko.demo.R
import com.android.oleksandrpriadko.demo.cocktails.managers.CocktailManagerFinder
import com.android.oleksandrpriadko.demo.cocktails.model.BundleConst
import com.android.oleksandrpriadko.demo.cocktails.model.DrinkDetails
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
import kotlinx.android.synthetic.main.cocktail_activity_drink_details.*
import kotlinx.android.synthetic.main.cocktail_overlay_ingredient_details.view.*

class DrinkDetailsActivity : AppCompatActivity(), PresenterView, ConnectionStatusSubscriber {

    private lateinit var presenter: DrinkDetailsPresenter

    private var drinkDetails: DrinkDetails? = null

    private lateinit var overlayManager: OverlayManager
    private lateinit var ingredientOverlay: ViewGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = DrinkDetailsPresenter(this, getString(R.string.cocktail_base_url))

        setContentView(R.layout.cocktail_activity_drink_details)

        App.connectionStatusReceiver.subscribe(this)

        initOverlay()

        requestLoadCocktail(intent)
    }

    private fun initOverlay() {
        overlayManager = OverlayManager(overlayContainer)
        overlayManager.hideParentAfter = false
        ingredientOverlay = overlayContainer.inflateOn(R.layout.cocktail_overlay_ingredient_details)
        ingredientOverlay.addToSearchTextView.setOnClickListener {
            presenter.onAddIngredientToSearch()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        requestLoadCocktail(intent)
    }

    private fun requestLoadCocktail(intent: Intent?) {
        intent?.let {
            val drinkId = intent.getStringExtra(BundleConst.DRINK_ID)
            presenter.loadDrinkDetails(drinkId)
        }
    }

    override fun populateDrinkDetails(drinkDetails: DrinkDetails) {
        this.drinkDetails = drinkDetails

        nameTextView.text = drinkDetails.strDrink

        avatarImageView.setImageResource(CocktailManagerFinder.randomPlaceholderManager.pickPlaceHolder())

        PicassoHolderExtension.loadImage(
                drinkDetails.strDrinkThumb,
                avatarImageView,
                CocktailManagerFinder.randomPlaceholderManager.pickPlaceHolder())
        instructionsTextView.text = drinkDetails.strInstructions

        displayIngredientsChips(drinkDetails)
    }

    private fun displayIngredientsChips(drinkDetails: DrinkDetails) {
        for ((index, ingredient) in drinkDetails.getListOfIngredientsNamesAndMeasureUnits(MeasureUnit.ML).withIndex()) {
            ingredientsChipGroup.inflateOn<Chip>(
                    R.layout.cocktail_item_ingredient,
                    false)
                    .apply {
                        text = ingredient
                        ingredientsChipGroup.addView(this)
                        setOnClickListener {
                            presenter.onIngredientItemClicked(drinkDetails.listOfIngredientsNames[index])
                        }
                    }
        }
    }

    override fun showIngredientOverlay() {
        val overlayBuilder: Overlay.Builder = Overlay.Builder(ingredientOverlay)
                .contentView(R.id.contentLayout)
                .animationShowContent(R.anim.overlay_module_slide_up)
                .animationHideContent(R.anim.overlay_module_slide_down)
                .backgroundView(R.id.backgroundLayout)
                .animationShowBackground(R.anim.overlay_module_fade_in)
                .animationHideBackground(R.anim.overlay_module_fade_out)
                .overlayListener(object : Overlay.OverlayListener {
                    override fun stateChanged(state: OverlayState) {
                        when (state) {
                            OverlayState.DISMISSED, OverlayState.DISMISSED_BACK_CLICK -> {
                                presenter.onIngredientOverlayHidden()
                            }
                            else -> {
                            }
                        }
                    }

                })
        overlayManager.add(overlayBuilder.build())
    }

    override fun setIngredientDescription(description: String) {

    }

    override fun hideIngredientOverlay() {
        overlayManager.hideLast(HideMethod.DEFAULT)
    }

    override fun loadIngredientImage(imageUrl: String) {
        PicassoHolderExtension.loadImage(imageUrl,
                ingredientOverlay.ingredientImageView,
                CocktailManagerFinder.randomPlaceholderManager.pickPlaceHolder())

    }

    override fun setIngredientName(name: String) {
        ingredientOverlay.nameTextView.text = name
    }

    override fun openSearchWithIngredient(shownIngredientName: String) {
        SearchActivity.addIngredientToSelected(this, shownIngredientName)
    }

    override fun showLoadingLayout(show: Boolean) {
        loadingLayout.show(show)
    }

    override fun onConnectionStatusChanged(isConnectedToInternet: Boolean) {
        presenter.onConnectionStatusChanged(isConnectedToInternet)
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

    companion object {

        fun loadDrinkById(context: Context, drinkId: String) {
            context.startActivity(Intent(context, DrinkDetailsActivity::class.java).apply {
                putExtra(BundleConst.DRINK_ID, drinkId)
            })
        }
    }
}