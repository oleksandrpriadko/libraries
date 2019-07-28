package com.android.oleksandrpriadko.demo.cocktails.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.oleksandrpriadko.demo.R
import com.android.oleksandrpriadko.demo.cocktails.managers.CocktailManagerFinder
import com.android.oleksandrpriadko.demo.cocktails.model.DrinkDetails
import com.android.oleksandrpriadko.recycler_adapter.BaseAdapterRecyclerView
import com.android.oleksandrpriadko.recycler_adapter.BaseItemListener
import com.android.oleksandrpriadko.recycler_adapter.PicassoHolderExtension
import kotlinx.android.synthetic.main.cocktail_item_drink.view.*

class StartDrinksAdapter(itemListener: StartItemListener? = null)
    : BaseAdapterRecyclerView<DrinkDetails, StartDrinkHolder, StartItemListener>(itemListener) {

    override fun isItemViewClickable(): Boolean = false

    override fun onGetHolder(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): StartDrinkHolder {
        return StartDrinkHolder(
                inflater.inflate(R.layout.cocktail_item_drink, parent, false))
    }

    override fun onBindHolder(holder: StartDrinkHolder, position: Int) {
        holder.onBind(items[position])
        holder.itemView.avatarCardView.setOnClickListener {
            itemListener?.itemClicked(position, items[position])
        }
    }
}

class StartDrinkHolder(iteView: View)
    : RecyclerView.ViewHolder(iteView) {

    private val picassoHolderExtension = PicassoHolderExtension(itemView.context)

    fun onBind(drinkDetails: DrinkDetails) {
        picassoHolderExtension.loadImage(
                drinkDetails.strDrinkThumb,
                itemView.avatarImageView,
                CocktailManagerFinder.randomPlaceholderManager.pickPlaceHolder())

        itemView.nameTextView.text = drinkDetails.strDrink
    }
}

interface StartItemListener : BaseItemListener<DrinkDetails>