package com.android.oleksandrpriadko.recycler_adapter

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

open class PicassoHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

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

    companion object {

        private fun getPicasso(context: Context): Picasso {
            val picasso = Picasso.with(context)
            picasso.setIndicatorsEnabled(false)
            picasso.isLoggingEnabled = false
            return picasso
        }

        fun loadImage(url: String?,
                      imageView: ImageView,
                      @DrawableRes placeHolderId: Int) {
            getPicasso(imageView.context)
                    .load(url)
                    .placeholder(placeHolderId)
                    .error(R.drawable.ic_error_outline_black_24dp)
                    .into(imageView)
        }
    }
}
