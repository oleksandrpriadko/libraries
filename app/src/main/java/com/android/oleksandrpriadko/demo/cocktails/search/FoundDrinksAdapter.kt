package com.android.oleksandrpriadko.demo.cocktails.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.oleksandrpriadko.demo.R
import com.android.oleksandrpriadko.demo.cocktails.managers.CocktailManagerFinder
import com.android.oleksandrpriadko.demo.cocktails.model.wrappers.Drink
import com.android.oleksandrpriadko.extension.show
import com.android.oleksandrpriadko.recycler_adapter.BaseAdapterRecyclerView
import com.android.oleksandrpriadko.recycler_adapter.BaseItemListener
import com.android.oleksandrpriadko.recycler_adapter.PicassoHolderExtension
import com.squareup.picasso.Callback
import kotlinx.android.synthetic.main.cocktail_item_drink.view.*

class FoundDrinksAdapter(itemListener: FoundDrinksItemListener? = null)
    : BaseAdapterRecyclerView<Drink, FoundDrinkHolder, FoundDrinksItemListener>(itemListener) {

    override fun isItemViewClickable(): Boolean = false

    override fun onGetHolder(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): FoundDrinkHolder {
        return FoundDrinkHolder(
                inflater.inflate(R.layout.cocktail_item_drink, parent, false))
    }

    override fun onBindHolder(holder: FoundDrinkHolder, position: Int) {
        holder.onBind(items[position])
        holder.itemView.avatarCardView.setOnClickListener {
            itemListener?.itemClicked(position, items[position])
        }
    }
}

class FoundDrinkHolder(iteView: View)
    : RecyclerView.ViewHolder(iteView) {

    private val picassoHolderExtension = PicassoHolderExtension()

    fun onBind(drink: Drink) {
        itemView.drinkItemImageLoadingLayout.show(true)

        picassoHolderExtension.loadImage(
                url = drink.imageUrl,
                imageView = itemView.avatarImageView,
                placeHolderDrawableId = CocktailManagerFinder.randomPlaceholderManager.pickPlaceHolder(),
                errorDrawableId = CocktailManagerFinder.randomPlaceholderManager.pickPlaceHolder(),
                imageWidth = itemView.measuredWidth,
                imageHeight = itemView.measuredHeight,
                callback = object : Callback {
                    override fun onSuccess() {
                        itemView.drinkItemImageLoadingLayout.show(false)
                    }

                    override fun onError() {
                        itemView.drinkItemImageLoadingLayout.show(false)
                    }

                })

        itemView.nameTextView.text = drink.name
    }
}

interface FoundDrinksItemListener : BaseItemListener<Drink>