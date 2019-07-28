package com.android.oleksandrpriadko.demo.cocktails.managers

import androidx.annotation.DrawableRes

interface RandomPlaceholderManager {

    @DrawableRes
    fun pickPlaceHolder(): Int

}