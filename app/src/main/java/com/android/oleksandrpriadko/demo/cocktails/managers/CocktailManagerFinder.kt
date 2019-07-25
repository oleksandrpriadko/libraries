package com.android.oleksandrpriadko.demo.cocktails.managers

import android.content.SharedPreferences
import com.android.oleksandrpriadko.demo.cocktails.managers.CocktailAppDatabase

class CocktailManagerFinder {

    companion object {
        lateinit var databaseManager: CocktailAppDatabase
        lateinit var sharedPreferencesManager: SharedPreferencesManager
    }
}