package com.android.oleksandrpriadko.demo.cocktails.managers

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

class SharedPreferencesmanagerImpl(context: Context) : SharedPreferencesManager {

    private val preferences: SharedPreferences

    init {
        preferences = context.getSharedPreferences(COCKTAIL_PREF_NAME, MODE_PRIVATE)
    }

    override fun save(string: String, key: Key) {
        preferences.edit().putString(key.key, string).apply()
    }

    override fun save(float: Float, key: Key) {
        preferences.edit().putFloat(key.key, float).apply()
    }

    override fun get(key: Key): Float = preferences.getFloat(key.key, 0f)

    companion object {
        const val COCKTAIL_PREF_NAME = "COCKTAIL_PREF_NAME"
    }
}

enum class Key(val key: String) {
    LOTTIE_SPLASH_PROGRESS("LOTTIE_SPLASH_PROGRESS")
}