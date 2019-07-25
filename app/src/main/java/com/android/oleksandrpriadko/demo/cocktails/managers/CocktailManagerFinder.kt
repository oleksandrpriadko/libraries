package com.android.oleksandrpriadko.demo.cocktails.managers

class CocktailManagerFinder {

    companion object {
        lateinit var databaseManager: CocktailAppDatabase
        lateinit var sharedPreferencesManager: SharedPreferencesManager
    }
}