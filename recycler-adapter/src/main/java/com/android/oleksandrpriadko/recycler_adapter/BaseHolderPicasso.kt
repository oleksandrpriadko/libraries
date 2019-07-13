package com.android.oleksandrpriadko.recycler_adapter

import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

open class BaseHolderPicasso(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val picasso: Picasso
        get() {
            val picasso = Picasso.with(itemView.context)
            picasso.setIndicatorsEnabled(false)
            picasso.isLoggingEnabled = false
            return picasso
        }

    protected fun loadImage(url: String,
                            imageView: ImageView,
                            @DrawableRes placeHolderId: Int,
                            @DrawableRes errorID: Int) {
        picasso
                .load(url)
                .placeholder(placeHolderId)
                .error(errorID)
                .into(imageView)
    }

    protected fun loadImage(url: String?,
                            imageView: ImageView,
                            @DrawableRes placeHolderId: Int) {
        picasso
                .load(url)
                .placeholder(placeHolderId)
                .error(R.drawable.ic_error_outline_black_24dp)
                .into(imageView)
    }
}
