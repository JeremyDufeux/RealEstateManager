package com.openclassrooms.realestatemanager.utils.extensions

import android.R.attr.state_checked
import android.content.res.ColorStateList
import android.graphics.BlendMode
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.widget.RadioButton

fun RadioButton.setCircleColor(color: Int){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP  ) {
        val colorStateList = ColorStateList(
            arrayOf(
                intArrayOf(-state_checked),
                intArrayOf(state_checked)
            ), intArrayOf(
                Color.GRAY,
                color
            )
        )

        buttonTintList = colorStateList

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            buttonTintBlendMode = BlendMode.SRC_IN
        } else {
            buttonTintMode = PorterDuff.Mode.SRC_IN
        }
    }
}