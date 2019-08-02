package com.android.oleksandrpriadko.recycler_adapter

import android.content.Context
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.android.oleksandrpriadko.core.CoreServiceManager
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator
import java.lang.Exception

class PicassoHolderExtension {
    fun loadImage(url: String?,
                  imageView: ImageView,
                  @DrawableRes placeHolderDrawableId: Int = -1,
                  @DrawableRes errorDrawableId: Int = R.drawable.ic_error_outline_black_24dp,
                  imageWidth: Int = -1,
                  imageHeight: Int = -1,
                  callback: Callback? = null) {
        Companion.loadImage(url,
                imageView,
                placeHolderDrawableId,
                errorDrawableId,
                imageWidth,
                imageHeight,
                callback)
    }

    companion object {

        private fun getPicasso(context: Context): Picasso {
            val picasso = Picasso.with(context)
            picasso.setIndicatorsEnabled(BuildConfig.DEBUG)
            picasso.isLoggingEnabled = false
            return picasso
        }

        fun loadImage(url: String?,
                      imageView: ImageView,
                      @DrawableRes placeHolderDrawableId: Int = -1,
                      @DrawableRes errorDrawableId: Int = R.drawable.ic_error_outline_black_24dp,
                      imageWidth: Int = -1,
                      imageHeight: Int = -1,
                      callback: Callback? = null) {
            try {
                val picasso: Picasso = getPicasso(imageView.context)

                val requestCreator: RequestCreator = picasso.load(url)
                if (placeHolderDrawableId != -1) {
                    requestCreator.placeholder(placeHolderDrawableId)
                }
                if (imageHeight > 0 && imageWidth > 0) {
                    requestCreator.resize(imageWidth, imageHeight)
                }
                requestCreator.error(errorDrawableId)
                if (callback != null) {
                    requestCreator.into(imageView, callback)
                } else {
                    requestCreator.into(imageView)
                }
            } catch (e: Exception) {
                CoreServiceManager.logService.e(e.message, e.cause?.message)
                imageView.setImageResource(errorDrawableId)
            }
        }
    }
}
