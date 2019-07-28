package com.android.oleksandrpriadko.recycler_adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapterRecyclerView<
        D,
        H : RecyclerView.ViewHolder,
        I : BaseItemListener<D>?>(var itemListener: I?)
    : RecyclerView.Adapter<H>() {

    protected var items: MutableList<D> = mutableListOf()
    private var inflater: LayoutInflater? = null

    protected abstract fun isItemViewClickable(): Boolean

    fun setData(data: MutableList<D>) {
        items = data
        if (items.isEmpty()) {
            this.itemListener?.isEmpty(true)
            return
        }
        this.itemListener?.isEmpty(items.isEmpty())
        this.notifyDataSetChanged()
    }

    fun getData(): List<D> {
        return items
    }

    fun clearData() {
        items.clear()
        notifyDataSetChanged()
    }

    fun addDataAnimate(newItems: List<D>) {
        for (newItem in newItems) {
            addItemAnimate(newItem)
        }
    }

    fun addItemAnimate(item: D) {
        val itemPosition = items.size
        items.add(item)
        this.itemListener?.isEmpty(items.isEmpty())
        notifyItemInserted(itemPosition)
    }

    fun removeItemAnimate(position: Int) {
        items.removeAt(position)
        this.itemListener?.isEmpty(items.isEmpty())
        notifyItemRemoved(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): H {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.context)
        }
        return onGetHolder(inflater!!, parent, viewType)
    }

    protected abstract fun onGetHolder(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): H

    override fun onBindViewHolder(holder: H, position: Int) {
        if (isItemViewClickable()) {
            holder.itemView.setOnClickListener {
                itemListener?.itemClicked(position, items[position])
            }
        }
        this.onBindHolder(holder, position)
    }

    protected abstract fun onBindHolder(holder: H, position: Int)

    override fun getItemCount(): Int = items.size
}
