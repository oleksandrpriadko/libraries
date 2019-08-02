package com.android.oleksandrpriadko.demo.cocktails.model.wrappers

import com.android.oleksandrpriadko.loggalitic.LogPublishService
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Ingredient(@SerializedName(INGREDIENT_FIELD_NAME)
                      @PrimaryKey
                      var name: String) : RealmObject() {
    @SerializedName(INGREDIENT_FIELD_ID)
    var id: String = ""
    var imageUrl: String = ""
    var description: String = NO_DESCRIPTION
    var type: String = NO_TYPE

    constructor() : this("")

    fun hasEmptyFields(): Boolean {
        return name.isEmpty()
                .or(imageUrl.isEmpty())
                .or(id.isEmpty())
                .or(description.isEmpty())
                .or(description.equals(NO_DESCRIPTION, true))
                .or(type.isEmpty())
                .or(type.equals(NO_TYPE, true))
    }

    fun fillEmptyFields(donor: Ingredient?): Boolean {
        if (donor == null) {
            return false
        }

        var isChanged = false

        if (isFieldCanBeChanged(name, donor.name)) {
            logState("$name to be changed to donor name = ${donor.name}")
            name = donor.name
            if (!isChanged) {
                isChanged = true
            }
        }
        if (isFieldCanBeChanged(id, donor.id)) {
            logState("$id to be changed to donor id = ${donor.id}")
            id = donor.id
            if (!isChanged) {
                isChanged = true
            }
        }
        if (isFieldCanBeChanged(imageUrl, donor.imageUrl)) {
            logState("imageUrl to be changed to donor imageUrl")
            imageUrl = donor.imageUrl
            if (!isChanged) {
                isChanged = true
            }
        }
        if (isFieldCanBeChanged(description, donor.description)) {
            logState("description to be changed to donor description")
            description = donor.description
            if (!isChanged) {
                isChanged = true
            }
        }
        if (isFieldCanBeChanged(type, donor.type)) {
            logState("$type to be changed to donor type = ${donor.type}")
            type = donor.type
            if (!isChanged) {
                isChanged = true
            }
        }

        return isChanged
    }

    private fun isFieldCanBeChanged(receiver: String, donor: String): Boolean {
        val isDonorValid = donor.isNotEmpty()
        val isReceiverEligible = receiver.isEmpty()
                .or(receiver.equals(NO_DESCRIPTION, true))
                .or(receiver.equals(NO_TYPE, true))

        return isDonorValid.and(isReceiverEligible) && !receiver.equals(donor, true)
    }

    private fun logState(message: String) {
        if (false) {
            LogPublishService.logger().d(this::class.java.simpleName, message)
        }
    }

    override fun toString(): String {
        return "Ingredient(name='$name', " +
                "id='$id', " +
                "imageUrl='$imageUrl', " +
                "description='${description.isNotEmpty()}', " +
                "type='$type')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Ingredient) return false

        if (name != other.name) return false
        if (id != other.id) return false
        if (imageUrl != other.imageUrl) return false
        if (description != other.description) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + imageUrl.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }


    companion object {
        const val INGREDIENT_FIELD_NAME = "name"
        const val INGREDIENT_FIELD_ID = "id"

        const val NO_DESCRIPTION = "No description"
        const val NO_TYPE = "Undefined"
    }
}