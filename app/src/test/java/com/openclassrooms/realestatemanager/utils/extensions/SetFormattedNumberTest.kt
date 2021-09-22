package com.openclassrooms.realestatemanager.utils.extensions

import com.google.android.material.textfield.TextInputEditText
import junit.framework.TestCase
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify

class SetFormattedNumberTest: TestCase() {

    @Test
    fun testTwoDigitAndGetFormatted() {
        val inputEditText = mock(TextInputEditText::class.java)

        inputEditText.setFormattedNumber("12")

        verify(inputEditText).setText("12")
    }

    @Test
    fun testFourDigitAndGetFormatted() {
        val inputEditText = mock(TextInputEditText::class.java)
        `when`(inputEditText.selectionStart).thenReturn(4)

        inputEditText.setFormattedNumber("1234")

        verify(inputEditText).setText("1 234")
        verify(inputEditText).setSelection(5)
    }

    @Test
    fun testEightDigitAndGetFormatted() {
        val inputEditText = mock(TextInputEditText::class.java)

        inputEditText.setFormattedNumber("12345678")

        verify(inputEditText).setText("12 345 678")
    }

    @Test
    fun testEightDigitWithSpacesAndGetFormatted() {
        val inputEditText = mock(TextInputEditText::class.java)
        `when`(inputEditText.selectionStart).thenReturn(10)

        inputEditText.setFormattedNumber("1 234 5678")

        verify(inputEditText).setText("12 345 678")
        verify(inputEditText).setSelection(10)
    }

    @Test
    fun testTenDigitWithSpacesAndGetFormatted() {
        val inputEditText = mock(TextInputEditText::class.java)
        `when`(inputEditText.selectionStart).thenReturn(12)

        inputEditText.setFormattedNumber("123 456 7890")

        verify(inputEditText).setText("1 234 567 890")
        verify(inputEditText).setSelection(13)
    }
}