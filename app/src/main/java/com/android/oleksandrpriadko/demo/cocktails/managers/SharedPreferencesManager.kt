package com.android.oleksandrpriadko.demo.cocktails.managers

interface SharedPreferencesManager {

    fun save(string: String, key: Key)

    fun save(float: Float, key: Key)


    fun get(key: Key): Float

}