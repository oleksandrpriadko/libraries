package com.android.oleksandrpriadko.demo.cocktails

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.oleksandrpriadko.demo.R
import com.android.oleksandrpriadko.demo.cocktails.model.Cocktail
import com.android.oleksandrpriadko.recycler_adapter.BaseAdapterRecyclerView
import com.android.oleksandrpriadko.recycler_adapter.BaseItemListener

class CocktailAdapter(itemListener: BaseItemListener<Cocktail>?)
    : BaseAdapterRecyclerView<Cocktail, Holder, BaseItemListener<Cocktail>>(itemListener) {

    override fun isItemViewClickable(): Boolean = false

    override fun onGetHolder(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): Holder {
        return Holder(inflater.inflate(R.layout.item_cocktail, parent, false))
    }

    override fun onBindHolder(holder: Holder, position: Int) {
        holder.onBind(items[position])
    }
}

class Holder(iteView: View) : RecyclerView.ViewHolder(iteView) {

    fun onBind(cocktail: Cocktail) {

    }

}