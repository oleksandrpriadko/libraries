package com.android.oleksandrpriadko.demo.cocktails.model.wrappers

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class IngredientTest {

    private var ingredient: Ingredient = Ingredient()

    @Before
    fun setUp() {
        ingredient = Ingredient()
    }

    @Test
    fun hasEmptyFields() {
        assertTrue(ingredient.hasEmptyFields())

        ingredient.id = "id"
        assertTrue(ingredient.hasEmptyFields())

        ingredient.name = "name"
        assertTrue(ingredient.hasEmptyFields())

        ingredient.imageUrl = "url"
        assertTrue(ingredient.hasEmptyFields())

        ingredient.description = Ingredient.NO_DESCRIPTION
        assertTrue(ingredient.hasEmptyFields())

        ingredient.description = "desc"
        assertTrue(ingredient.hasEmptyFields())

        ingredient.type = Ingredient.NO_TYPE
        assertTrue(ingredient.hasEmptyFields())

        ingredient.type = "type"
        assertFalse(ingredient.hasEmptyFields())
    }

    @Test
    fun fillEmptyFields() {
        val ingredient2 = Ingredient()
        ingredient2.id = "2"
        ingredient2.name = "2"
        ingredient2.imageUrl = "2"
        ingredient2.type = "2"
        ingredient2.description = "2"

        ingredient.fillEmptyFields(ingredient2)
        assertTrue(ingredient == ingredient2)
    }
}