package com.android.oleksandrpriadko.demo.cocktails.drinkdetails

import com.android.oleksandrpriadko.loggalitic.LogPublishService

class MeasureUnitsConverter {

    companion object {
        fun convert(amountAndUnitString: String, toMeasureUnit: MeasureUnit): String {
//            val validUnit = findValidUnit(amountAndUnitString)
//            if (validUnit != null) {
//                val parts = amountAndUnitString.split(validUnit.unitName, ignoreCase = true)
//                if (parts.isNotEmpty()) {
//                    val amount: Float
//                    try {
//                        amount = parseToFloat(parts[0].toLowerCase())
//
//                    } catch (e: NumberFormatException) {
//                        logState("${parts[0]} cannot be parsed")
//                        return amountAndUnitString
//                    }
//                    val mlInAmount = amount * validUnit.mlInUnit
//                    val requiredUnitInAmount = mlInAmount / toMeasureUnit.mlInUnit
//                    return "$requiredUnitInAmount ${toMeasureUnit.unitName}"
//                } else {
//                    logState("no parts in $amountAndUnitString")
//                }
//            } else {
//                logState("unit not found in $amountAndUnitString")
//            }
            return amountAndUnitString
        }

        fun findValidUnit(amountAndUnitString: String): MeasureUnit {
            MeasureUnit.values().forEach {
                if (amountAndUnitString.toLowerCase().contains(it.unitName, true)) {
                    return it
                }
            }
            return MeasureUnit.AS_IS
        }

        private fun parseToFloat(ratio: String): Float {
            return if (ratio.contains("/")) {
                val rat = ratio.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                rat[0].toFloat() / rat[1].toFloat()
            } else if (ratio.contains("-")){
                return 4f
            } else {
                ratio.toFloat()
            }
        }

        private fun logState(message: String) {
            LogPublishService.logger().e(MeasureUnitsConverter::class.java.simpleName, message)
        }

    }
}

enum class MeasureUnit(val unitName: String, val mlInUnit: Float) {
    AS_IS("", 1f),
    ML("ml", 1f),
    OZ("oz", 29.57f),
    CL("cl", 10f),
    SHOT("shot", 44f),
    SHOOT("shoot", 44f),
    TSP("tsp", 4.92f)
}