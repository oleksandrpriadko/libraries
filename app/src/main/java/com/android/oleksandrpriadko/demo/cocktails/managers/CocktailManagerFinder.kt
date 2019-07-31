package com.android.oleksandrpriadko.demo.cocktails.managers

class CocktailManagerFinder {

    companion object {
        lateinit var sharedPreferencesManager: SharedPreferencesManager
        val randomPlaceholderManager: RandomPlaceholderManager = RandomPlaceHolderManagerImpl()
    }
}