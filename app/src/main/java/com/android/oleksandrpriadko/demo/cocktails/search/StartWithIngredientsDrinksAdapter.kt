package com.android.oleksandrpriadko.demo.cocktails.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.oleksandrpriadko.demo.R
import com.android.oleksandrpriadko.demo.cocktails.drinkdetails.MeasureUnit
import com.android.oleksandrpriadko.demo.cocktails.model.DrinkDetails
import com.android.oleksandrpriadko.extension.inflateOn
import com.android.oleksandrpriadko.recycler_adapter.BaseAdapterRecyclerView
import com.android.oleksandrpriadko.recycler_adapter.BaseItemListener
import com.android.oleksandrpriadko.recycler_adapter.PicassoHolderExtension
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.cocktail_item_drink.view.avatarCardView
import kotlinx.android.synthetic.main.cocktail_item_drink.view.avatarImageView
import kotlinx.android.synthetic.main.cocktail_item_drink.view.nameTextView
import kotlinx.android.synthetic.main.cocktail_item_drink_with_ingredients.view.*

class StartWithIngredientsDrinksAdapter(itemListener: StartsWithIngredientsItemListener? = null)
    : BaseAdapterRecyclerView<DrinkDetails, StartWithIngredientsDrinkHolder, StartsWithIngredientsItemListener>(itemListener) {

    override fun isItemViewClickable(): Boolean = false

    override fun onGetHolder(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): StartWithIngredientsDrinkHolder {
        return StartWithIngredientsDrinkHolder(
                inflater.inflate(R.layout.cocktail_item_drink_with_ingredients, parent, false),
                itemListener)
    }

    override fun onBindHolder(holder: StartWithIngredientsDrinkHolder, position: Int) {
        holder.onBind(items[position])
        holder.itemView.avatarCardView.setOnClickListener {
            itemListener?.itemClicked(position, items[position])
        }
    }
}

class StartWithIngredientsDrinkHolder(iteView: View, private val itemListener: StartsWithIngredientsItemListener?)
    : RecyclerView.ViewHolder(iteView) {

    private val picassoHolderExtension = PicassoHolderExtension(itemView.context)

    fun onBind(drinkDetails: DrinkDetails) {
        picassoHolderExtension.loadImage(drinkDetails.strDrinkThumb, itemView.avatarImageView, R.drawable.main_ic_cocktail_512)

        itemView.nameTextView.text = drinkDetails.strDrink

        for (ingredient in drinkDetails.getListOfIngredientsNamesAndMeasureUnits(MeasureUnit.ML)) {
            val chip = itemView.ingredientsCarouselChipGroup.inflateOn<Chip>(R.layout.cocktail_chip_carousel)
            chip.text = ingredient
            chip.setOnClickListener {
                itemListener?.let {
                    val chipText = chip.text
                    if (chipText != null && chipText.isNotEmpty()) {
                        it.onIngredientClicked(chipText.toString(), adapterPosition)
                    }
                }
            }
            itemView.ingredientsCarouselChipGroup.addView(chip)
        }
    }
}

interface StartsWithIngredientsItemListener : BaseItemListener<DrinkDetails> {

    fun onIngredientClicked(ingredientName: String, drinkPosition: Int)

}