package com.android.oleksandrpriadko.demo.cocktails.model

import com.android.oleksandrpriadko.demo.cocktails.model.wrappers.Drink
import com.android.oleksandrpriadko.demo.cocktails.model.wrappers.Drink.Companion.DRINK_FIELD_ID
import com.android.oleksandrpriadko.demo.cocktails.model.wrappers.Drink.Companion.DRINK_FIELD_NAME
import com.android.oleksandrpriadko.demo.cocktails.model.wrappers.Ingredient
import com.android.oleksandrpriadko.demo.cocktails.model.wrappers.Ingredient.Companion.INGREDIENT_FIELD_NAME
import com.android.oleksandrpriadko.demo.cocktails.model.wrappers.MeasuredIngredient
import com.android.oleksandrpriadko.demo.cocktails.model.wrappers.MeasuredIngredient.Companion.MEASURED_INGREDIENT_FIELD_ID
import com.android.oleksandrpriadko.mvp.repo_extension.RealmRepoExtension
import io.realm.Case
import io.realm.Realm
import io.realm.RealmResults

class CocktailsRealmRepoExtension : RealmRepoExtension() {

    fun saveIngredients(ingredientList: List<Ingredient>) {
        realm?.executeTransaction {
            for (donor in ingredientList) {
                var receiver = findIngredientLike(donor.name, it)

                receiver = prepareIngredientToUpdate(donor, receiver)

                it.copyToRealmOrUpdate(receiver)
            }
            logState("ingredientList copyToRealmOrUpdate")
        }
    }

    fun saveIngredient(donor: Ingredient) {
        realm?.executeTransactionAsync {
            var receiver = findIngredientLike(donor.name, it)

            receiver = prepareIngredientToUpdate(donor, receiver)

            it.copyToRealmOrUpdate(receiver)

            logState("$receiver copyToRealmOrUpdate")
        }
    }

    private fun prepareIngredientToUpdate(donor: Ingredient,
                                          receiver: Ingredient?): Ingredient {
        return when {
            receiver == null -> {
                logState("donor to be saved")
                donor
            }
            receiver.fillEmptyFields(donor) -> {
                logState("receiver updated and to be saved")
                receiver
            }
            else -> {
                logState("receiver NOT updated and to be saved")
                receiver
            }
        }
    }

    fun findIngredientContains(mustContainString: String): RealmResults<Ingredient>? {
        val found: RealmResults<Ingredient>? = realm?.where(Ingredient::class.java)
                ?.contains(INGREDIENT_FIELD_NAME, mustContainString, Case.INSENSITIVE)
                ?.findAll()
        logState("ingredientsList with name $mustContainString ${if ((found.isNullOrEmpty())) "not found" else "found"}")
        return found
    }

    private fun findIngredientLike(mustHaveNameString: String, realmPassed: Realm? = realm): Ingredient? {
        val found = realmPassed?.where(Ingredient::class.java)
                ?.like(INGREDIENT_FIELD_NAME, mustHaveNameString)
                ?.findFirst()
        return when (found) {
            null -> {
                logState("ingredient with name $mustHaveNameString not found")
                null
            }
            else -> {
                logState("ingredient with name $mustHaveNameString found")
                found
            }
        }
    }

    fun findIngredientLikeNullIfEmpty(mustHaveNameString: String): Ingredient? {
        val found = realm?.where(Ingredient::class.java)
                ?.like(INGREDIENT_FIELD_NAME, mustHaveNameString)
                ?.findFirst()
        return when {
            found == null || found.hasEmptyFields() -> {
                logState("ingredient with name $mustHaveNameString not found or not filled")
                logState(found?.toString() ?: "")
                null
            }
            else -> {
                logState("ingredient with name $mustHaveNameString found and filled")
                found
            }
        }
    }

    fun findIngredientLike(mustHaveNameString: String): Ingredient? {
        val found = realm?.where(Ingredient::class.java)
                ?.like(INGREDIENT_FIELD_NAME, mustHaveNameString)
                ?.findFirst()
        return when (found) {
            null -> {
                logState("ingredient with name $mustHaveNameString not found")
                logState(found?.toString() ?: "")
                null
            }
            else -> {
                logState("ingredient with name $mustHaveNameString found")
                found
            }
        }
    }

    fun findAllIngredients(): RealmResults<Ingredient>? {
        val found: RealmResults<Ingredient>? = realm?.where(Ingredient::class.java)?.findAll()
        logState("ingredients ${if ((found.isNullOrEmpty())) "not found" else "found"}")
        return found
    }

    private fun findMeasuredIngredientLike(mustHaveIdString: String, realmPassed: Realm? = realm): MeasuredIngredient? {
        val found = realmPassed?.where(MeasuredIngredient::class.java)
                ?.like(MEASURED_INGREDIENT_FIELD_ID, mustHaveIdString)
                ?.findFirst()
        return when (found) {
            null -> {
                logState("measured ingredient with id $mustHaveIdString not found")
                null
            }
            else -> {
                logState("measured ingredient with name $mustHaveIdString found")
                found
            }
        }
    }

    private fun prepareMeasuredIngredientToUpdate(donor: MeasuredIngredient,
                                                  receiver: MeasuredIngredient?): MeasuredIngredient {
        return when {
            receiver == null -> {
                logState("donor to be saved")
                donor
            }
            receiver.fillEmptyFields(donor) -> {
                logState("receiver updated and to be saved")
                receiver
            }
            else -> {
                logState("receiver NOT updated and to be saved")
                receiver
            }
        }
    }

    fun saveDrink(donor: Drink) {
        realm?.executeTransactionAsync {
            var receiver = findDrinkById(donor.id, it)

            receiver = prepareDrinkToUpdate(donor, receiver)

            it.copyToRealmOrUpdate(receiver)

            logState("$receiver copyToRealmOrUpdate")
        }
    }

    fun saveDrinks(drinkList: List<Drink>) {
        realm?.executeTransaction {
            for (donor in drinkList) {
                var receiver = findDrinkById(donor.id, it)

                receiver = prepareDrinkToUpdate(donor, receiver)

                it.copyToRealmOrUpdate(receiver)
            }
            logState("drinkList copyToRealmOrUpdate")
        }
    }

    private fun prepareDrinkToUpdate(donor: Drink,
                                     receiver: Drink?): Drink {
        return when {
            receiver == null -> {
                logState("donor to be saved")
                donor
            }
            receiver.fillEmptyFields(donor) -> {
                logState("receiver updated and to be saved")
                receiver
            }
            else -> {
                logState("receiver NOT updated and to be saved")
                receiver
            }
        }
    }

    fun findDrinkContains(mustContainString: String): RealmResults<Drink>? {
        val found: RealmResults<Drink>? = realm?.where(Drink::class.java)
                ?.contains(DRINK_FIELD_NAME, mustContainString, Case.INSENSITIVE)
                ?.findAll()
        logState("drinksList with name $mustContainString ${if (found.isNullOrEmpty()) "not found" else "found"}")
        return found
    }

    fun findDrinkById(id: String, passedRealm: Realm? = realm): Drink? {
        val found: Drink? = passedRealm?.where(Drink::class.java)
                ?.like(DRINK_FIELD_ID, id)
                ?.findFirst()
        logState("drink with id $id ${if (found == null) "not found" else "found"}")
        return found
    }

    fun findDrinkByIdNullIfEmpty(id: String): Drink? {
        val found = realm?.where(Drink::class.java)
                ?.like(DRINK_FIELD_ID, id)
                ?.findFirst()
        return when {
            found == null || found.hasEmptyFields() -> {
                logState("drink with id $id not found or not filled")
                null
            }
            else -> {
                logState("drink with id $id found and filled")
                found
            }
        }
    }

    override fun enableLog(): Boolean {
        return false
    }
}