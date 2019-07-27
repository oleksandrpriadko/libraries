package com.android.oleksandrpriadko.demo.cocktails.search

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.android.oleksandrpriadko.demo.R
import com.android.oleksandrpriadko.demo.cocktails.model.DrinkDetails
import com.android.oleksandrpriadko.extension.inflateOn
import com.android.oleksandrpriadko.recycler_adapter.*
import com.squareup.picasso.Callback
import kotlinx.android.synthetic.main.cocktail_item_search_result.view.*
import kotlinx.android.synthetic.main.main_item_demo.view.avatarImageView

class DrinkDetailsAdapter : BaseAdapterRecyclerView<
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

class FoundDrinksHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val picassoHolderExtension = PicassoHolderExtension(itemView.context)
    private val paletteHolderExtension = PaletteHolderExtension()

    fun onBind(drinkDetails: DrinkDetails) {
        itemView.drinkNameTextView.text = drinkDetails.strDrink
        picassoHolderExtension.loadImage(drinkDetails.strDrinkThumb,
                itemView.avatarImageView,
                R.drawable.main_ic_cocktail_512,
                object : Callback {
                    override fun onSuccess() {
                        onImageLoaded()
                    }

                    override fun onError() {

                    }
                })
    }

    private fun onImageLoaded() {
        paletteHolderExtension.createPaletteAsync(itemView.avatarImageView, object : PaletteListener {
            override fun onVibrantSwatchExtracted(vibrantSwatch: Palette.Swatch) {
                val gradientDrawable = GradientDrawable(
                        GradientDrawable.Orientation.BOTTOM_TOP,
                        IntArray(2) { vibrantSwatch.rgb; Color.TRANSPARENT })
                itemView.foreground = gradientDrawable
            }
        })
    }

}