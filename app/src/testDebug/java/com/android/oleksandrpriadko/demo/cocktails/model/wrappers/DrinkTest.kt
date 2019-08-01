package com.android.oleksandrpriadko.demo.cocktails.model.wrappers

import io.realm.RealmList
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class DrinkTest {

    private var drink: Drink = Drink()

    @Before
    @Throws(Exception::class)
    fun setUp() {
        drink = Drink()
    }

    @Test
    fun calculateIngredientMatches() {
        drink.ingredientList = RealmList()
        drink.ingredientList.add(Ingredient("name0"))
        drink.ingredientList.add(Ingredient("name1"))
        drink.ingredientList.add(Ingredient("name2"))

        assertEquals(2, drink.calculateIngredientMatches(listOf("name1", "name2")))
    }

    @Test
    fun hasEmptyFields() {
        assertTrue(drink.hasEmptyFields())

        drink.id = "id"
        assertTrue(drink.hasEmptyFields())

        drink.name = "name"
        assertTrue(drink.hasEmptyFields())

        drink.ingredientList = RealmList()
        drink.ingredientList.add(Ingredient())
        assertTrue(drink.hasEmptyFields())

        drink.instructions = "instr"
        assertTrue(drink.hasEmptyFields())

        drink.glassType = "glass"
        assertTrue(drink.hasEmptyFields())

        drink.alcoholicType = "alc"
        assertTrue(drink.hasEmptyFields())

        drink.category = "cat"
        assertTrue(drink.hasEmptyFields())

        drink.imageUrl = "url"
        assertFalse(drink.hasEmptyFields())
    }

    @Test
    fun fillEmptyFields() {
        val drink2 = Drink()
        drink2.id = "2"
        drink2.name = "2"
        drink2.ingredientList = RealmList(Ingredient("2"), Ingredient("2"), Ingredient("2"))
        drink2.imageUrl = "2"
        drink2.category = "2"
        drink2.alcoholicType = "2"
        drink2.glassType = "2"
        drink2.instructions = "2"

        drink.fillEmptyFields(drink2)
        assertTrue(drink == drink2)
    }
}