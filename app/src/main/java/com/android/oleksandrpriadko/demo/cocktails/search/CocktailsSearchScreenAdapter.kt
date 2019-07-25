package com.android.oleksandrpriadko.demo.cocktails.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.oleksandrpriadko.demo.R
import com.android.oleksandrpriadko.demo.cocktails.model.DrinkDetails
import com.android.oleksandrpriadko.recycler_adapter.BaseAdapterRecyclerView
import com.android.oleksandrpriadko.recycler_adapter.BaseHolderPicasso
import com.android.oleksandrpriadko.recycler_adapter.BaseItemListener
import kotlinx.android.synthetic.main.cocktail_item_cocktail_search_screen.view.*

class CarouselDrinksAdapter(itemListener: BaseItemListener<DrinkDetails>? = null)
    : BaseAdapterRecyclerView<DrinkDetails, CocktailSearchScreenHolder, BaseItemListener<DrinkDetails>>(itemListener) {

    override fun isItemViewClickable(): Boolean = false

    override fun onGetHolder(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): CocktailSearchScreenHolder {
        return CocktailSearchScreenHolder(inflater.inflate(R.layout.cocktail_item_cocktail_search_screen, parent, false))
    }

    override fun onBindHolder(holder: CocktailSearchScreenHolder, position: Int) {
        holder.onBind(items[position])
        holder.itemView.avatarCardView.setOnClickListener {
            itemListener?.itemClicked(position, items[position])
        }
    }
}

class CocktailSearchScreenHolder(iteView: View) : BaseHolderPicasso(iteView) {

    fun onBind(drinkDetails: DrinkDetails) {
        loadImage(drinkDetails.strDrinkThumb, itemView.avatarImageView, R.drawable.main_ic_cocktail_512)
        itemView.nameTextView.text = drinkDetails.strDrink
    }

}