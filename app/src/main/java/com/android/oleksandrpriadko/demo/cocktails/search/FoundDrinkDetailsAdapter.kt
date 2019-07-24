package com.android.oleksandrpriadko.demo.cocktails.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.oleksandrpriadko.demo.R
import com.android.oleksandrpriadko.demo.cocktails.model.DrinkDetails
import com.android.oleksandrpriadko.extension.inflateOn
import com.android.oleksandrpriadko.recycler_adapter.BaseAdapterRecyclerView
import com.android.oleksandrpriadko.recycler_adapter.BaseHolderPicasso
import com.android.oleksandrpriadko.recycler_adapter.BaseItemListener
import kotlinx.android.synthetic.main.cocktail_item_search_result.view.*
import kotlinx.android.synthetic.main.main_item_demo.view.avatarImageView

class FoundDrinkDetailsAdapter : BaseAdapterRecyclerView<
        DrinkDetails,
        FoundDrinksHolder,
        BaseItemListener<DrinkDetails>>(itemListener = null) {

    override fun isItemViewClickable() = false

    override fun onGetHolder(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): FoundDrinksHolder {
        return FoundDrinksHolder(parent.inflateOn(R.layout.cocktail_item_search_result))
    }

    override fun onBindHolder(holder: FoundDrinksHolder, position: Int) {
        with(holder) {
            this.onBind(items[position])
            this.itemView.avatarCardView.setOnClickListener {
                itemListener?.itemClicked(position, items[position])
            }
        }
    }
}

class FoundDrinksHolder(itemView: View) : BaseHolderPicasso(itemView) {

    fun onBind(drinkDetails: DrinkDetails) {
        itemView.drinkNameTextView.text = drinkDetails.strDrink
        loadImage(drinkDetails.strDrinkThumb, itemView.avatarImageView, R.drawable.main_ic_cocktail_512)
    }

}