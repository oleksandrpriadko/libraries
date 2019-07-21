package com.android.oleksandrpriadko.demo.cocktails.cocktaildetails

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IntDef
import androidx.recyclerview.widget.RecyclerView
import com.android.oleksandrpriadko.demo.R
import com.android.oleksandrpriadko.demo.cocktails.cocktaildetails.CocktailDetailsAdapter.Companion.IMAGE_INGREDIENTS
import com.android.oleksandrpriadko.demo.cocktails.cocktaildetails.CocktailDetailsAdapter.Companion.INSTRUCTION
import com.android.oleksandrpriadko.extension.inflateOn
import com.android.oleksandrpriadko.recycler_adapter.BaseAdapterRecyclerView
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
        holder.onBind()

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

abstract class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    abstract fun onBind()
}

class TopHolder(itemView: ViewGroup, private val itemListener: ItemListener?) : Holder(itemView) {
    override fun onBind() {
        for (i in 0..10) {
            (itemView as ViewGroup).inflateOn<Chip>(
                    R.layout.cocktail_item_ingredient, false).apply {
                text = "Chip # $i"
                chipIcon = context.getDrawable(R.drawable.ic_error_outline_black_24dp)
                itemView.ingredientsChipGroup.addView(this)
                setOnClickListener {
                    itemListener?.onIngredientClick()
                }
            }
        }
    }

}

class BottomHolder(itemView: View) : Holder(itemView) {
    override fun onBind() {
        itemView.instructionsTextView.text = itemView.context.getString(R.string.lorem_ipsum)
    }
}

enum class SelectedPage(val description: String) {
    TOP("TOP"),
    BOTTOM("BOTTOM")
}

interface ItemListener : BaseItemListener<SelectedPage> {

    fun onIngredientClick()

}

@IntDef(IMAGE_INGREDIENTS, INSTRUCTION)
internal annotation class ViewType