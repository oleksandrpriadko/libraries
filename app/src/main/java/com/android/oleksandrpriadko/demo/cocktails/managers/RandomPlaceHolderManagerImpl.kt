package com.android.oleksandrpriadko.demo.cocktails.managers

import com.android.oleksandrpriadko.demo.R

class RandomPlaceHolderManagerImpl : RandomPlaceholderManager {

    override fun pickPlaceHolder(): Int {
        return listOfDrawablesIds.shuffled()[0]
    }

    companion object {
        private val listOfDrawablesIds: List<Int> = listOf(
                R.drawable.ic_loading_drink_1,
                R.drawable.ic_loading_drink_2,
                R.drawable.ic_loading_drink_3,
                R.drawable.ic_loading_drink_8,
                R.drawable.ic_loading_drink_9,
                R.drawable.ic_loading_drink_10,
                R.drawable.ic_loading_drink_11,
                R.drawable.ic_loading_drink_12,
                R.drawable.ic_loading_drink_13,
                R.drawable.ic_loading_drink_14,
                R.drawable.ic_loading_drink_15,
                R.drawable.ic_loading_drink_16,
                R.drawable.ic_loading_drink_17,
                R.drawable.ic_loading_drink_18,
                R.drawable.ic_loading_drink_20,
                R.drawable.ic_loading_drink_21,
                R.drawable.ic_loading_drink_22,
                R.drawable.ic_loading_drink_23,
                R.drawable.ic_loading_drink_24,
                R.drawable.ic_loading_drink_26,
                R.drawable.ic_loading_drink_28,
                R.drawable.ic_loading_drink_29,
                R.drawable.ic_loading_drink_30,
                R.drawable.ic_loading_drink_32)
    }
}