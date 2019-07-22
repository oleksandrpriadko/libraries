package com.android.oleksandrpriadko.demo.cocktails.search

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearSnapHelper
import com.android.oleksandrpriadko.demo.R
import com.android.oleksandrpriadko.demo.cocktails.cocktaildetails.CocktailDetailsActivity
import com.android.oleksandrpriadko.demo.cocktails.model.Cocktail
import com.android.oleksandrpriadko.demo.cocktails.model.Drink
import com.android.oleksandrpriadko.recycler_adapter.BaseItemListenerAdapter
import kotlinx.android.synthetic.main.cocktail_activity_cocktail_search.*

class SearchActivity : AppCompatActivity(), PresenterView {

    private lateinit var presenter: SearchPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = SearchPresenter(getString(R.string.cocktail_base_url), this)

        setContentView(R.layout.cocktail_activity_cocktail_search)

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
        adapter.itemListener = object : BaseItemListenerAdapter<Cocktail>() {
            override fun itemClicked(position: Int, item: Cocktail) {
                super.itemClicked(position, item)
            }
        }

        LinearSnapHelper().attachToRecyclerView(searchResultsRecView)

        searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                presenter.filterByIngredient(searchInput.text.toString())
            }
            false
        }
    }

    override fun showFoundCocktails(foundDrinks: MutableList<Drink>) {
        searchResultsRecView.adapter = FoundDrinksAdapter().apply {
            setData(foundDrinks)
            itemListener = object : BaseItemListenerAdapter<Drink>() {
                override fun itemClicked(position: Int, item: Drink) {
                    presenter.onCocktailClicked(item)
                }
            }
        }
    }

    override fun openCocktailDetails(drinkId: String) {
        CocktailDetailsActivity.loadCocktailById(this, drinkId)
    }
}