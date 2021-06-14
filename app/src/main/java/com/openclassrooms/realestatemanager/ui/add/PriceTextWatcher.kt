package com.openclassrooms.realestatemanager.ui.add

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.text.DecimalFormat

class PriceTextWatcher(private val mEditText: EditText) : TextWatcher {

    private val df = DecimalFormat("#,###")

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
        mEditText.removeTextChangedListener(this)

        val iniLen: Int = mEditText.text.length
            val v = s.toString().replace(df.decimalFormatSymbols.groupingSeparator.toString(), "")
            val n = df.parse(v)
            val cp: Int = mEditText.selectionStart
            mEditText.setText(df.format(n))


        val endLen: Int = mEditText.text.length
            val sel = cp + (endLen - iniLen)
            if (sel > 0 && sel <= mEditText.text.length) {
                mEditText.setSelection(sel)
            } else {
                mEditText.setSelection(mEditText.text.length - 1)
            }

        mEditText.addTextChangedListener(this)

    }
}