package com.android.oleksandrpriadko.demo.cocktails

import android.os.Bundle
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import com.android.oleksandrpriadko.demo.R
import com.android.oleksandrpriadko.demo.cocktails.model.Cocktail
import com.android.oleksandrpriadko.extension.dimenPixelSize
import com.android.oleksandrpriadko.recycler_view.SliderLayoutManager
import kotlinx.android.synthetic.main.activity_cocktail.*

class CocktailActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_cocktail)

        val adapter = CocktailAdapter(null)
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
        itemsCarousel.adapter = adapter
        (itemsCarousel.layoutManager as SliderLayoutManager).callback = object : SliderLayoutManager.OnItemSelectedListener {
            override fun onItemSelected(layoutPosition: Int) {
                itemsCarousel.layoutManager
                        ?.findViewByPosition(layoutPosition)
                        ?.findViewById<CardView>(R.id.avatarCardView)
                        ?.cardElevation = dimenPixelSize(R.dimen.radius_card).toFloat()
            }
        }
    }
}