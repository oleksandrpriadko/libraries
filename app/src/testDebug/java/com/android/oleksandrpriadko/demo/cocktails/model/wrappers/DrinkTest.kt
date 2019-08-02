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
        val measuredIngredient1 = MeasuredIngredient("${drink.name}name1")
        measuredIngredient1.patronName = "name1"
        val measuredIngredient2 = MeasuredIngredient("${drink.name}name2")
        measuredIngredient2.patronName = "name2"
        val measuredIngredient3 = MeasuredIngredient("${drink.name}name3")
        measuredIngredient3.patronName = "name3"
        drink.ingredientList.add(measuredIngredient1)
        drink.ingredientList.add(measuredIngredient2)
        drink.ingredientList.add(measuredIngredient3)

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
        val measuredIngredient = MeasuredIngredient("${drink.name}1")
        measuredIngredient.patronName = "name"
        measuredIngredient.measure = "323"
        drink.ingredientList.add(measuredIngredient)
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
        drink2.ingredientList = RealmList(MeasuredIngredient("${drink.name}1"),
                MeasuredIngredient("${drink.name}2"),
                MeasuredIngredient("${drink.name}3"))
        drink2.imageUrl = "2"
        drink2.category = "2"
        drink2.alcoholicType = "2"
        drink2.glassType = "2"
        drink2.instructions = "2"

        drink.fillEmptyFields(drink2)
        assertTrue(drink == drink2)
    }
}