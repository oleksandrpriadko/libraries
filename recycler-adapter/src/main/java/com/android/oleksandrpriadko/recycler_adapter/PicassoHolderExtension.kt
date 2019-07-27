package com.android.oleksandrpriadko.recycler_adapter

import android.content.Context
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class PicassoHolderExtension(context: Context) {

    private val picasso: Picasso = getPicasso(context)

    fun loadImage(url: String,
                  imageView: ImageView,
                  @DrawableRes placeHolderId: Int,
                  @DrawableRes errorID: Int) {
        picasso
                .load(url)
                .placeholder(placeHolderId)
                .error(errorID)
                .into(imageView)
    }

    fun loadImage(url: String?,
                  imageView: ImageView,
                  @DrawableRes placeHolderId: Int) {
        picasso
                .load(url)
                .placeholder(placeHolderId)
                .error(R.drawable.ic_error_outline_black_24dp)
                .into(imageView)
    }

    fun loadImage(url: String?,
                  imageView: ImageView,
                  @DrawableRes placeHolderId: Int,
                  callback: Callback) {
        picasso
                .load(url)
                .placeholder(placeHolderId)
                .error(R.drawable.ic_error_outline_black_24dp)
                .into(imageView, callback)
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
