package com.android.oleksandrpriadko.demo.cocktails.cocktaildetails

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.android.oleksandrpriadko.demo.R

class CocktailDetailsActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.cocktail_activity_cocktail_details)
    }
}