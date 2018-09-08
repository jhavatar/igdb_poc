package io.chthonic.igdb.poc.utils

import android.content.SharedPreferences
import com.squareup.moshi.JsonAdapter
import io.chthonic.igdb.poc.data.model.*
import timber.log.Timber
import java.math.BigDecimal
import java.math.MathContext

/**
 * Created by jhavatar on 3/25/2018.
 */
object ExchangeUtils {

    private const val PERSIST_KEY_NAME = "exchange_state"

    val BITCOIN_TICKER: Ticker by lazy {
        Ticker(timestamp = System.currentTimeMillis(),
                bid = "1.0",
                ask = "1.0",
                last_trade = "",
                rolling_24_hour_volume = "",
                pair = Currency.Bitcoin.code
        )
    }

    private val codeToCurrencyMap: Map<String, out Currency> by lazy {
        val m = mutableMapOf<String, Currency>()
        m.putAll(Currency.values.associateBy( {it.code}, {it} ))
        m
    }

    fun getPersistedExchangeState(prefs: SharedPreferences, serializer: JsonAdapter<ExchangeState>, fallbackState: ExchangeState): ExchangeState {
        Timber.d("getPersistedExchangeState")
        return try {
            val json = prefs.getString(PERSIST_KEY_NAME, null)
            Timber.d("getPersistedExchangeState: json = $json")
            if (json != null) {
                serializer.fromJson(json) ?: fallbackState

            } else {
                fallbackState
            }

        } catch (t: Throwable) {
            Timber.e(t,"getPersistedExchangeState failed")
            fallbackState
        }
    }


    fun setPersistedExchangeState(exchangeState: ExchangeState, prefs: SharedPreferences, serializer: JsonAdapter<ExchangeState>) {
        Timber.d("setPersistedExchangeState $exchangeState")
        try {
            val json = serializer.toJson(exchangeState)
            if (!json.isNullOrEmpty()) {
                prefs.edit().putString(PERSIST_KEY_NAME, json).apply()
            }

        } catch (t: Throwable) {
            Timber.e(t,"setPersistedExchangeState failed")
        }
    }


    fun getCurrencyForTicker(ticker: Ticker): Currency? {
        return getCurrencyForTicker(ticker.code)
    }

    fun getCurrencyForTicker(code: String): Currency? {
        return codeToCurrencyMap[code]
    }

    fun isSupportedCurrency(ticker: Ticker): Boolean {
        return getCurrencyForTicker(ticker) != null
    }

    fun convertFromBitcoin(bitcoinPrice: BigDecimal, ticker: Ticker): BigDecimal {
        return if (ticker.exchangeMappingFromBitcoin) {
            bitcoinPrice.multiply(ticker.bid.toBigDecimal())

        } else {
            bitcoinPrice.divide(ticker.ask.toBigDecimal(), MathContext.DECIMAL128)
        }
    }

    fun convertToBitcoin(tickerPrice: BigDecimal, ticker: Ticker): BigDecimal {
        Timber.d("convertToBitcoin: tickerPrice = $tickerPrice, ticker = $ticker")
        return if (ticker.exchangeMappingFromBitcoin) {
            tickerPrice.divide(ticker.ask.toBigDecimal(), MathContext.DECIMAL128)

        } else {
            tickerPrice.multiply(ticker.ask.toBigDecimal())
        }
    }
}