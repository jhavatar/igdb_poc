package io.chthonic.igdb.poc.ui.model

/**
 * Created by jhavatar on 3/30/2018.
 */
data class CalculationViewModel(
//        val bitcoinPrice: String,
//                                val convertFromBitcoin: Boolean,
                                val leftTickerIsSource: Boolean,
                                val leftTicker: TickerViewModel?,
                                val rightTicker: TickerViewModel?,
                                val forceSet: Boolean) {
}