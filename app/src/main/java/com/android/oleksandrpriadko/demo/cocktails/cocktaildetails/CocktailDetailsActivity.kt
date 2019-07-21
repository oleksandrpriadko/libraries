package com.android.oleksandrpriadko.demo.cocktails.cocktaildetails

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearSnapHelper
import com.android.oleksandrpriadko.demo.R
import com.android.oleksandrpriadko.demo.cocktails.search.SearchActivity
import com.android.oleksandrpriadko.extension.hide
import com.android.oleksandrpriadko.extension.show
import kotlinx.android.synthetic.main.cocktail_activity_cocktail_details.*

class CocktailDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.cocktail_activity_cocktail_details)

        initDetailsRecView()
    }

    private fun initDetailsRecView() {
        ingredientPopup.setOnClickListener {
            ingredientPopup.hide()
            startActivity(Intent(this@CocktailDetailsActivity, SearchActivity::class.java))
        }

        LinearSnapHelper().attachToRecyclerView(cocktailDetailsRecView)
        cocktailDetailsRecView.adapter = CocktailDetailsAdapter().apply {
            itemListener = object : ItemListener {

                override fun onIngredientClick() {
                    ingredientPopup.show()
                }

                override fun isEmpty(isEmpty: Boolean) {}

                override fun itemClicked(position: Int, item: SelectedPage) {}
            }
        }
    }
}