package io.chthonic.igdb.poc.data.model

import java.math.BigDecimal

/**
 * Created by jhavatar on 4/2/2018.
 */
data class CalculatorSerializableState(val leftTickerCode: String,
                                       val rightTickerCode: String?,
                                       val leftTickerIsSource: Boolean,
                                       val sourceValue: String) {

    companion object {
        fun fromCalculatorState(state: CalculatorState): CalculatorSerializableState {
            return CalculatorSerializableState(state.leftTickerCode, state.rightTickerCode, state.leftTickerIsSource, state.sourceValue.toString())
        }
    }

    fun toCalculatorState(): CalculatorState {
        return CalculatorState(leftTickerCode, rightTickerCode, leftTickerIsSource, BigDecimal(sourceValue))
    }
}