package io.chthonic.igdb.poc.ui.view

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import io.chthonic.igdb.poc.data.model.CurrencyRealtimeFormatInput
import io.chthonic.igdb.poc.utils.TextUtils
import io.reactivex.subjects.PublishSubject
import timber.log.Timber

/**
 * Created by jhavatar on 4/4/2018.
 */
class CurrencyInputWatcher(val inputView: EditText, val inputChangePublisher: PublishSubject<String>, val maxLength: Int) : TextWatcher {
    var inputState: CurrencyRealtimeFormatInput = CurrencyRealtimeFormatInput.factoryReset()
            .copy(maxLength = maxLength)


    override fun afterTextChanged(editable: Editable?) {
        val s = editable?.toString() ?: return

        inputView.removeTextChangedListener(this)

        inputState = inputState.copy(s = s)
        val output = TextUtils.realtimeFormatInput(inputState)
//        Timber.d("afterTextChanged: output = $output")

        if (!output.doNothing) {
            inputView.setText(output.sFormatted)
            inputView.setSelection(output.caretPos)
        }

        inputView.addTextChangedListener(this)

        if (!(output.doNothing || output.revert)) {
            inputChangePublisher.onNext(output.sRaw)
        }
    }

    fun updateInputType(decimalDigits: Int) {
        inputState.copy(decimalDigits = decimalDigits)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        val caretPos = inputView.selectionStart
        val sPrev = s?.toString() ?: ""
        val changed = if (count > 0) s?.substring(start, start + count) ?: "" else inputState.changed
        inputState = inputState.copy(sPrev = sPrev,
                changed = changed,
                caretPos = caretPos
                )
//        Timber.d("beforeTextChanged: start = $start, count = $count, after = $after, inputState = $inputState")
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val delAction = (s?.length ?: 0) < inputState.sPrev.length
        val changed = if (!delAction) s?.substring(start, start + count) ?: "" else inputState.changed
        inputState = inputState.copy(delAction = delAction,
                changed = changed)
//        Timber.d("onTextChanged: start = $start, count = $count, before = $before, inputState = $inputState")
    }
}