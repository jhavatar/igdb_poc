package io.chthonic.igdb.poc.data.model

/**
 * Created by jhavatar on 4/1/2018.
 */
sealed class Currency(val code: String,
                      val decimalDigits: Int) {

    companion object {
        val values: List<Currency> by lazy {
            Currency::class.nestedClasses.filter { it.objectInstance is Currency }.map { it.objectInstance as Currency }
        }

        const val BITCOIN_DECIMAL_DIGITS = 8
        const val FIAT_DECIMAL_DIGITS = 2
        const val ETHEREUM_DECIMAL_DIGITS = 8
    }

    object Bitcoin: Currency("XBT", BITCOIN_DECIMAL_DIGITS)
    object Ethereum: Currency("ETH", ETHEREUM_DECIMAL_DIGITS)
    object Zar: Currency("ZAR", FIAT_DECIMAL_DIGITS)
    object Myr: Currency("MYR", FIAT_DECIMAL_DIGITS)
    object Idr: Currency("IDR", FIAT_DECIMAL_DIGITS)
    object Ngn: Currency("NGN", FIAT_DECIMAL_DIGITS)
}