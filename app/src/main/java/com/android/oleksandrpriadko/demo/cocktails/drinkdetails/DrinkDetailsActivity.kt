package com.android.oleksandrpriadko.demo.cocktails.drinkdetails

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearSnapHelper
import com.android.oleksandrpriadko.demo.R
import com.android.oleksandrpriadko.demo.cocktails.model.BundleConst
import com.android.oleksandrpriadko.demo.cocktails.model.DrinkDetails
import com.android.oleksandrpriadko.demo.cocktails.search.SearchActivity
import com.android.oleksandrpriadko.extension.hide
import com.android.oleksandrpriadko.extension.show
import com.android.oleksandrpriadko.recycler_adapter.PicassoHolderExtension
import kotlinx.android.synthetic.main.cocktail_activity_cocktail_details.*
import kotlinx.android.synthetic.main.cocktail_ingredient_popup_layout.view.*

class DrinkDetailsActivity : AppCompatActivity(), PresenterView {

    private lateinit var presenter: DrinkDetailsPresenter
    private val detailsAdapter = DrinkDetailsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = DrinkDetailsPresenter(this, getString(R.string.cocktail_base_url))

        setContentView(R.layout.cocktail_activity_cocktail_details)

        initDetailsRecView()

        requestLoadCocktail(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        requestLoadCocktail(intent)
    }

    private fun initDetailsRecView() {
        ingredientLayout.setOnClickListener {
            presenter.onIngredientLayoutClicked()
        }

        LinearSnapHelper().attachToRecyclerView(cocktailDetailsRecView)
        cocktailDetailsRecView.adapter = detailsAdapter
        detailsAdapter.itemListener = object : ItemListener {
            override fun onIngredientClick(ingredientName: String?) {
                presenter.onIngredientChipClicked(ingredientName)
            }

            override fun isEmpty(isEmpty: Boolean) {}

            override fun itemClicked(position: Int, item: SelectedPage) {}
        }
    }

    override fun showIngredientPopup(show: Boolean) {
        if (show) ingredientLayout.show() else ingredientLayout.hide()
    }

    override fun loadIngredientImage(imageUrl: String) {
        PicassoHolderExtension.loadImage(imageUrl,
                ingredientLayout.avatarImageView,
                R.drawable.main_ic_cocktail_512)

    }

    private fun requestLoadCocktail(intent: Intent?) {
        intent?.let {
            val drinkId = intent.getStringExtra(BundleConst.DRINK_ID)
            presenter.loadDrinkDetails(drinkId)
        }
    }

    override fun populateDrinkDetails(drinkDetails: DrinkDetails) {
        detailsAdapter.drinkDetails = drinkDetails
        detailsAdapter.notifyDataSetChanged()
    }

    override fun openSearchWithIngredient(shownIngredientName: String) {
        SearchActivity.addIngredientToSelected(this, shownIngredientName)
    }

    companion object {

        fun loadDrinkById(context: Context, drinkId: String) {
            context.startActivity(Intent(context, DrinkDetailsActivity::class.java).apply {
                putExtra(BundleConst.DRINK_ID, drinkId)
            })
        }
    }
}