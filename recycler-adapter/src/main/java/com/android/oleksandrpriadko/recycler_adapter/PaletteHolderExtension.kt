package com.android.oleksandrpriadko.recycler_adapter

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import androidx.palette.graphics.Palette

class PaletteHolderExtension {

    fun createPaletteAsync(bitmap: Bitmap, paletteListener: PaletteListener) {
        Palette.from(bitmap).generate {
            notifySwatchExtracted(it, paletteListener)
        }
    }

    fun createPaletteAsync(imageView: ImageView, paletteListener: PaletteListener) {
        val drawable = imageView.drawable
        if (drawable is BitmapDrawable) {
            Palette.from(drawable.bitmap).generate {
                notifySwatchExtracted(it, paletteListener)
            }
        }
    }

    private fun notifySwatchExtracted(palette: Palette?, paletteListener: PaletteListener) {
        palette?.let {
            paletteListener.onPaletteExtracted(it)
        }
    }
}

interface PaletteListener {

    fun onPaletteExtracted(palette: Palette)

}