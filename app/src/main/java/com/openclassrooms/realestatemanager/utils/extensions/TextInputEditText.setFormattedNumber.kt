package com.openclassrooms.realestatemanager.utils.extensions

import com.google.android.material.textfield.TextInputEditText

fun TextInputEditText.setFormattedNumber(number: String){
    var selection = selectionStart

    val initialSpaceCount =  number.filter{ it == ' ' }.count()

    val formattedText = number
        .replace(" ", "")
        .reversed()
        .chunked(3)
        .joinToString(" ")
        .reversed()

    val spaceCount =  formattedText.filter{ it == ' ' }.count()

    if (formattedText != text.toString()) {
        setText(formattedText)
    }

    if(spaceCount > initialSpaceCount){
        selection++
    }

    if(selection > text.toString().length) {
        setSelection(text.toString().length)
    } else {
        setSelection(selection)
    }
}