package com.avloga.budgetik.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import kotlin.math.abs
import kotlin.math.floor

object MoneyUtils {
    private val decimalFormatSymbols: DecimalFormatSymbols = DecimalFormatSymbols().apply {
        decimalSeparator = ','
        groupingSeparator = ' '
    }

    private val twoDecimalFormatter: DecimalFormat = DecimalFormat("0.00").apply {
        decimalFormatSymbols = this@MoneyUtils.decimalFormatSymbols
        isGroupingUsed = false
    }

    /**
     * Formats a monetary [amount] to two decimals using comma as decimal separator,
     * without rounding (truncates extra fractional digits). Examples:
     * 100.0 -> "100,00", 100.569 -> "100,56", -42.1 -> "42,10".
     * The sign (if needed) should be added by the caller.
     */
    fun formatMoneyTruncated(amount: Double): String {
        val absoluteAmount = abs(amount)
        val truncated = floor(absoluteAmount * 100.0) / 100.0
        synchronized(twoDecimalFormatter) {
            return twoDecimalFormatter.format(truncated)
        }
    }

    /**
     * Same as [formatMoneyTruncated] but preserves a leading minus sign for negative values.
     * Does not add a plus sign for positives.
     */
    fun formatMoneyTruncatedWithSign(amount: Double): String {
        val core = formatMoneyTruncated(amount)
        return if (amount < 0) "-$core" else core
    }
}


