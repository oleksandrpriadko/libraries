package com.android.oleksandrpriadko.demo.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.android.oleksandrpriadko.demo.R
import com.android.oleksandrpriadko.recycler_adapter.BaseAdapterRecyclerView
import com.android.oleksandrpriadko.recycler_adapter.BaseItemListener
import com.android.oleksandrpriadko.recycler_adapter.PicassoHolderExtension
import kotlinx.android.synthetic.main.main_item_demo.view.*

class AdapterDemos(itemListener: BaseItemListener<Demo>?)
    : BaseAdapterRecyclerView<
        Demo,
        AdapterDemos.Holder,
        BaseItemListener<Demo>>(itemListener) {

    override fun onGetHolder(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): Holder {
        return Holder(inflater.inflate(R.layout.main_item_demo, parent, false))
    }

    override fun isItemViewClickable(): Boolean {
        return false
    }

    override fun onBindHolder(holder: Holder, position: Int) {
        val demo = items[position]
        holder.itemView.nameTextView.text = demo.name
        holder.itemView.nameTextView.isSelected = true
        holder.itemView.itemCardView
                .setOnClickListener { itemListener?.itemClicked(position, demo) }
        holder.loadAvatar(demo.avatarUrl, demo.iconResId)
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val picassoHolderExtension = PicassoHolderExtension()

        fun loadAvatar(url: String?, @DrawableRes iconResId: Int) {
            if (iconResId == 0) {
                picassoHolderExtension.loadImage(url, itemView.avatarImageView, R.drawable.main_ic_workflow_512)
            } else {
                itemView.avatarImageView.setImageDrawable(itemView.context.getDrawable(iconResId))
            }
        }
    }
}