package io.chthonic.igdb.poc.utils

import io.chthonic.igdb.poc.data.model.CryptoCurrency
import io.chthonic.igdb.poc.data.model.FiatCurrency
import io.chthonic.igdb.poc.data.model.Ticker
import org.junit.Test
import java.math.BigDecimal
import java.math.MathContext
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue


/**
 * Created by jhavatar on 3/25/2018.
 */
//@RunWith(RobolectricTestRunner::class)
class ExchangeUtilsTest {

    @Test
    fun testGetCurrencyForTicker() {
        FiatCurrency.values.forEach { fiat ->
            val currency = ExchangeUtils.getCurrencyForTicker(Ticker(pair = CryptoCurrency.Bitcoin.code + fiat.code,
                    timestamp = 1,
                    ask = "",
                    bid = "",
                    rolling_24_hour_volume = "",
                    last_trade = ""))
            println("testGetCurrencyForTicker: currency = $currency, fiat = $fiat, code = ${fiat.code}")
            assertEquals(currency, fiat)
        }

        CryptoCurrency.values.forEach { crypto ->
            val currency = ExchangeUtils.getCurrencyForTicker(Ticker(pair = crypto.code + CryptoCurrency.Bitcoin.code,
                    timestamp = 1,
                    ask = "",
                    bid = "",
                    rolling_24_hour_volume = "",
                    last_trade = ""))
            println("testGetCurrencyForTicker: currency = $currency, crypto = $crypto, code = ${crypto.code}")
            assertEquals(currency, crypto)
        }

        assertNull(ExchangeUtils.getCurrencyForTicker(Ticker(pair = CryptoCurrency.Bitcoin.code + "foo",
                timestamp = 1,
                ask = "",
                bid = "",
                rolling_24_hour_volume = "",
                last_trade = "")))
    }


    @Test
    fun testIsSupportedFiatCurrency() {
        FiatCurrency.values.forEach { fiat ->
            assertTrue(ExchangeUtils.isSupportedCurrency(Ticker(pair = CryptoCurrency.Bitcoin.code + fiat.code,
                    timestamp = 1,
                    ask = "",
                    bid = "",
                    rolling_24_hour_volume = "",
                    last_trade = "")))
        }

        CryptoCurrency.values.forEach { crypto ->
            assertTrue(ExchangeUtils.isSupportedCurrency(Ticker(pair = crypto.code + CryptoCurrency.Bitcoin.code,
                    timestamp = 1,
                    ask = "",
                    bid = "",
                    rolling_24_hour_volume = "",
                    last_trade = "")))
        }

        assertFalse(ExchangeUtils.isSupportedCurrency(Ticker(pair = CryptoCurrency.Bitcoin.code + "foo",
                timestamp = 1,
                ask = "",
                bid = "",
                rolling_24_hour_volume = "",
                last_trade = "")))
    }


    @Test
    fun testConvertFromBitcoin() {
        val ticker = Ticker(10, "10", "11", "12", "foo", "bar")
        val fiatVal = ExchangeUtils.convertFromBitcoin(BigDecimal(100), ticker)
        assertEquals(fiatVal, BigDecimal(100).multiply(ticker.bid.toBigDecimal()))
    }


    @Test
    fun testConvertToBitcoin() {
        val ticker = Ticker(10, "10", "11", "12", "foo", "bar")
        val fiatVal = ExchangeUtils.convertToBitcoin(BigDecimal(100), ticker)
        assertEquals(fiatVal, BigDecimal(100).divide(ticker.ask.toBigDecimal(), MathContext.DECIMAL128))
    }

}