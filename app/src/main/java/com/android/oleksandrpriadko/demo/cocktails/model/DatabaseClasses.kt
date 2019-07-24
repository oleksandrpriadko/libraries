package com.android.oleksandrpriadko.demo.cocktails.model

import androidx.room.*

@Dao
interface IngredientDao {
    @Query("SELECT * FROM ingredients")
    fun getAll(): List<IngredientName>

    @Query("SELECT * FROM ingredients WHERE name LIKE :text")
    fun findMatchesByName(text: String): List<IngredientName>

    @Query("SELECT * FROM ingredients WHERE name LIKE :text")
    fun findByName(text: String): IngredientName

    @Insert
    fun insert(ingredientName: IngredientName)

    @Delete
    fun delete(user: IngredientName)
}

@Database(entities = [IngredientName::class], version = 1)
abstract class CocktailAppDatabase : RoomDatabase() {
    abstract fun ingredientDao(): IngredientDao
}