package com.openclassrooms.realestatemanager.utils

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

private const val MIN_SCALE = 0.90f

class ZoomOutPageTransformer : ViewPager2.PageTransformer {
    override fun transformPage(view: View, position: Float) {
        view.apply {
            val pageWidth = width
            val pageHeight = height
            if(position <= 1){
                val scaleFactor = MIN_SCALE.coerceAtLeast(1 - abs(position))
                val vMargin = pageHeight * (1 - scaleFactor) / 2
                val hMargin = pageWidth * (1 - scaleFactor) / 2

                translationX = if (position < 0) {
                    hMargin - vMargin / 2
                } else {
                    hMargin + vMargin / 2
                }

                scaleX = scaleFactor
                scaleY = scaleFactor
            }
        }
    }
}