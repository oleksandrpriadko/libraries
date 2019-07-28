package com.android.oleksandrpriadko.demo.cocktails.managers

import androidx.room.*
import com.android.oleksandrpriadko.demo.cocktails.model.IngredientName

@Dao
interface IngredientDao {
    @Query("SELECT * FROM ingredients")
    fun getAll(): List<IngredientName>

    @Query("SELECT * FROM ingredients WHERE name LIKE :text")
    fun findMatchesByName(text: String): List<IngredientName>

    @Query("SELECT * FROM ingredients WHERE name LIKE :text")
    fun findByName(text: String): IngredientName

    @Insert
    fun insert(ingredient: IngredientName)

    @Delete
    fun delete(ingredient: IngredientName)

    @Query("DELETE FROM ingredients")
    fun deleteAll()
}

@Database(entities = [IngredientName::class], version = 1)
abstract class CocktailAppDatabase : RoomDatabase() {
    abstract fun ingredientDao(): IngredientDao
}