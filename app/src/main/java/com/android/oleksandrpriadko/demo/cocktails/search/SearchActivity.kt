package com.android.oleksandrpriadko.demo.cocktails.search

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.android.oleksandrpriadko.demo.R
import com.android.oleksandrpriadko.demo.cocktails.cocktaildetails.CocktailDetailsActivity
import com.android.oleksandrpriadko.demo.cocktails.model.Cocktail
import com.android.oleksandrpriadko.recycler_adapter.BaseItemListenerAdapter
import kotlinx.android.synthetic.main.cocktail_activity_cocktail_search.*

class SearchActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                startActivity(Intent(this@SearchActivity, CocktailDetailsActivity::class.java))
            }
        }
    }
}