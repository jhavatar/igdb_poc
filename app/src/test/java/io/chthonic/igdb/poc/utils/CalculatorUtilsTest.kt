package io.chthonic.igdb.poc.utils

import io.chthonic.igdb.poc.data.model.CalculatorState
import io.chthonic.igdb.poc.data.model.CryptoCurrency
import io.chthonic.igdb.poc.data.model.ExchangeState
import io.chthonic.igdb.poc.data.model.Ticker
import org.junit.Assert
import org.junit.Test
import java.math.BigDecimal
import java.math.MathContext
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Created by jhavatar on 4/2/2018.
 */
class CalculatorUtilsTest {


    @Test
    fun testGetTicker() {
        val fooTicker = Ticker(10, "11", "12", "", "", CryptoCurrency.Bitcoin.code + "foo")
        val barTicker = Ticker(14, "15", "16", "", "", CryptoCurrency.Bitcoin.code + "bar")
        val tickers = mapOf(Pair(fooTicker.code, fooTicker),
                Pair(barTicker.code, barTicker))
        val exchangeState = ExchangeState(tickers)

        var calcState = CalculatorState(barTicker.code, true, BigDecimal(100))
        assertEquals(CalculatorUtils.getRightTicker(calcState, exchangeState = exchangeState ), barTicker)

        calcState = CalculatorState(fooTicker.code, true, BigDecimal(100))
        assertEquals(CalculatorUtils.getRightTicker(calcState, exchangeState = exchangeState), fooTicker)

        calcState = CalculatorState(null, true, BigDecimal(100))
        assertNull(CalculatorUtils.getRightTicker(calcState, exchangeState = exchangeState))

        calcState = CalculatorState("unknown", true, BigDecimal(100))
        assertNull(CalculatorUtils.getRightTicker(calcState, exchangeState = exchangeState))
    }

    @Test
    fun testGetBitcoinPrice() {
        val fooTicker = Ticker(10, "11", "12", "", "", CryptoCurrency.Bitcoin.code + "foo")
        val barTicker = Ticker(14, "15", "16", "", "", CryptoCurrency.Bitcoin.code + "bar")
        val tickers = mapOf(Pair(fooTicker.code, fooTicker),
                Pair(barTicker.code, barTicker))
        val exchangeState = ExchangeState(tickers)

        // no conversion required
        var calcState = CalculatorState("bar", true, BigDecimal(100))
        var bigDecimalResult = CalculatorUtils.getBitcoinPrice(calcState, exchangeState)
        assertEquals(bigDecimalResult, calcState.source)

        // convert from fiat foo
        calcState = CalculatorState(fooTicker.code, false, BigDecimal(100))
        bigDecimalResult = CalculatorUtils.getBitcoinPrice(calcState, exchangeState)
        assertEquals(bigDecimalResult, calcState.source.divide(fooTicker.ask.toBigDecimal(), MathContext.DECIMAL128))

        // convert from fiat bar
        calcState = CalculatorState(barTicker.code, false, BigDecimal(100))
        bigDecimalResult = CalculatorUtils.getBitcoinPrice(calcState, exchangeState)
        assertEquals(bigDecimalResult, calcState.source.divide(barTicker.ask.toBigDecimal(), MathContext.DECIMAL128))

        // convert from non existing fiat
        calcState = CalculatorState("fus", false, BigDecimal(100))
        assertNull(CalculatorUtils.getBitcoinPrice(calcState, exchangeState))

        // convert from from fiat with none selected
        calcState = CalculatorState(null, false, BigDecimal(100))
        assertNull(CalculatorUtils.getBitcoinPrice(calcState, exchangeState))
    }


    @Test
    fun testGetFiatPrice() {
        val fooTicker = Ticker(10, "11", "12", "", "", CryptoCurrency.Bitcoin.code + "foo")
        val barTicker = Ticker(14, "15", "16", "", "", CryptoCurrency.Bitcoin.code + "bar")
        val source = BigDecimal(100)
        val fooBitcoin =  ExchangeUtils.convertToBitcoin(source, fooTicker)
        val barBitcoin =  ExchangeUtils.convertToBitcoin(source, barTicker)
        val tickers = mapOf(Pair(fooTicker.code, fooTicker),
                Pair(barTicker.code, barTicker))
        val exchangeState = ExchangeState(tickers)

        // no conversion required
        var calcState = CalculatorState(fooTicker.code, false, source)
        var bigDecimalResult = CalculatorUtils.getFiatPrice(fooTicker, calcState, exchangeState)
        assertEquals(bigDecimalResult, calcState.source)

        // convert directly from bitcoin (selected)
        calcState = CalculatorState(fooTicker.code, true, source)
        bigDecimalResult = CalculatorUtils.getFiatPrice(fooTicker, calcState, exchangeState)
        assertEquals(bigDecimalResult, ExchangeUtils.convertFromBitcoin(calcState.source, fooTicker))

        // convert directly from bitcoin (not selected)
        calcState = CalculatorState(barTicker.code, true, source)
        bigDecimalResult = CalculatorUtils.getFiatPrice(fooTicker, calcState, exchangeState)
        assertEquals(bigDecimalResult, ExchangeUtils.convertFromBitcoin(calcState.source, fooTicker))

        // convert to bitcoin then to foo fiat
        calcState = CalculatorState(barTicker.code, false, source)
        bigDecimalResult = CalculatorUtils.getFiatPrice(fooTicker, calcState, exchangeState)
        assertEquals(bigDecimalResult, barBitcoin.multiply(fooTicker.bid.toBigDecimal()))

        // convert to bitcoin then to bar fiat
        calcState = CalculatorState(fooTicker.code, false, source)
        bigDecimalResult = CalculatorUtils.getFiatPrice(barTicker, calcState, exchangeState)
        assertEquals(bigDecimalResult, fooBitcoin.multiply(barTicker.bid.toBigDecimal()))

        // unknown fiat selected to convert from
        calcState = CalculatorState("fus", false, BigDecimal(100))
        assertNull(CalculatorUtils.getFiatPrice(fooTicker, calcState, exchangeState))

        // no fiat selected to convert from
        calcState = CalculatorState(null, false, BigDecimal(100))
        Assert.assertNull(CalculatorUtils.getFiatPrice(fooTicker, calcState, exchangeState))
    }
}