package com.android.oleksandrpriadko.demo.cocktails.cocktaildetails

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IntDef
import com.android.oleksandrpriadko.demo.R
import com.android.oleksandrpriadko.demo.cocktails.cocktaildetails.CocktailDetailsAdapter.Companion.IMAGE_INGREDIENTS
import com.android.oleksandrpriadko.demo.cocktails.cocktaildetails.CocktailDetailsAdapter.Companion.INSTRUCTION
import com.android.oleksandrpriadko.demo.cocktails.model.DrinkDetails
import com.android.oleksandrpriadko.extension.inflateOn
import com.android.oleksandrpriadko.recycler_adapter.BaseAdapterRecyclerView
import com.android.oleksandrpriadko.recycler_adapter.BaseHolderPicasso
import com.android.oleksandrpriadko.recycler_adapter.BaseItemListener
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.cocktail_item_cocktail_details_bottom.view.*
import kotlinx.android.synthetic.main.cocktail_item_cocktail_details_top.view.*

class CocktailDetailsAdapter(itemListener: ItemListener? = null) :
        BaseAdapterRecyclerView<
                SelectedPage,
                Holder,
                BaseItemListener<SelectedPage>>(itemListener) {

    init {
        setData(mutableListOf(SelectedPage.TOP, SelectedPage.BOTTOM))
    }

    var drinkDetails: DrinkDetails = DrinkDetails()

    override fun onGetHolder(inflater: LayoutInflater,
                             parent: ViewGroup,
                             @ViewType viewType: Int): Holder {
        return when (viewType) {
            IMAGE_INGREDIENTS -> TopHolder(parent.inflateOn(
                    R.layout.cocktail_item_cocktail_details_top), itemListener as ItemListener?)
            INSTRUCTION -> BottomHolder(parent.inflateOn(
                    R.layout.cocktail_item_cocktail_details_bottom))
            else -> TopHolder(parent.inflateOn(
                    R.layout.cocktail_item_cocktail_details_top), itemListener as ItemListener?)
        }
    }

    override fun onBindHolder(holder: Holder, position: Int) {
        holder.onBind(drinkDetails)

    }

    override fun isItemViewClickable() = false

    @ViewType
    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> IMAGE_INGREDIENTS
            1 -> INSTRUCTION
            else -> IMAGE_INGREDIENTS
        }
    }

    companion object {
        const val IMAGE_INGREDIENTS = 0
        const val INSTRUCTION = 1
    }
}

abstract class Holder(itemView: View) : BaseHolderPicasso(itemView) {

    abstract fun onBind(drinkDetails: DrinkDetails)
}

class TopHolder(itemView: ViewGroup, private val itemListener: ItemListener?) : Holder(itemView) {
    override fun onBind(drinkDetails: DrinkDetails) {
        loadAvatar(drinkDetails.strDrinkThumb)

        displayIngredientsChips(drinkDetails)

        itemView.nameTextView.text = drinkDetails.strDrink
    }

    private fun loadAvatar(imageUrl: String?) {
        loadImage(imageUrl, itemView.avatarImageView, R.drawable.main_ic_cocktail_512)
    }

    private fun displayIngredientsChips(drinkDetails: DrinkDetails) {
        for (ingredient in drinkDetails.listOfIngredients) {
            (itemView as ViewGroup).inflateOn<Chip>(
                    R.layout.cocktail_item_ingredient,
                    false)
                    .apply {
                        text = ingredient
                        itemView.ingredientsChipGroup.addView(this)
                        setOnClickListener {
                            itemListener?.onIngredientClick(ingredient)
                        }
                    }
        }
    }
}

class BottomHolder(itemView: View) : Holder(itemView) {
    override fun onBind(drinkDetails: DrinkDetails) {
        itemView.instructionsTextView.text = drinkDetails.strInstructions
    }
}

enum class SelectedPage(val description: String) {
    TOP("TOP"),
    BOTTOM("BOTTOM")
}

interface ItemListener : BaseItemListener<SelectedPage> {

    fun onIngredientClick(ingredientName: String?)

}

@IntDef(IMAGE_INGREDIENTS, INSTRUCTION)
internal annotation class ViewType