package io.chthonic.igdb.poc.data.model

/**
 * Created by jhavatar on 4/14/2018.
 */
data class CurrencyRealtimeFormatOutput(val sFormatted: String = "",
                                        val sRaw: String = "",
                                        val caretPos: Int = 0,
                                        val doNothing: Boolean = true,
                                        val revert: Boolean = false) {
}