package io.chthonic.igdb.poc.utils

import io.chthonic.igdb.poc.BuildConfig
import io.chthonic.igdb.poc.data.model.CurrencyRealtimeFormatInput
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLog
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue


/**
 * Created by jhavatar on 4/2/2018.
 */

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class)
class TextUtilsTest {

    @Before
    fun before() {
        ShadowLog.stream = System.out
    }

    @Test
    fun testFormatCurrency() {
        var amount:BigDecimal? = null
        val fallback = "foo"

        // test fallback
        assertEquals(TextUtils.formatCurrency(amount, fallback, true), fallback)
        assertEquals(TextUtils.formatCurrency(amount, fallback, false), fallback)

        // test fiat
        amount = 0.0002.toBigDecimal()
        var result: String = TextUtils.formatCurrency(amount, fallback, false)
        assertEquals(result, "0.00")

        amount = 0.002.toBigDecimal()
        result = TextUtils.formatCurrency(amount, fallback, false)
        assertEquals(result, "0.00")

        amount = 0.00.toBigDecimal()
        result = TextUtils.formatCurrency(amount, fallback, false)
        assertEquals(result, "0.00")

        amount = 0.02.toBigDecimal()
        result = TextUtils.formatCurrency(amount, fallback, false)
        assertEquals(result, "0.02")

        amount = 0.toBigDecimal()
        result = TextUtils.formatCurrency(amount, fallback, false)
        assertEquals(result, "0.00")

        amount = 1.toBigDecimal()
        result = TextUtils.formatCurrency(amount, fallback, false)
        assertEquals(result, "1.00")

        amount = 9.99.toBigDecimal()
        result = TextUtils.formatCurrency(amount, fallback, false)
        assertEquals(result, "9.99")

        amount = 123.toBigDecimal()
        result = TextUtils.formatCurrency(amount, fallback, false)
        assertEquals(result, "123.00")

        amount = 1234.toBigDecimal()
        result = TextUtils.formatCurrency(amount, fallback, false)
        assertEquals(result, "1 234.00")

        amount = 123456.toBigDecimal()
        result = TextUtils.formatCurrency(amount, fallback, false)
        assertEquals(result, "123 456.00")

        amount = 1234567.toBigDecimal()
        result = TextUtils.formatCurrency(amount, fallback, false)
        assertEquals(result, "1 234 567.00")


        // test crypto
        amount = 0.000000002.toBigDecimal()
        result = TextUtils.formatCurrency(amount, fallback, true)
        assertEquals(result, "0.00")

        amount = 0.00000002.toBigDecimal()
        result = TextUtils.formatCurrency(amount, fallback, true)
        assertEquals(result, "0.00000002")

        amount = 0.0002.toBigDecimal()
        result = TextUtils.formatCurrency(amount, fallback, true)
        assertEquals(result, "0.0002")

        amount = 0.002.toBigDecimal()
        result = TextUtils.formatCurrency(amount, fallback, true)
        assertEquals(result, "0.002")

        amount = 0.00.toBigDecimal()
        result = TextUtils.formatCurrency(amount, fallback, true)
        assertEquals(result, "0.00")

        amount = 0.02.toBigDecimal()
        result = TextUtils.formatCurrency(amount, fallback, true)
        assertEquals(result, "0.02")

        amount = 0.toBigDecimal()
        result = TextUtils.formatCurrency(amount, fallback, true)
        assertEquals(result, "0.00")

        amount = 1.toBigDecimal()
        result = TextUtils.formatCurrency(amount, fallback, false)
        assertEquals(result, "1.00")

        amount = 9.99.toBigDecimal()
        result = TextUtils.formatCurrency(amount, fallback, false)
        assertEquals(result, "9.99")
    }


    @Test
    fun testDeFormatCurrency() {
        assertEquals(TextUtils.deFormatCurrency(""), "")
        assertEquals(TextUtils.deFormatCurrency(" "), "")
        assertEquals(TextUtils.deFormatCurrency("          "), "")
        assertEquals(TextUtils.deFormatCurrency(TextUtils.PLACE_HOLDER_STRING), "")
        assertEquals(TextUtils.deFormatCurrency("${TextUtils.PLACE_HOLDER_STRING}${TextUtils.PLACE_HOLDER_STRING}${TextUtils.PLACE_HOLDER_STRING}"), "")

        assertEquals(TextUtils.deFormatCurrency("abcdefg"), "")
        assertEquals(TextUtils.deFormatCurrency("a bc defg"), "")
        assertEquals(TextUtils.deFormatCurrency("ABCDEFG"), "")
        assertEquals(TextUtils.deFormatCurrency("a Bc DEFG"), "")
        assertEquals(TextUtils.deFormatCurrency(TextUtils.TOO_MANY_DIGITS_MSG), "")
        assertEquals(TextUtils.deFormatCurrency("${TextUtils.TOO_MANY_DIGITS_MSG}${TextUtils.TOO_MANY_DIGITS_MSG}${TextUtils.TOO_MANY_DIGITS_MSG}"), "")

        assertEquals(TextUtils.deFormatCurrency("0.00000000"), "0.00000000")
        assertEquals(TextUtils.deFormatCurrency("0.00"), "0.00")
        assertEquals(TextUtils.deFormatCurrency("1"), "1")
        assertEquals(TextUtils.deFormatCurrency("123.00"), "123.00")
        assertEquals(TextUtils.deFormatCurrency("1 234.00"), "1234.00")
        assertEquals(TextUtils.deFormatCurrency("1 234 567.00"), "1234567.00")
        assertEquals(TextUtils.deFormatCurrency("123.45"), "123.45")

        assertEquals(TextUtils.deFormatCurrency("01.23"), "01.23")
    }


    @Test
    fun testGetDateTimeString() {
        var dateTime = "13:12:20"
        var date = TextUtils.timeFormatter.parse(dateTime)
        assertEquals(TextUtils.getDateString(date.time), dateTime)

        dateTime = "2001-02-03"
        date = TextUtils.dateFormatter.parse(dateTime)
        assertEquals(TextUtils.getDateString(date.time), dateTime)
    }

    @Test
    fun testIsCurrencyInWarningState() {
        assertFalse(TextUtils.isCurrencyInWarningState(null))
        assertFalse(TextUtils.isCurrencyInWarningState(""))
        assertFalse(TextUtils.isCurrencyInWarningState("foo"))
        assertFalse(TextUtils.isCurrencyInWarningState("12.34"))

        assertTrue(TextUtils.isCurrencyInWarningState(TextUtils.PLACE_HOLDER_STRING))
        assertTrue(TextUtils.isCurrencyInWarningState(TextUtils.TOO_MANY_DIGITS_MSG))
    }

    @Test
    fun testWasCurrencyInWarningState() {
        assertFalse(TextUtils.wasCurrencyWarningState(null, true))
        assertFalse(TextUtils.wasCurrencyWarningState("", true))
        assertFalse(TextUtils.wasCurrencyWarningState("foo", true))
        assertFalse(TextUtils.wasCurrencyWarningState("12.34", true))

        assertFalse(TextUtils.wasCurrencyWarningState(TextUtils.PLACE_HOLDER_STRING, false))
        assertFalse(TextUtils.wasCurrencyWarningState(TextUtils.TOO_MANY_DIGITS_MSG, false))
        assertTrue(TextUtils.wasCurrencyWarningState(TextUtils.PLACE_HOLDER_STRING, true))
        assertTrue(TextUtils.wasCurrencyWarningState(TextUtils.TOO_MANY_DIGITS_MSG, true))
    }



    @Test
    fun testRealtimeFormatInput() {
        var input = CurrencyRealtimeFormatInput.factoryReset()

        // warning states
        input = input.copy(s = TextUtils.PLACE_HOLDER_STRING)
        var output = TextUtils.realtimeFormatInput(input)
        assertTrue(output.doNothing)

        input = input.copy(sPrev = TextUtils.TOO_MANY_DIGITS_MSG)
        output = TextUtils.realtimeFormatInput(input)
        assertTrue(output.doNothing)

        // fallback states
        input = input.copy(s="",
                sPrev = "0.00",
                delAction = false)
        output = TextUtils.realtimeFormatInput(input)
        assertFalse(output.doNothing)
        assertFalse(output.revert)
        assertEquals(output.sRaw, TextUtils.FALLBACK_CURRENCY_STRING)
        assertEquals(output.sFormatted, TextUtils.FALLBACK_CURRENCY_STRING)

        input = input.copy(s="123.45",
                sPrev = TextUtils.PLACE_HOLDER_STRING,
                delAction = true)
        output = TextUtils.realtimeFormatInput(input)
        assertFalse(output.doNothing)
        assertFalse(output.revert)
        assertEquals(output.sRaw, TextUtils.FALLBACK_CURRENCY_STRING)
        assertEquals(output.sFormatted, TextUtils.FALLBACK_CURRENCY_STRING)

        input = input.copy(s="123.45",
                sPrev = TextUtils.TOO_MANY_DIGITS_MSG,
                delAction = true)
        output = TextUtils.realtimeFormatInput(input)
        assertFalse(output.doNothing)
        assertFalse(output.revert)
        assertEquals(output.sRaw, TextUtils.FALLBACK_CURRENCY_STRING)
        assertEquals(output.sFormatted, TextUtils.FALLBACK_CURRENCY_STRING)

        input = input.copy(s="foo",
                sPrev = "0.00",
                delAction = false)
        output = TextUtils.realtimeFormatInput(input)
        assertFalse(output.doNothing)
        assertFalse(output.revert)
        assertEquals(output.sRaw, TextUtils.FALLBACK_CURRENCY_STRING)
        assertEquals(output.sFormatted, TextUtils.FALLBACK_CURRENCY_STRING)


        // del non numeric
        input = input.copy(s="12345",
                sPrev = "123.45",
                changed = ".",
                delAction = true,
                caretPos = 4)
        output = TextUtils.realtimeFormatInput(input)
        assertFalse(output.doNothing)
        assertTrue(output.revert)
        assertEquals(output.sRaw, "123.45")
        assertEquals(output.sFormatted, input.sPrev)
        assertEquals(output.caretPos, 3)

        input = input.copy(s="1234.56",
                sPrev = "1 234.56",
                changed = " ",
                delAction = true,
                caretPos = 2)
        output = TextUtils.realtimeFormatInput(input)
        assertFalse(output.doNothing)
        assertTrue(output.revert)
        assertEquals(output.sRaw, "1234.56")
        assertEquals(output.sFormatted, input.sPrev)
        assertEquals(output.caretPos, 1)




        // add group separator
        input = input.copy(s="1234.56",
                sPrev = "234.56",
                changed = "1",
                delAction = false,
                caretPos = 0)
        output = TextUtils.realtimeFormatInput(input)
        assertFalse(output.doNothing)
        assertFalse(output.revert)
        assertEquals(output.sRaw, "1234.56")
        assertEquals(output.sFormatted, "1${TextUtils.GROUPING_SEPARATOR}234.56")
        assertEquals(output.caretPos, 2)

        input = input.copy(s="1 2347.56",
                sPrev = "1 234.56",
                changed = "7",
                delAction = false,
                caretPos = 5)
        output = TextUtils.realtimeFormatInput(input)
        assertFalse(output.doNothing)
        assertFalse(output.revert)
        assertEquals(output.sRaw, "12347.56")
        assertEquals(output.sFormatted, "12${TextUtils.GROUPING_SEPARATOR}347.56")
        assertEquals(output.caretPos, 6)


        input = input.copy(s="123 4596.78",
                sPrev = "123 456.78",
                changed = "9",
                delAction = false,
                caretPos = 6)
        output = TextUtils.realtimeFormatInput(input)
        assertFalse(output.doNothing)
        assertFalse(output.revert)
        assertEquals(output.sRaw, "1234596.78")
        assertEquals(output.sFormatted, "1${TextUtils.GROUPING_SEPARATOR}234${TextUtils.GROUPING_SEPARATOR}596.78")
        assertEquals(output.caretPos, 8)


        // keep minimal currency format
        input = input.copy(s=".00",
                sPrev = "1.00",
                changed = "1",
                delAction = true,
                caretPos = 1)
        output = TextUtils.realtimeFormatInput(input)
        assertFalse(output.doNothing)
        assertFalse(output.revert)
        assertEquals(output.sRaw, "0.00")
        assertEquals(output.sFormatted, "0.00")
        assertEquals(output.caretPos, 0)

        input = input.copy(s="1.0",
                sPrev = "1.00",
                changed = "0",
                delAction = true,
                caretPos = 4)
        output = TextUtils.realtimeFormatInput(input)
        assertFalse(output.doNothing)
        assertFalse(output.revert)
        assertEquals(output.sRaw, "1.00")
        assertEquals(output.sFormatted, "1.00")
        assertEquals(output.caretPos, 3)


        // formatted longer than max_length
        input = input.copy(s="1234.00",
                sPrev = "123.00",
                changed = "4",
                delAction = false,
                caretPos = 3,
                maxLength = 7)
        output = TextUtils.realtimeFormatInput(input)
        assertFalse(output.doNothing)
        assertTrue(output.revert)
        assertEquals(output.sRaw, "123.00")
        assertEquals(output.sFormatted, "123.00")
        assertEquals(output.caretPos, 3)

        input = input.copy(s="123 456 7891.00",
                sPrev = "123 456 789.00",
                changed = "1",
                delAction = false,
                caretPos = 11,
                maxLength = 15)
        output = TextUtils.realtimeFormatInput(input)
        assertFalse(output.doNothing)
        assertTrue(output.revert)
        assertEquals(output.sRaw, "123456789.00")
        assertEquals(output.sFormatted, "123 456 789.00")
        assertEquals(output.caretPos, 11)


        // keep caret on same side of decimal
        input = input.copy(s="01.00",
                sPrev = "0.00",
                changed = "1",
                delAction = false,
                caretPos = 1)
        output = TextUtils.realtimeFormatInput(input)
        assertFalse(output.doNothing)
        assertFalse(output.revert)
        assertEquals(output.sRaw, "1.00")
        assertEquals(output.sFormatted, "1.00")
        assertEquals(output.caretPos, 1)
    }
}