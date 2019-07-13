package com.android.oleksandrpriadko.demo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.android.oleksandrpriadko.demo.R
import com.android.oleksandrpriadko.recycler_adapter.BaseAdapterRecyclerView
import com.android.oleksandrpriadko.recycler_adapter.BaseHolderPicasso
import com.android.oleksandrpriadko.recycler_adapter.BaseItemListener
import kotlinx.android.synthetic.main.item_demo.view.*

class AdapterDemos(itemListener: BaseItemListener<Demo>)
    : BaseAdapterRecyclerView<Demo, AdapterDemos.Holder, BaseItemListener<Demo>>(itemListener) {

    override fun onGetHolder(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): Holder {
        return Holder(inflater.inflate(R.layout.item_demo, parent, false))
    }

    override fun isItemViewClickable(): Boolean {
        return true
    }

    override fun onBindHolder(holder: Holder, position: Int) {
        val demo = items[position]
        holder.itemView.textView_demo_name.text = demo.name
        holder.loadAvatar(demo.avatarUrl)
    }

    inner class Holder(itemView: View) : BaseHolderPicasso(itemView) {

        fun loadAvatar(url: String?) {
            loadImage(url, itemView.imageView_demo_avatar, R.drawable.ic_hexagon_black_24dp)
        }
    }
}