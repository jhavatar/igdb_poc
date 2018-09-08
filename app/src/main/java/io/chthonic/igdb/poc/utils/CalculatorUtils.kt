package io.chthonic.igdb.poc.utils

import android.content.SharedPreferences
import com.squareup.moshi.JsonAdapter
import io.chthonic.igdb.poc.data.model.*
import timber.log.Timber
import java.math.BigDecimal

/**
 * Created by jhavatar on 4/2/2018.
 */
object CalculatorUtils {

    private const val PERSIST_KEY_NAME = "calculator_state"

    fun getPersistedCalculatorState(prefs: SharedPreferences, serializer: JsonAdapter<CalculatorSerializableState>, fallbackState: CalculatorState): CalculatorState {
        Timber.d("getPersistedCalculatorState")
        return try {
            val json = prefs.getString(PERSIST_KEY_NAME, null)
            Timber.d("getPersistedCalculatorState: json = $json")
            if (json != null) {
                serializer.fromJson(json)?.toCalculatorState() ?: fallbackState

            } else {
                fallbackState
            }

        } catch (t: Throwable) {
            Timber.e(t,"getPersistedCalculatorState failed")
            fallbackState
        }
    }

    fun setPersistedCalculatorState(calculatorState: CalculatorState, prefs: SharedPreferences, serializer: JsonAdapter<CalculatorSerializableState>) {
        Timber.d("setPersistedCalculatorState $calculatorState")
        try {
            val json = serializer.toJson(CalculatorSerializableState.fromCalculatorState(calculatorState))
            if (!json.isNullOrEmpty()) {
                prefs.edit().putString(PERSIST_KEY_NAME, json).apply()
            }

        } catch (t: Throwable) {
            Timber.e(t,"setPersistedCalculatorState failed")
        }
    }

    fun getRightTicker(calculatorState: CalculatorState, exchangeState: ExchangeState): Ticker? {
        return getRightTicker(calculatorState, exchangeState.tickers)
    }

    fun getRightTicker(calculatorState: CalculatorState, tickers: Map<String, Ticker>): Ticker? {
        return tickers.get(calculatorState.rightTickerCode)
    }

    fun getLeftTicker(calculatorState: CalculatorState, exchangeState: ExchangeState): Ticker? {
        return getLeftTicker(calculatorState, exchangeState.tickers)
    }

    fun getLeftTicker(calculatorState: CalculatorState, tickers: Map<String, Ticker>): Ticker? {
        return tickers.get(calculatorState.leftTickerCode)
    }

    fun getBitcoinPrice(calculatorState: CalculatorState, exchangeState: ExchangeState): BigDecimal? {
        return getBitcoinPrice(calculatorState, exchangeState.tickers)
    }

    fun getBitcoinPrice(calculatorState: CalculatorState, tickerMap: Map<String, Ticker>): BigDecimal? {
        return if (calculatorState.sourceTickerCode == Currency.Bitcoin.code) {

            // source is bitcoin
            calculatorState.sourceValue

        } else {

            // convert source to bitcoin
            val ticker = tickerMap.get(calculatorState.sourceTickerCode)
            if (ticker != null) {
                ExchangeUtils.convertToBitcoin(calculatorState.sourceValue, ticker)

            } else {
                null
            }
        }
    }

    fun getPrice(ticker: Ticker, calculatorState: CalculatorState, tickers: Map<String, Ticker>): BigDecimal? {
        return getPrice(ticker, calculatorState, ExchangeState(tickers))
    }

    fun getPrice(ticker: Ticker, calculatorState: CalculatorState, exchangeState: ExchangeState): BigDecimal? {
        return if (calculatorState.sourceTickerCode == ticker.code) {
            calculatorState.sourceValue

        } else {
            val bitcoinPrice = getBitcoinPrice(calculatorState, exchangeState)
            if (bitcoinPrice != null) {
                ExchangeUtils.convertFromBitcoin(bitcoinPrice, ticker)

            } else {
                null
            }
        }
    }


//    fun getFiatPrice(ticker: Ticker, calculatorState: CalculatorState, tickers: Map<String, Ticker>): BigDecimal? {
//        return getFiatPrice(ticker, calculatorState, ExchangeState(tickers))
//    }
//
//    fun getFiatPrice(ticker: Ticker, calculatorState: CalculatorState, exchangeState: ExchangeState): BigDecimal? {
//        return if (calculatorState.convertFromBitcoin) {
//            ExchangeUtils.convertFromBitcoin(calculatorState.sourceValue, ticker)
//
//        } else if (calculatorState.targetTickerCode != null) {
//            if (calculatorState.targetTickerCode == ticker.code) {
//                calculatorState.sourceValue
//
//            } else {
//                val bitcoinPrice = getBitcoinPrice(calculatorState, exchangeState)
//                if (bitcoinPrice != null) {
//                    ExchangeUtils.convertFromBitcoin(bitcoinPrice, ticker)
//
//                } else {
//                    null
//                }
//            }
//
//        } else {
//            null
//        }
//    }
}