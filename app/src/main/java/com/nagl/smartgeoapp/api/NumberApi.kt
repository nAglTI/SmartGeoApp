package com.nagl.smartgeoapp.api

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
/**
 * round symbols
 */
class NumberApi {

    /**
     * round symbol
     *
     * @param number will round
     *
     * @param symbolsCount how many symbols round
     *
     * @return new rounded symbol
     */
    fun roundSomeSymbols(number: Double, symbolsCount: Int): Double {
        return if (number.isInfinite() || number.isNaN()) {
            0.0
        } else {
            BigDecimal(number).setScale(symbolsCount, RoundingMode.HALF_EVEN).toDouble()
        }
    }

    /**
     * format Number
     *
     * @param number will round
     *
     * @param fractionDigits
     *
     * @return new formatted Number
     */
    fun formatNumber(number: Double, fractionDigits: Int): String {
        val decimalFormat = DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH))
        decimalFormat.maximumFractionDigits = fractionDigits

        return decimalFormat.format(number)
    }

    /**
     * format Number
     *
     * @param number will round
     *
     * @param fractionDigits
     *
     * @return new formatted Number
     */
    fun formatNumberExactly(number: Number, fractionDigits: Int): String {
        return "%.${fractionDigits}f".format(number.toDouble()).replace(",", ".")
    }
}