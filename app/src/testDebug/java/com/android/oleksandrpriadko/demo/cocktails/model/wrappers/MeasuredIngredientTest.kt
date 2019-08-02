package com.android.oleksandrpriadko.demo.cocktails.model.wrappers

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class MeasuredIngredientTest {

    private var measuredIngredient: MeasuredIngredient = MeasuredIngredient()

    @Before
    fun setUp() {
        measuredIngredient = MeasuredIngredient()
    }

    @Test
    fun hasEmptyFields() {
        assertTrue(measuredIngredient.hasEmptyFields())

        measuredIngredient.id = "id"
        assertTrue(measuredIngredient.hasEmptyFields())

        measuredIngredient.measure = "123"
        assertTrue(measuredIngredient.hasEmptyFields())

        measuredIngredient.patronName = "patron"
        assertFalse(measuredIngredient.hasEmptyFields())

        assertTrue(measuredIngredient.hasEmptyFields(true))

        measuredIngredient.measureUnitAsString = "test"
        assertFalse(measuredIngredient.hasEmptyFields())
    }

    @Test
    fun fillEmptyFields() {
        val measuredIngredient2 = MeasuredIngredient("id2")
        measuredIngredient2.measure = "m2"
        measuredIngredient2.patronName = "p2"
        measuredIngredient2.measureUnitAsString = "mu2"

        measuredIngredient = MeasuredIngredient("")

        measuredIngredient.fillEmptyFields(measuredIngredient2)

        assertEquals(measuredIngredient2, measuredIngredient)
    }
}