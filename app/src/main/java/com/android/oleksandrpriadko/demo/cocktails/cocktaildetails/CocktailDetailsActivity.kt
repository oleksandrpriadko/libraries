package com.android.oleksandrpriadko.demo.cocktails.cocktaildetails

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearSnapHelper
import com.android.oleksandrpriadko.demo.R
import com.android.oleksandrpriadko.demo.cocktails.BundleConst
import com.android.oleksandrpriadko.demo.cocktails.model.CocktailApi
import com.android.oleksandrpriadko.demo.cocktails.model.DrinkDetails
import com.android.oleksandrpriadko.demo.cocktails.search.SearchActivity
import com.android.oleksandrpriadko.extension.hide
import com.android.oleksandrpriadko.extension.show
import com.android.oleksandrpriadko.recycler_adapter.BaseHolderPicasso
import kotlinx.android.synthetic.main.cocktail_activity_cocktail_details.*
import kotlinx.android.synthetic.main.cocktail_ingredient_popup.view.*

class CocktailDetailsActivity : AppCompatActivity(), PresenterView {

    private lateinit var presenter: CocktailDetailsPresenter
    private val detailsAdapter = CocktailDetailsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = CocktailDetailsPresenter(this, getString(R.string.cocktail_base_url))

        setContentView(R.layout.cocktail_activity_cocktail_details)

        initDetailsRecView()

        requestLoadCocktail(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        requestLoadCocktail(intent)
    }

    private fun initDetailsRecView() {
        ingredientPopup.setOnClickListener {
            ingredientPopup.hide()
        }

        LinearSnapHelper().attachToRecyclerView(cocktailDetailsRecView)
        cocktailDetailsRecView.adapter = detailsAdapter
        detailsAdapter.itemListener = object : ItemListener {
            override fun onIngredientClick(ingredientName: String?) {
                ingredientName?.let {
                    ingredientPopup.show()
                    BaseHolderPicasso.loadImage(CocktailApi.createIngredientImageUrl(ingredientName),
                            ingredientPopup.avatarImageView,
                            R.drawable.main_ic_cocktail_512)
                }
            }

            override fun isEmpty(isEmpty: Boolean) {}

            override fun itemClicked(position: Int, item: SelectedPage) {}
        }
    }

    private fun requestLoadCocktail(intent: Intent?) {
        intent?.let {
            val drinkId = intent.getStringExtra(BundleConst.DRINK_ID)
            presenter.loadDrinkDetails(drinkId)
        }
    }

    override fun onDrinkDetailsLoaded(drinkDetails: DrinkDetails) {
        detailsAdapter.drinkDetails = drinkDetails
        detailsAdapter.notifyDataSetChanged()
    }

    companion object {

        fun loadCocktailById(context: Context, drinkId: String) {
            context.startActivity(Intent(context, CocktailDetailsActivity::class.java).apply {
                putExtra(BundleConst.DRINK_ID, drinkId)
            })
        }
    }
}