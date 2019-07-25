package com.android.oleksandrpriadko.demo.cocktails.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.oleksandrpriadko.demo.R
import com.android.oleksandrpriadko.demo.cocktails.model.DrinkDetails
import com.android.oleksandrpriadko.recycler_adapter.BaseAdapterRecyclerView
import com.android.oleksandrpriadko.recycler_adapter.BaseItemListener
import com.android.oleksandrpriadko.recycler_adapter.PicassoHolder
import kotlinx.android.synthetic.main.cocktail_item_cocktail_search_screen.view.*

class CarouselDrinksAdapter(itemListener: BaseItemListener<DrinkDetails>? = null)
    : BaseAdapterRecyclerView<DrinkDetails, CarouselDrinkHolder, BaseItemListener<DrinkDetails>>(itemListener) {

    override fun isItemViewClickable(): Boolean = false

    override fun onGetHolder(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): CarouselDrinkHolder {
        return CarouselDrinkHolder(inflater.inflate(R.layout.cocktail_item_cocktail_search_screen, parent, false))
    }

    override fun onBindHolder(holder: CarouselDrinkHolder, position: Int) {
        holder.onBind(items[position])
        holder.itemView.avatarCardView.setOnClickListener {
            itemListener?.itemClicked(position, items[position])
        }
    }
}

class CarouselDrinkHolder(iteView: View) : PicassoHolder(iteView) {

    fun onBind(drinkDetails: DrinkDetails) {
        loadImage(drinkDetails.strDrinkThumb, itemView.avatarImageView, R.drawable.main_ic_cocktail_512)
        itemView.nameTextView.text = drinkDetails.strDrink
    }

}