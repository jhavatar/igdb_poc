package io.chthonic.igdb.poc.utils

import android.text.format.DateUtils
import android.util.SparseArray
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by jhavatar on 4/2/2018.
 */
object TextUtils {
    const val PLACE_HOLDER_STRING: String = "\u2014"
    const val DECIMAL_SEPARATOR : Char = '.'
    const val DECIMAL_SEPARATOR_STRING = DECIMAL_SEPARATOR.toString()
    const val GROUPING_SEPARATOR: Char = ' '
    const val GROUPING_SEPARATOR_STRING = GROUPING_SEPARATOR.toString()
    const val TOO_MANY_DIGITS_MSG = "MAX"
    const val FALLBACK_CURRENCY_STRING = "0.00"
    const val BASE_DECIMAL_FORMAT = "###,##0.00"

    internal val timeFormatter: SimpleDateFormat by lazy {
        SimpleDateFormat("HH:mm:ss")
    }

    internal val dateFormatter: SimpleDateFormat by lazy {
        SimpleDateFormat("MMM, yyyy")
    }

    private val decimalFormatterList: SparseArray<DecimalFormat> = SparseArray()

    private val currencyFormatReplaceRegex: Regex by lazy {
        """[$GROUPING_SEPARATOR_STRING$PLACE_HOLDER_STRING[a-zA-Z]]""".toRegex()
    }

    fun isCurrencyInWarningState(s: String?): Boolean {
        return (s == TextUtils.PLACE_HOLDER_STRING) || (s == TextUtils.TOO_MANY_DIGITS_MSG)
    }

    fun wasCurrencyWarningState(prevS: String?, delAction: Boolean): Boolean {
        return delAction && ((prevS == TextUtils.PLACE_HOLDER_STRING) || (prevS == TextUtils.TOO_MANY_DIGITS_MSG))
    }

    private fun createDecimalFormatter(decimalDigits: Int): DecimalFormat {
        val pattern = if (decimalDigits <= 2) {
            BASE_DECIMAL_FORMAT

        } else {
            "$BASE_DECIMAL_FORMAT${"#".repeat(decimalDigits - 2)}"
        }
        val formatSymbols = DecimalFormatSymbols(Locale.ENGLISH)
        formatSymbols.setDecimalSeparator(DECIMAL_SEPARATOR)
        formatSymbols.setGroupingSeparator(GROUPING_SEPARATOR)
        return DecimalFormat(pattern, formatSymbols)
    }

    private fun getDecimalFormatter(decimalDigits: Int): DecimalFormat {
        return synchronized(decimalFormatterList) {
            decimalFormatterList[decimalDigits] ?: decimalFormatterList.let {
                val formatter = createDecimalFormatter(decimalDigits)
                it.append(decimalDigits, formatter)
                formatter
            }
        }
    }

//    private val fiatCurrencyFormat: DecimalFormat by lazy {
//        val pattern = "###,##0.00"
//        val formatSymbols = DecimalFormatSymbols(Locale.ENGLISH)
//        formatSymbols.setDecimalSeparator(DECIMAL_SEPARATOR)
//        formatSymbols.setGroupingSeparator(GROUPING_SEPARATOR)
//        DecimalFormat(pattern, formatSymbols)
//    }
//
//    private  val cryptoCurrencyFormat: DecimalFormat by lazy {
//        val pattern = "###,##0.00${"#".repeat(BITCOIN_DECIMAL_DIGITS - 2)}"
//        val formatSymbols = DecimalFormatSymbols(Locale.ENGLISH)
//        formatSymbols.setDecimalSeparator('.')
//        formatSymbols.setGroupingSeparator(' ')
//        DecimalFormat(pattern, formatSymbols)
//    }


    fun deFormatCurrency(s: String): String {
//        Timber.d("deFormatCurrency: s = $s, result = ${s.replace(currencyFormatReplaceRegex, "")}")
        return s.replace(currencyFormatReplaceRegex, "")

    }

    fun getDateTimeString(time: Long): String {
        val date = Date(time)
        return if (DateUtils.isToday(time)) {
            timeFormatter.format(date)

        } else {
            dateFormatter.format(date)
        }
    }



}