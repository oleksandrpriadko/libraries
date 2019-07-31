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

class StartDrinksAdapter(itemListener: StartItemListener? = null)
    : BaseAdapterRecyclerView<Drink, StartDrinkHolder, StartItemListener>(itemListener) {

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

    fun onBind(drink: Drink) {
        itemView.drinkItemImageLoadingLayout.show(true)

        picassoHolderExtension.loadImage(
                drink.imageUrl,
                itemView.avatarImageView,
                CocktailManagerFinder.randomPlaceholderManager.pickPlaceHolder(),
                object : Callback {
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

interface StartItemListener : BaseItemListener<Drink>