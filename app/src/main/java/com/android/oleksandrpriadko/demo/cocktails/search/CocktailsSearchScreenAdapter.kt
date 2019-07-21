package com.android.oleksandrpriadko.demo.cocktails.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.oleksandrpriadko.demo.R
import com.android.oleksandrpriadko.demo.cocktails.model.Cocktail
import com.android.oleksandrpriadko.recycler_adapter.BaseAdapterRecyclerView
import com.android.oleksandrpriadko.recycler_adapter.BaseItemListener
import kotlinx.android.synthetic.main.cocktail_item_cocktail_search_screen.view.*

class CocktailSearchScreenAdapter(itemListener: BaseItemListener<Cocktail>? = null)
    : BaseAdapterRecyclerView<Cocktail, Holder, BaseItemListener<Cocktail>>(itemListener) {

    override fun isItemViewClickable(): Boolean = false

    override fun onGetHolder(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): Holder {
        return Holder(inflater.inflate(R.layout.cocktail_item_cocktail_search_screen, parent, false))
    }

    override fun onBindHolder(holder: Holder, position: Int) {
        holder.onBind(items[position])
        holder.itemView.avatarCardView.setOnClickListener {
            itemListener?.itemClicked(position, items[position])
        }
    }
}

class Holder(iteView: View) : RecyclerView.ViewHolder(iteView) {

    fun onBind(cocktail: Cocktail) {
    }

}